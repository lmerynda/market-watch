package com.lmerynda.market_watch.services;

import io.polygon.kotlin.sdk.rest.PolygonRestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class OptionsChainServiceTest {

    private OptionsChainService optionsChainService;
    private PolygonRestClient polygonClient;

    private static final String API_KEY = System.getenv("POLYGON_API_KEY");

    @BeforeEach
    void setUp() {
        optionsChainService = new OptionsChainService();
        polygonClient = new PolygonRestClient(API_KEY);
    }

    @Test
    @DisplayName("Test: Get Options Chain Snapshot")
    void testGetOptionsChainSnapshot() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("RUNNING TEST: Get Options Chain Snapshot");
        System.out.println("=".repeat(80));

        try {
            optionsChainService.getOptionsChainSnapshot(polygonClient);
            System.out.println("✅ Test completed successfully");
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("=".repeat(80));
    }
}
