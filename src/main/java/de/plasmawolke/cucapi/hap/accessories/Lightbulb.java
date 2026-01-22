package de.plasmawolke.cucapi.hap.accessories;

import java.util.concurrent.CompletableFuture;

import de.plasmawolke.cucapi.i2c.MCP27013_PIN;
import io.github.hapjava.accessories.LightbulbAccessory;
import io.github.hapjava.characteristics.HomekitCharacteristicChangeCallback;

public class Lightbulb extends BaseAccessory implements LightbulbAccessory {

    public Lightbulb(int hapId, MCP27013_PIN pin) {
        super(hapId, pin);
    }

    @Override
    public CompletableFuture<Boolean> getLightbulbPowerState() {
        return getPowerState();
    }

    @Override
    public CompletableFuture<Void> setLightbulbPowerState(boolean powerState) throws Exception {
        return setPowerState(powerState);
    }

    @Override
    public void subscribeLightbulbPowerState(HomekitCharacteristicChangeCallback callback) {
        subscribePowerState(callback);
    }

    @Override
    public void unsubscribeLightbulbPowerState() {
        unsubscribePowerState();
    }

}
