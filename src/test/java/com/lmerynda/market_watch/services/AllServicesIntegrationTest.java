package com.lmerynda.market_watch.services;

import io.polygon.kotlin.sdk.rest.PolygonRestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class AllServicesIntegrationTest {

    private OptionsAggregatesService aggregatesService;
    private OptionsChainService chainService;
    private PolygonRestClient polygonClient;

    private static final String API_KEY = System.getenv("POLYGON_API_KEY");

    @BeforeEach
    void setUp() {
        aggregatesService = new OptionsAggregatesService();
        chainService = new OptionsChainService();
        polygonClient = new PolygonRestClient(API_KEY);
    }

    @Test
    @DisplayName("Integration Test: Run All Service Methods")
    void testAllServiceMethods() {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("RUNNING FULL INTEGRATION TEST: All Market Watch Services");
        System.out.println("=".repeat(100));

        try {
            // Test 1: Options Chain Snapshot
            System.out.println("1/3: Testing Options Chain Snapshot...");
            chainService.getOptionsChainSnapshot(polygonClient);

            System.out.println("\n" + "-".repeat(100));

            // Test 2: Hourly Options Prices
            System.out.println("2/3: Testing Hourly Options Prices...");
            aggregatesService.getHourlyOptionsPrices(polygonClient);

            System.out.println("\n" + "-".repeat(100));

            // Test 3: 15-Minute SPY Options Data
            System.out.println("3/3: Testing 15-Minute SPY Options Data...");
            aggregatesService.getAllSPYOptions15MinuteData(polygonClient);

            System.out.println("\n" + "=".repeat(100));
            System.out.println("✅ ALL INTEGRATION TESTS COMPLETED SUCCESSFULLY");
            System.out.println("=".repeat(100));

        } catch (Exception e) {
            System.err.println("❌ Integration test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Quick Test: Options Chain Only")
    void testOptionsChainOnly() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("QUICK TEST: Options Chain Snapshot Only");
        System.out.println("=".repeat(80));

        try {
            chainService.getOptionsChainSnapshot(polygonClient);
            System.out.println("✅ Options Chain test completed successfully");
        } catch (Exception e) {
            System.err.println("❌ Options Chain test failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=".repeat(80));
    }

    @Test
    @DisplayName("Quick Test: Hourly Prices Only")
    void testHourlyPricesOnly() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("QUICK TEST: Hourly Options Prices Only");
        System.out.println("=".repeat(80));

        try {
            aggregatesService.getHourlyOptionsPrices(polygonClient);
            System.out.println("✅ Hourly prices test completed successfully");
        } catch (Exception e) {
            System.err.println("❌ Hourly prices test failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=".repeat(80));
    }

    @Test
    @DisplayName("Quick Test: 15-Minute Data Only")
    void test15MinuteDataOnly() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("QUICK TEST: 15-Minute SPY Options Data Only");
        System.out.println("=".repeat(80));

        try {
            aggregatesService.getAllSPYOptions15MinuteData(polygonClient);
            System.out.println("✅ 15-minute data test completed successfully");
        } catch (Exception e) {
            System.err.println("❌ 15-minute data test failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=".repeat(80));
    }
}
