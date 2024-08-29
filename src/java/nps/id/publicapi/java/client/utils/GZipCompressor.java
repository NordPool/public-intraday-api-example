package nps.id.publicapi.java.client.utils;

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

public class GZipCompressor {
    private static final Logger logger = LogManager.getLogger(GZipCompressor.class);

    public static String compress(String val) throws IOException {
        final byte[] data = val.getBytes(StandardCharsets.UTF_8);
        byte[] compress = compress(data);
        return new String(compress, StandardCharsets.UTF_8);
    }

    public static byte[] compress(final byte[] data) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(outputStream)) {
            gzip.write(data);
            gzip.finish();
            byte[] compress = outputStream.toByteArray();
            return Base64.getEncoder().encode(compress);
        }
    }

    public static String decompress(String val) throws IOException {
        final byte[] bytes = val.getBytes(StandardCharsets.UTF_8);
        byte[] decode = decompress(bytes);
        return new String(decode, StandardCharsets.UTF_8);
    }

    public static byte[] decompress(final byte[] data) throws IOException {
        Long start = System.nanoTime();
        final byte[] decode = Base64.getDecoder().decode(data);
        try (ByteArrayInputStream outputStream = new ByteArrayInputStream(decode);
             GZIPInputStream gzip = new GZIPInputStream(outputStream)) {
            byte[] result = IOUtils.toByteArray(gzip);
            Long end = System.nanoTime();
            logger.info("gzipDecompress: Decompressed " + data.length + " into " + result.length + " bytes in " + (end - start) / 1000 + " microseconds.");
            return result;
        }
    }
}