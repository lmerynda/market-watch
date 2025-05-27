package com.lmerynda.market_watch.services;

import io.polygon.kotlin.sdk.rest.PolygonRestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class OptionsAggregatesServiceTest {

    private OptionsAggregatesService optionsAggregatesService;
    private PolygonRestClient polygonClient;

    // Replace with your actual API key or use environment variable
    private static final String API_KEY = "3HrbQuqNplv2NvnBG_ZDPpmd0ogyuT6B";

    @BeforeEach
    void setUp() {
        optionsAggregatesService = new OptionsAggregatesService();
        polygonClient = new PolygonRestClient(API_KEY);
    }

    @Test
    @DisplayName("Test: Get Hourly Options Prices")
    void testGetHourlyOptionsPrices() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("RUNNING TEST: Get Hourly Options Prices");
        System.out.println("=".repeat(80));

        try {
            optionsAggregatesService.getHourlyOptionsPrices(polygonClient);
            System.out.println("✅ Test completed successfully");
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=".repeat(80));
    }

    @Test
    @DisplayName("Test: Get All SPY Options 15 Minute Data")
    void testGetAllSPYOptions15MinuteData() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("RUNNING TEST: Get All SPY Options 15 Minute Data");
        System.out.println("=".repeat(80));

        try {
            optionsAggregatesService.getAllSPYOptions15MinuteData(polygonClient);
            System.out.println("✅ Test completed successfully");
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=".repeat(80));
    }

    @Test
    @DisplayName("Integration Test: Run All Aggregates Methods")
    void testAllAggregatesMethods() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("RUNNING INTEGRATION TEST: All Aggregates Methods");
        System.out.println("=".repeat(80));

        try {
            System.out.println("1/2: Testing Hourly Options Prices...");
            optionsAggregatesService.getHourlyOptionsPrices(polygonClient);

            System.out.println("\n" + "-".repeat(80));
            System.out.println("2/2: Testing 15-Minute SPY Options Data...");
            optionsAggregatesService.getAllSPYOptions15MinuteData(polygonClient);

            System.out.println("✅ All aggregates tests completed successfully");
        } catch (Exception e) {
            System.err.println("❌ Integration test failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=".repeat(80));
    }
}
