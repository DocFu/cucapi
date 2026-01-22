package de.plasmawolke.cucapi.i2c;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.plasmawolke.cucapi.hap.accessories.BaseAccessory;

public class I2C {

    private static final Logger logger = LoggerFactory.getLogger(I2C.class);

    private static final String hexAddressInput = "0x20";
    private static final String hexAddressOutput = "0x24";

    private static I2C instance;

    private boolean mock = false;
    private boolean initialized = false;

    private Map<MCP27013_PIN, Boolean> pinState = new HashMap<>();
    private Map<MCP27013_PIN, BaseAccessory> pinAccessory = new HashMap<>();

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

            updatePinStates(true);

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

    public void updatePinStates(boolean log) {
        if (log) {
            logger.info("Updating pin states...");
        }
        try {
            String stateA = exec("i2cget -y 1 " + hexAddressInput + " 0x12", log);
            String stateB = exec("i2cget -y 1 " + hexAddressInput + " 0x13", log);
            int stateAInt = Integer.parseInt(stateA.substring(2), 16);
            int stateBInt = Integer.parseInt(stateB.substring(2), 16);

            Boolean previousPinStateA0 = pinState.put(MCP27013_PIN.A0, isBitSet(stateAInt, 0));
            if (pinAccessory.get(MCP27013_PIN.A0) != null && previousPinStateA0 != pinState.get(MCP27013_PIN.A0)) {
                pinAccessory.get(MCP27013_PIN.A0).updatePowerState();
            }
            Boolean previousPinStateA1 = pinState.put(MCP27013_PIN.A1, isBitSet(stateAInt, 1));
            if (pinAccessory.get(MCP27013_PIN.A1) != null && previousPinStateA1 != pinState.get(MCP27013_PIN.A1)) {
                pinAccessory.get(MCP27013_PIN.A1).updatePowerState();
            }
            Boolean previousPinStateA2 = pinState.put(MCP27013_PIN.A2, isBitSet(stateAInt, 2));
            if (pinAccessory.get(MCP27013_PIN.A2) != null && previousPinStateA2 != pinState.get(MCP27013_PIN.A2)) {
                pinAccessory.get(MCP27013_PIN.A2).updatePowerState();
            }
            Boolean previousPinStateA3 = pinState.put(MCP27013_PIN.A3, isBitSet(stateAInt, 3));
            if (pinAccessory.get(MCP27013_PIN.A3) != null && previousPinStateA3 != pinState.get(MCP27013_PIN.A3)) {
                pinAccessory.get(MCP27013_PIN.A3).updatePowerState();
            }
            Boolean previousPinStateA4 = pinState.put(MCP27013_PIN.A4, isBitSet(stateAInt, 4));
            if (pinAccessory.get(MCP27013_PIN.A4) != null && previousPinStateA4 != pinState.get(MCP27013_PIN.A4)) {
                pinAccessory.get(MCP27013_PIN.A4).updatePowerState();
            }
            Boolean previousPinStateA5 = pinState.put(MCP27013_PIN.A5, isBitSet(stateAInt, 5));
            if (pinAccessory.get(MCP27013_PIN.A5) != null && previousPinStateA5 != pinState.get(MCP27013_PIN.A5)) {
                pinAccessory.get(MCP27013_PIN.A5).updatePowerState();
            }
            Boolean previousPinStateA6 = pinState.put(MCP27013_PIN.A6, isBitSet(stateAInt, 6));
            if (pinAccessory.get(MCP27013_PIN.A6) != null && previousPinStateA6 != pinState.get(MCP27013_PIN.A6)) {
                pinAccessory.get(MCP27013_PIN.A6).updatePowerState();
            }
            Boolean previousPinStateA7 = pinState.put(MCP27013_PIN.A7, isBitSet(stateAInt, 7));
            if (pinAccessory.get(MCP27013_PIN.A7) != null && previousPinStateA7 != pinState.get(MCP27013_PIN.A7)) {
                pinAccessory.get(MCP27013_PIN.A7).updatePowerState();
            }

            Boolean previousPinStateB0 = pinState.put(MCP27013_PIN.B0, isBitSet(stateBInt, 0));
            if (pinAccessory.get(MCP27013_PIN.B0) != null && previousPinStateB0 != pinState.get(MCP27013_PIN.B0)) {
                pinAccessory.get(MCP27013_PIN.B0).updatePowerState();
            }
            Boolean previousPinStateB1 = pinState.put(MCP27013_PIN.B1, isBitSet(stateBInt, 1));
            if (pinAccessory.get(MCP27013_PIN.B1) != null && previousPinStateB1 != pinState.get(MCP27013_PIN.B1)) {
                pinAccessory.get(MCP27013_PIN.B1).updatePowerState();
            }
            Boolean previousPinStateB2 = pinState.put(MCP27013_PIN.B2, isBitSet(stateBInt, 2));
            if (pinAccessory.get(MCP27013_PIN.B2) != null && previousPinStateB2 != pinState.get(MCP27013_PIN.B2)) {
                pinAccessory.get(MCP27013_PIN.B2).updatePowerState();
            }
            Boolean previousPinStateB3 = pinState.put(MCP27013_PIN.B3, isBitSet(stateBInt, 3));
            if (pinAccessory.get(MCP27013_PIN.B3) != null && previousPinStateB3 != pinState.get(MCP27013_PIN.B3)) {
                pinAccessory.get(MCP27013_PIN.B3).updatePowerState();
            }
            Boolean previousPinStateB4 = pinState.put(MCP27013_PIN.B4, isBitSet(stateBInt, 4));
            if (pinAccessory.get(MCP27013_PIN.B4) != null && previousPinStateB4 != pinState.get(MCP27013_PIN.B4)) {
                pinAccessory.get(MCP27013_PIN.B4).updatePowerState();
            }
            Boolean previousPinStateB5 = pinState.put(MCP27013_PIN.B5, isBitSet(stateBInt, 5));
            if (pinAccessory.get(MCP27013_PIN.B5) != null && previousPinStateB5 != pinState.get(MCP27013_PIN.B5)) {
                pinAccessory.get(MCP27013_PIN.B5).updatePowerState();
            }
            Boolean previousPinStateB6 = pinState.put(MCP27013_PIN.B6, isBitSet(stateBInt, 6));
            if (pinAccessory.get(MCP27013_PIN.B6) != null && previousPinStateB6 != pinState.get(MCP27013_PIN.B6)) {
                pinAccessory.get(MCP27013_PIN.B6).updatePowerState();
            }
            Boolean previousPinStateB7 = pinState.put(MCP27013_PIN.B7, isBitSet(stateBInt, 7));
            if (pinAccessory.get(MCP27013_PIN.B7) != null && previousPinStateB7 != pinState.get(MCP27013_PIN.B7)) {
                pinAccessory.get(MCP27013_PIN.B7).updatePowerState();
            }

            if (log) {
                logger.info("Pin states updated: " + pinState);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to update pin states", e);
        }
    }

    public void registerAccessories(Collection<BaseAccessory> accessories) {

        for (BaseAccessory accessory : accessories) {
            pinAccessory.put(accessory.getMCP27013_PIN(), accessory);
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
        String result = "";
        while ((line = reader.readLine()) != null) {
            if (log) {
                logger.info("I2C: " + line);
            }
            result += line;
        }
        reader.close();
        return result.trim();
    }

}
