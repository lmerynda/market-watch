package com.lmerynda.market_watch.services;

import io.polygon.kotlin.sdk.rest.PolygonRestClient;
import io.polygon.kotlin.sdk.rest.ComparisonQueryFilterParameters;
import io.polygon.kotlin.sdk.rest.options.SnapshotChainParameters;
import io.polygon.kotlin.sdk.rest.options.SnapshotChainParametersBuilder;
import org.springframework.stereotype.Service;

@Service
public class OptionsChainService {

    public void getOptionsChainSnapshot(PolygonRestClient client) {
        try {
            String underlyingTicker = "SPY";

            System.out.println("\n" + "=".repeat(80));
            System.out.println("Fetching entire options chain for: " + underlyingTicker);
            System.out.println("=".repeat(80));

            // Get the options chain snapshot using blocking call
            // Create parameters for the options chain snapshot
            SnapshotChainParameters params = new SnapshotChainParameters();
            var snapshot = client.getOptionsClient().getSnapshotChainBlocking(underlyingTicker, params);

            if (snapshot.getResults() != null && !snapshot.getResults().isEmpty()) {
                var contracts = snapshot.getResults();
                System.out.println("Found " + contracts.size() + " options contracts:");
                System.out.println();

                // Simple display of first 10 contracts for demonstration
                System.out.println("Sample contracts (showing first 10):");
                System.out.println("Ticker\t\t\t\tBid\tAsk");
                System.out.println("-".repeat(50));

                contracts.stream()
                        .limit(10)
                        .forEach(contract -> {
                            var details = contract.getDetails();
                            var quote = contract.getLastQuote();

                            System.out.printf("%-25s\t%.2f\t%.2f%n",
                                    details.getTicker(),
                                    quote != null && quote.getBid() != null ? quote.getBid() : 0.0,
                                    quote != null && quote.getAsk() != null ? quote.getAsk() : 0.0);
                        });

                // Summary statistics
                var totalVolume = contracts.stream()
                        .filter(c -> c.getDay() != null && c.getDay().getVolume() != null)
                        .mapToDouble(c -> c.getDay().getVolume())
                        .sum();

                var totalOI = contracts.stream()
                        .filter(c -> c.getOpenInterest() != null)
                        .mapToDouble(c -> c.getOpenInterest())
                        .sum();

                System.out.println("\nChain Summary:");
                System.out.println("Total Contracts: " + contracts.size());
                System.out.println("Total Volume: " + String.format("%.0f", totalVolume));
                System.out.println("Total Open Interest: " + String.format("%.0f", totalOI));

            } else {
                System.out.println("No options contracts found for " + underlyingTicker);
            }

        } catch (Exception e) {
            System.err.println("Error fetching options chain: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
