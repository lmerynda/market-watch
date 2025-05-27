package com.lmerynda.market_watch.services;

import io.polygon.kotlin.sdk.rest.PolygonRestClient;
import io.polygon.kotlin.sdk.rest.AggregateDTO;
import io.polygon.kotlin.sdk.rest.AggregatesDTO;
import io.polygon.kotlin.sdk.rest.AggregatesParameters;
import io.polygon.kotlin.sdk.rest.AggregatesParametersBuilder;
import io.polygon.kotlin.sdk.rest.ComparisonQueryFilterParameters;
import io.polygon.kotlin.sdk.rest.options.SnapshotChainParametersBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class OptionsAggregatesService {

    public void getHourlyOptionsPrices(PolygonRestClient client) {
        try {
            // Example options ticker: SPY call option expiring 12/19/2025 with $650 strike
            // Format: O:SPY251219C00650000
            String optionsTicker = "O:SPY251219C00650000";

            // Set date range for the last 5 trading days
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(2);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // Build parameters for hourly aggregates
            AggregatesParameters params = new AggregatesParametersBuilder()
                    .ticker(optionsTicker)
                    .multiplier(1) // 1 hour intervals
                    .timespan("hour") // hourly data
                    .fromDate(startDate.format(formatter)) // start date
                    .toDate(endDate.format(formatter)) // end date
                    .sort("asc") // ascending order
                    .limit(50) // limit results
                    .build();

            System.out.println("Fetching hourly options data for: " + optionsTicker);
            System.out.println("Date range: " + startDate + " to " + endDate);

            // Make the API call
            AggregatesDTO response = client.getAggregatesBlocking(params);

            if (response.getResults() != null && !response.getResults().isEmpty()) {
                List<AggregateDTO> aggregates = response.getResults();
                System.out.println("\nFound " + aggregates.size() + " hourly data points:");
                System.out.println("Timestamp\t\t\tOpen\tHigh\tLow\tClose\tVolume\tAvg Price");
                System.out.println("-------------------------------------------------------------------------");

                for (AggregateDTO agg : aggregates) {
                    // Calculate average price from OHLC
                    double avgPrice = (agg.getOpen() + agg.getHigh() + agg.getLow() + agg.getClose()) / 4.0;

                    // Convert timestamp to readable format
                    java.time.Instant instant = java.time.Instant.ofEpochMilli(agg.getTimestampMillis());
                    java.time.LocalDateTime dateTime = java.time.LocalDateTime.ofInstant(instant,
                            java.time.ZoneId.of("America/New_York"));

                    System.out.printf("%s\t%.2f\t%.2f\t%.2f\t%.2f\t%.0f\t%.2f%n",
                            dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                            agg.getOpen(),
                            agg.getHigh(),
                            agg.getLow(),
                            agg.getClose(),
                            agg.getVolume(),
                            avgPrice);
                }

                // Calculate overall average for the period
                double totalAvg = aggregates.stream()
                        .mapToDouble(agg -> (agg.getOpen() + agg.getHigh() + agg.getLow() + agg.getClose()) / 4.0)
                        .average()
                        .orElse(0.0);

                System.out.println(
                        "\nOverall average contract price for the period: $" + String.format("%.2f", totalAvg));

            } else {
                System.out.println("No data found for the specified options ticker and date range.");
                System.out.println("This could be due to:");
                System.out.println("1. The option doesn't exist or has expired");
                System.out.println("2. No trading activity during the specified period");
                System.out.println("3. Weekend/holiday period with no market data");
            }

        } catch (Exception e) {
            System.err.println("Error fetching options data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void getAllSPYOptions15MinuteData(PolygonRestClient client) {
        try {
            String underlyingTicker = "SPY";

            System.out.println("\n" + "=".repeat(80));
            System.out.println("Fetching 15-minute data for all SPY options (last 7 days)");
            System.out.println("=".repeat(80));

            // Step 1: Get the entire options chain to get all contract tickers
            var parameters = new SnapshotChainParametersBuilder()
                    .strikePrice(ComparisonQueryFilterParameters.equal(0.0))
                    .expirationDate(ComparisonQueryFilterParameters.equal(""))
                    .contractType("")
                    .order("asc")
                    .limit(1000)
                    .sort("strike_price")
                    .build();
            var snapshot = client.getOptionsClient().getSnapshotChainBlocking(underlyingTicker, parameters);

            if (snapshot.getResults() == null || snapshot.getResults().isEmpty()) {
                System.out.println("No options contracts found for " + underlyingTicker);
                return;
            }

            var contracts = snapshot.getResults();
            System.out.println("Found " + contracts.size() + " options contracts");

            // Set date range for the last 7 days
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(7);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            System.out.println("Date range: " + startDate + " to " + endDate);
            System.out.println("Processing contracts (showing first 10 with data)...\n");

            int processedCount = 0;
            int contractsWithData = 0;

            // Step 2: Get 15-minute aggregates for each contract (limit to first 10 for
            // demonstration)
            for (var contract : contracts) {
                if (contractsWithData >= 10)
                    break; // Limit output for demonstration

                try {
                    var details = contract.getDetails();
                    String optionsTicker = details.getTicker();

                    // Build parameters for 15-minute aggregates
                    AggregatesParameters params = new AggregatesParametersBuilder()
                            .ticker(optionsTicker)
                            .multiplier(15) // 15 minute intervals
                            .timespan("minute") // minute data with 15 multiplier
                            .fromDate(startDate.format(formatter))
                            .toDate(endDate.format(formatter))
                            .sort("asc")
                            .limit(100) // Limit to avoid too much data
                            .build();

                    // Make the API call
                    AggregatesDTO response = client.getAggregatesBlocking(params);

                    if (response.getResults() != null && !response.getResults().isEmpty()) {
                        List<AggregateDTO> aggregates = response.getResults();
                        contractsWithData++;

                        System.out.println("Contract: " + optionsTicker);
                        System.out.println("Found " + aggregates.size() + " 15-minute intervals");
                        System.out.println("Timestamp\t\t\tOpen\tHigh\tLow\tClose\tVolume\tAvg Price");
                        System.out.println("-".repeat(85));

                        // Show first 3 intervals for each contract
                        aggregates.stream()
                                .limit(3)
                                .forEach(agg -> {
                                    double avgPrice = (agg.getOpen() + agg.getHigh() + agg.getLow() + agg.getClose())
                                            / 4.0;

                                    java.time.Instant instant = java.time.Instant
                                            .ofEpochMilli(agg.getTimestampMillis());
                                    java.time.LocalDateTime dateTime = java.time.LocalDateTime.ofInstant(instant,
                                            java.time.ZoneId.of("America/New_York"));

                                    System.out.printf("%s\t%.2f\t%.2f\t%.2f\t%.2f\t%.0f\t%.2f%n",
                                            dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                                            agg.getOpen(),
                                            agg.getHigh(),
                                            agg.getLow(),
                                            agg.getClose(),
                                            agg.getVolume(),
                                            avgPrice);
                                });

                        // Calculate average price for this contract
                        double contractAvg = aggregates.stream()
                                .mapToDouble(
                                        agg -> (agg.getOpen() + agg.getHigh() + agg.getLow() + agg.getClose()) / 4.0)
                                .average()
                                .orElse(0.0);

                        System.out.println("Contract Average Price: $" + String.format("%.2f", contractAvg));
                        System.out.println();

                        // Small delay to avoid rate limiting
                        Thread.sleep(100);
                    }

                    processedCount++;
                    if (processedCount % 50 == 0) {
                        System.out.println("Processed " + processedCount + " contracts...");
                    }

                } catch (Exception e) {
                    System.err.println("Error fetching data for contract: " + e.getMessage());
                }
            }

            System.out.println("\nSummary:");
            System.out.println("Total contracts processed: " + processedCount);
            System.out.println("Contracts with 15-minute data: " + contractsWithData);

        } catch (Exception e) {
            System.err.println("Error fetching SPY options data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
