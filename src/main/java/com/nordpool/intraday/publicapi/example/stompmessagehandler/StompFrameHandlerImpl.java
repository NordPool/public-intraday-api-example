/*
 *  Copyright 2017 Nord Pool.
 *  This library is intended to aid integration with Nord Pool’s Intraday API and comes without any warranty. Users of this library are responsible for separately testing and ensuring that it works according to their own standards.
 *  Please send feedback to idapi@nordpoolgroup.com.
 */

package com.nordpool.intraday.publicapi.example.stompmessagehandler;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nordpool.intraday.publicapi.example.service.gzip.MessageZipper;
import com.nordpool.intraday.publicapi.example.service.subscription.Subscription;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class StompFrameHandlerImpl implements StompFrameHandler {
    private static final Logger LOGGER = LogManager.getLogger(StompFrameHandlerImpl.class);
    private final ObjectMapper objectMapper;
    private final MessageZipper messageZipper;
    private Subscription topic;
    public static final String NPS_SEQ_NUM_HEADER = "x-nps-sequenceNo";
    public static final String NPS_SNAPSHOT_HEADER = "x-nps-snapshot";
    public static final String DESTINATION_TOPIC = "destination";
    private final boolean isGzipped;

    public StompFrameHandlerImpl(Subscription subscription) {
        messageZipper = new MessageZipper();
        objectMapper = new ObjectMapper();
        isGzipped = (subscription.getGzipped() != null) ? subscription.getGzipped() : false;
        LOGGER.info("Note: for the topic " + subscription + " compression enabled: " + isGzipped);
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        this.topic = subscription;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return byte[].class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        int truncateAt = 5;
        try {

            List<?> parsedMessage = objectMapper.readValue(unzip((byte[]) payload), getValueType());
            int size = parsedMessage.size();
            LOGGER.info("[Frame " + headers.getFirst(DESTINATION_TOPIC) + ", " + NPS_SEQ_NUM_HEADER + ": " + headers.getFirst(NPS_SEQ_NUM_HEADER) + ", "
                    + NPS_SNAPSHOT_HEADER + ": " + headers.getFirst(NPS_SNAPSHOT_HEADER) + ", size: " + parsedMessage.size() + " entries] -- >\n "
                    + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(parsedMessage.subList(0, size >= truncateAt ? truncateAt : size)));
            if (parsedMessage.size() > truncateAt) {
                LOGGER.info("[Frame] == Warning: output above is truncated, shown " + truncateAt + " messages out of " + size + " ==\n");
            }
        } catch (IOException e) {
            LOGGER.error("Topic" + topic + ": " + e.getMessage(), e);
        }
    }

    private CollectionType getValueType() {
        return objectMapper.getTypeFactory().constructCollectionType(LinkedList.class, topic.getTopic().getDestinationRow());
    }

    /*
      Filter to unzip / un-Base64 the payload.
     */
    private byte[] unzip(byte[] input) throws IOException {
        return isGzipped ? messageZipper.gzipDecompress(input) : input;
    }
}
