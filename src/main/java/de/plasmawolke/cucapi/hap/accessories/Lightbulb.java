package de.plasmawolke.cucapi.hap.accessories;

import java.util.concurrent.CompletableFuture;

import io.github.hapjava.characteristics.HomekitCharacteristicChangeCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lightbulb extends BaseLightbulb {

    private static final Logger logger = LoggerFactory.getLogger(Lightbulb.class);

    public Lightbulb(int id, String name) {
        super(id, name);
    }

    private boolean powerState;
    private HomekitCharacteristicChangeCallback powerStateCallback;

    @Override
    public CompletableFuture<Boolean> getLightbulbPowerState() {
        return CompletableFuture.completedFuture(powerState);
    }

    @Override
    public CompletableFuture<Void> setLightbulbPowerState(boolean powerState) throws Exception {
        logger.info(
                "Setting state of " + getName().get() + "(" + getId() + ") from " + this.powerState + " to "
                        + powerState);
        this.powerState = powerState;
        if (powerStateCallback != null) {
            powerStateCallback.changed();
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void subscribeLightbulbPowerState(HomekitCharacteristicChangeCallback callback) {
        this.powerStateCallback = callback;
    }

    @Override
    public void unsubscribeLightbulbPowerState() {
        // do nothing
    }

}
