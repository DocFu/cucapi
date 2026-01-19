package de.plasmawolke.cucapi.hap.accessories;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.plasmawolke.cucapi.Version;
import io.github.hapjava.accessories.LightbulbAccessory;

public abstract class BaseLightbulb implements LightbulbAccessory {

    private static final Logger logger = LoggerFactory.getLogger(BaseLightbulb.class);

    private int id;

    private String name;

    public BaseLightbulb(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public CompletableFuture<String> getName() {
        return CompletableFuture.completedFuture(name);
    }

    @Override
    public void identify() {
        logger.info("Hello from " + id + " " + name);
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
        return "BaseLightbulb{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}
