/*
 *  Copyright 2017 Nord Pool.
 *  This library is intended to aid integration with Nord Pool’s Intraday API and comes without any warranty. Users of this library are responsible for separately testing and ensuring that it works according to their own standards.
 *  Please send feedback to idapi@nordpoolgroup.com.
 */

package com.nordpool.intraday.publicapi.example;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.nordpool.intraday.publicapi.example.config.ExampleAppConfig;
import org.springframework.boot.SpringApplication;

@SpringBootApplication
public class PublicApiApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ExampleAppConfig.class);
        app.run(args);
    }
}
