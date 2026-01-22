package de.plasmawolke.cucapi.i2c;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class I2C {

    private static final Logger logger = LoggerFactory.getLogger(I2C.class);

    private static final String hexAddressInput = "0x20";
    private static final String hexAddressOutput = "0x24";

    private static I2C instance;

    private boolean mock = false;
    private boolean initialized = false;

    private Map<MCP27013_PIN, Boolean> pinState = new HashMap<>();

    private I2C() {
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            mock = true;
        }
    }

    public static I2C getInstance() {
        if (instance == null) {
            instance = new I2C();
        }
        return instance;
    }

    public void init() {

        try {

            logger.info("Initializing I2C...");

            // ### IODIR PORT A, alle in
            exec("i2cset -y 1 " + hexAddressInput + " 0x00 0xff");

            // ### IODIR PORT B, alle in
            exec("i2cset -y 1 " + hexAddressInput + " 0x01 0xff");

            // ### IPOLA PORT A invertieren
            exec("i2cset -y 1 " + hexAddressInput + " 0x02 0xff");

            // ### IPOLB PORT B invertieren
            exec("i2cset -y 1 " + hexAddressInput + " 0x03 0xff");

            // ### Pull Up PORT A aktivieren
            exec("i2cset -y 1 " + hexAddressInput + " 0x0C 0xff");

            // ### Pull Up PORT B aktivieren
            exec("i2cset -y 1 " + hexAddressInput + " 0x0D 0xff");

            // ### IODIR PORT A alle out
            exec("i2cset -y 1 " + hexAddressOutput + " 0x00 0x00");

            // ### IODIR PORT B alle out
            exec("i2cset -y 1 " + hexAddressOutput + " 0x01 0x00");

            updatePinStates();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to initialize I2C", e);
        }
        initialized = true;
    }

    public void pulse(MCP27013_PIN pin) {
        if (!initialized) {
            throw new RuntimeException("I2C not initialized");
        }

        try {
            exec("i2cset -y 1 " + hexAddressOutput + " " + pin.getRegister() + " 0x00");
            exec("i2cset -y 1 " + hexAddressOutput + " " + pin.getRegister() + " " + pin.getBit());
            Thread.sleep(100);
            exec("i2cset -y 1 " + hexAddressOutput + " " + pin.getRegister() + " 0x00");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to pulse I2C", e);
        }
    }

    public boolean getPinState(MCP27013_PIN pin) {
        return pinState.getOrDefault(pin, false);
    }

    public void updatePinStates() {
        logger.trace("Updating pin states...");
        try {
            String stateA = exec("i2cget -y 1 " + hexAddressInput + " 0x12", false);
            String stateB = exec("i2cget -y 1 " + hexAddressInput + " 0x13", false);
            int stateAInt = Integer.parseInt(stateA.substring(2), 16);
            int stateBInt = Integer.parseInt(stateB.substring(2), 16);
            pinState.put(MCP27013_PIN.A0, isBitSet(stateAInt, 0));
            pinState.put(MCP27013_PIN.A1, isBitSet(stateAInt, 1));
            pinState.put(MCP27013_PIN.A2, isBitSet(stateAInt, 2));
            pinState.put(MCP27013_PIN.A3, isBitSet(stateAInt, 3));
            pinState.put(MCP27013_PIN.A4, isBitSet(stateAInt, 4));
            pinState.put(MCP27013_PIN.A5, isBitSet(stateAInt, 5));
            pinState.put(MCP27013_PIN.A6, isBitSet(stateAInt, 6));
            pinState.put(MCP27013_PIN.A7, isBitSet(stateAInt, 7));
            pinState.put(MCP27013_PIN.B0, isBitSet(stateBInt, 0));
            pinState.put(MCP27013_PIN.B1, isBitSet(stateBInt, 1));
            pinState.put(MCP27013_PIN.B2, isBitSet(stateBInt, 2));
            pinState.put(MCP27013_PIN.B3, isBitSet(stateBInt, 3));
            pinState.put(MCP27013_PIN.B4, isBitSet(stateBInt, 4));
            pinState.put(MCP27013_PIN.B5, isBitSet(stateBInt, 5));
            pinState.put(MCP27013_PIN.B6, isBitSet(stateBInt, 6));
            pinState.put(MCP27013_PIN.B7, isBitSet(stateBInt, 7));
            logger.trace("Pin states updated: " + pinState);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to update pin states", e);
        }
    }

    public void runTest() {
        logger.info("Running test...");

        try {
            for (MCP27013_PIN pin : MCP27013_PIN.values()) {
                logger.info("Testing pin: " + pin);
                pulse(pin);
                Thread.sleep(3000);
            }

            Thread.sleep(5000);

            for (MCP27013_PIN pin : MCP27013_PIN.values()) {
                logger.info("Testing pin: " + pin);
                pulse(pin);
                Thread.sleep(3000);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to sleep", e);
        }
        logger.info("Test completed.");
    }

    private boolean isBitSet(int value, int bitPosition) {
        return (value & (1 << bitPosition)) != 0;
    }

    private String exec(String command) throws IOException, InterruptedException {
        return exec(command, true);
    }

    private String exec(String command, boolean log) throws IOException, InterruptedException {
        if (log) {
            logger.info("Executing I2C command: " + command);
        }
        if (mock) {
            return "0x00";
        }
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (log) {
                logger.info("I2C: " + line);
            }
        }
        reader.close();
        return line;
    }

}
