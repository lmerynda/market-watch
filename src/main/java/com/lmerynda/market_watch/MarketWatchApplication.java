package com.lmerynda.market_watch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.lmerynda.market_watch.services.OptionsAggregatesService;

import io.polygon.kotlin.sdk.rest.PolygonRestClient;

@SpringBootApplication
public class MarketWatchApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(MarketWatchApplication.class, args);

        // Get API key from environment variable
        String apiKey = System.getenv("POLYGON_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("POLYGON_API_KEY environment variable is not set");
        }

        PolygonRestClient client = new PolygonRestClient(apiKey);
        // client.getTechnicalIndicatorSMA("SPY", 50); // TODO: Fix SMA parameters

        // Get service beans from Spring context
        OptionsAggregatesService aggregatesService = context.getBean(OptionsAggregatesService.class);
        // OptionsChainService chainService =
        // context.getBean(OptionsChainService.class);

        // Example: Get hourly average contract price for SPY options
        // aggregatesService.getHourlyOptionsPrices(client);

        // Example: Get entire options chain for SPY
        // chainService.getOptionsChainSnapshot(client);

        // Example: Get 15-minute data for all SPY options
        aggregatesService.getAllSPYOptions15MinuteData(client);
    }
}
