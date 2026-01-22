package de.plasmawolke.cucapi.hap.accessories;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.plasmawolke.cucapi.Version;
import de.plasmawolke.cucapi.i2c.I2C;
import de.plasmawolke.cucapi.i2c.MCP27013_PIN;
import io.github.hapjava.accessories.HomekitAccessory;
import io.github.hapjava.characteristics.HomekitCharacteristicChangeCallback;

public abstract class BaseAccessory implements HomekitAccessory {

    private static final Logger logger = LoggerFactory.getLogger(BaseAccessory.class);

    private int hapId;
    private MCP27013_PIN pin;

    public BaseAccessory(int hapId, MCP27013_PIN pin) {
        this.hapId = hapId;
        this.pin = pin;
    }

    private HomekitCharacteristicChangeCallback powerStateCallback;

    public CompletableFuture<Boolean> getPowerState() {
        return CompletableFuture.completedFuture(I2C.getInstance().getPinState(pin));
    }

    public CompletableFuture<Void> setPowerState(boolean powerState) throws Exception {

        if (powerState == I2C.getInstance().getPinState(pin)) {
            return CompletableFuture.completedFuture(null);
        }

        I2C.getInstance().pulse(pin);

        if (powerStateCallback != null) {
            powerStateCallback.changed();
        }
        return CompletableFuture.completedFuture(null);
    }

    public void subscribePowerState(HomekitCharacteristicChangeCallback callback) {
        this.powerStateCallback = callback;
    }

    public void unsubscribePowerState() {
        this.powerStateCallback = null;
    }

    @Override
    public int getId() {
        return hapId;
    }

    @Override
    public CompletableFuture<String> getName() {
        return CompletableFuture.completedFuture(pin.name());
    }

    @Override
    public void identify() {
        logger.info("I am " + hapId + "/" + pin.name());
    }

    @Override
    public CompletableFuture<String> getSerialNumber() {
        return CompletableFuture.completedFuture("SN-1234567890");
    }

    @Override
    public CompletableFuture<String> getModel() {
        return CompletableFuture.completedFuture("CucaPI Lightbulb");
    }

    @Override
    public CompletableFuture<String> getManufacturer() {
        return CompletableFuture.completedFuture("DocFu");
    }

    @Override
    public CompletableFuture<String> getFirmwareRevision() {
        return CompletableFuture.completedFuture(Version.getVersionAndRevision());
    }

    @Override
    public String toString() {
        return "BaseAccessory{" +
                "hapId=" + hapId +
                ", pin=" + pin +
                ", powerState=" + I2C.getInstance().getPinState(pin) +
                '}';
    }
}
