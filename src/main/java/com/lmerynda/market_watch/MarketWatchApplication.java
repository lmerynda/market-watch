package com.lmerynda.market_watch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.polygon.kotlin.sdk.rest.PolygonRestClient;
import io.polygon.kotlin.sdk.rest.AggregateDTO;
import io.polygon.kotlin.sdk.rest.AggregatesDTO;
import io.polygon.kotlin.sdk.rest.AggregatesParameters;
import io.polygon.kotlin.sdk.rest.AggregatesParametersBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SpringBootApplication
public class MarketWatchApplication {
    private static String apiKey = "3HrbQuqNplv2NvnBG_ZDPpmd0ogyuT6B";

    public static void main(String[] args) {
        PolygonRestClient client = new PolygonRestClient(apiKey);

        // Example: Get hourly average contract price for SPY options
        getHourlyOptionsPrices(client);

        SpringApplication.run(MarketWatchApplication.class, args);
    }

    private static void getHourlyOptionsPrices(PolygonRestClient client) {
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
}
