package de.plasmawolke.cucapi.hap.accessories;

import java.util.concurrent.CompletableFuture;

import de.plasmawolke.cucapi.i2c.MCP27013_PIN;
import io.github.hapjava.accessories.OutletAccessory;
import io.github.hapjava.characteristics.HomekitCharacteristicChangeCallback;

public class Outlet extends BaseAccessory implements OutletAccessory {

    HomekitCharacteristicChangeCallback inUseCallback;

    public Outlet(int hapId, MCP27013_PIN pin) {
        super(hapId, pin);
    }

    @Override
    public CompletableFuture<Boolean> getOutletInUse() {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public void subscribeOutletInUse(HomekitCharacteristicChangeCallback callback) {
        this.inUseCallback = callback;
    }

    @Override
    public void unsubscribeOutletInUse() {
        this.inUseCallback = null;
    }

}
