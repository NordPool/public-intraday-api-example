/*
 *  Copyright 2017 Nord Pool.
 *  This library is intended to aid integration with Nord Pool’s Intraday API and comes without any warranty. Users of this library are responsible for separately testing and ensuring that it works according to their own standards.
 *  Please send feedback to idapi@nordpoolgroup.com.
 */

package com.nordpool.intraday.publicapi.example;

import com.nordpool.intraday.publicapi.example.config.ExampleAppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;

public class PublicApiApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ExampleAppConfig.class);
        app.setWebApplicationType(WebApplicationType.NONE); // We do not use embedded server capabilities of Spring boot
        app.run(args);
    }
}
