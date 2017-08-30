package com.nordpool.intraday.publicapi.example.service.gzip;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Askar.Ibragimov on 2017-08-08.
 */

public class MessageZipper {
    private static final Logger LOGGER = LogManager.getLogger(MessageZipper.class);

    public String gzipCompress(String val) throws IOException {
        final byte[] data = val.getBytes(StandardCharsets.UTF_8);
        byte[] compress = this.gzipCompress(data);
        return new String(compress, StandardCharsets.UTF_8);
    }

    public byte[] gzipCompress(final byte[] data) throws IOException {

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(outputStream)) {
            gzip.write(data);
            gzip.finish();
            byte[] compress = outputStream.toByteArray();
            byte[] reply = Base64.getEncoder().encode(compress);
            return reply;
        }
    }

    public String gzipDecompress(String val) throws IOException {
        final byte[] bytes = val.getBytes(StandardCharsets.UTF_8);
        byte[] decode = this.gzipDecompress(bytes);
        return new String(decode, StandardCharsets.UTF_8);
    }

    public byte[] gzipDecompress(final byte[] data) throws IOException {
        Long start = System.nanoTime();
        final byte[] decode = java.util.Base64.getDecoder().decode(data);
        try (ByteArrayInputStream outputStream = new ByteArrayInputStream(decode);
             GZIPInputStream gzip = new GZIPInputStream(outputStream)) {
            byte[] result = IOUtils.toByteArray(gzip);
            Long end = System.nanoTime();
            LOGGER.info("gzipDecompress: Decompressed " + data.length + " into " + result.length + " bytes in " + (end - start) / 1000 + " microseconds.");
            return result;
        }
    }


}
