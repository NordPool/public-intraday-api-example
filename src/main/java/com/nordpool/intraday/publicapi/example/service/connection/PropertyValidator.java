/*
 *  Copyright 2017 Nord Pool.
 *  This library is intended to aid integration with Nord Pool’s Intraday API and comes without any warranty. Users of this library are responsible for separately testing and ensuring that it works according to their own standards.
 *  Please send feedback to idapi@nordpoolgroup.com.
 */

package com.nordpool.intraday.publicapi.example.service.connection;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class PropertyValidator {
    private static final Logger LOGGER = LogManager.getLogger(PropertyValidator.class);
    @Autowired
    private ConfigurableApplicationContext applicationContext;

    /**
     * Validates app properties. Quits program when property is not defined.
     *
     * @param value
     * @param propertyName
     */

    public void validate(Object value, String propertyName) {
        if (value == null || (value instanceof String && StringUtils.isBlank((String) value))) {
            LOGGER.error(propertyName + " property is empty. Closing application.");
            SpringApplication.exit(applicationContext);
        }
    }
}
