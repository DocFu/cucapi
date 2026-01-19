package de.plasmawolke.cucapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class I2C {

    private static final Logger logger = LoggerFactory.getLogger(I2C.class);

    public static void init() {

        try {
            Process process = Runtime.getRuntime().exec("scripts/test/init-i2c.sh");
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info("I2C: " + line);
            }
            reader.close();

        } catch (IOException | InterruptedException e) {
            logger.error("Failed to initialize I2C: " + e.getMessage());
        }
    }

    public static Map<Integer, String> get() {
        Map<Integer, String> result = new HashMap<>();
        try {
            Process process = Runtime.getRuntime().exec("scripts/test/get-i2c.sh");
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                result.put(index, line);
                index++;
                logger.info("I2C: " + line);
            }
            reader.close();
        } catch (IOException | InterruptedException e) {
            logger.error("Failed to get I2C value: " + e.getMessage());
        }
        return result;
    }

    public static void set(int port, int value) {
        logger.info("Setting I2C value for port " + port + " to " + value);
    }

}
