package com.oilerrig.vendor.seeder;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder {

    private final CsvSeeder csvSeeder;

    public DataSeeder(CsvSeeder csvSeeder) {
        this.csvSeeder = csvSeeder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seed() throws Exception {
        csvSeeder.seed();
    }
}
