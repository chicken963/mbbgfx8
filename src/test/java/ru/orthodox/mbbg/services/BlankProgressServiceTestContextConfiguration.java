package ru.orthodox.mbbg.services;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class BlankProgressServiceTestContextConfiguration {

    @Bean
    public BlanksProgressService blanksProgressService() {
        return new BlanksProgressService() {
        };
    }
}