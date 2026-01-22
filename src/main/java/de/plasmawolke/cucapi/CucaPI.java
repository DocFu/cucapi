package de.plasmawolke.cucapi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

import de.plasmawolke.cucapi.hap.HomekitService;
import de.plasmawolke.cucapi.hap.accessories.Lightbulb;
import de.plasmawolke.cucapi.hap.accessories.Outlet;
import de.plasmawolke.cucapi.i2c.I2C;
import de.plasmawolke.cucapi.i2c.MCP27013_PIN;
import io.github.hapjava.accessories.HomekitAccessory;

public class CucaPI {

    private static final Logger logger = LoggerFactory.getLogger(CucaPI.class);

    public static void main(String[] args) {

        logger.info("Running CucaPI " + Version.getVersionAndRevision() + "...");

        AppArguments appArguments = new AppArguments();
        JCommander commander = JCommander.newBuilder()
                .addObject(appArguments)
                .build();
        try {
            commander.parse(args);
        } catch (Exception e) {
            commander.usage();
            exitWithError("Invalid command line arguments detected: " + e.getMessage());
        }

        I2C.getInstance().init();

        // Start periodic pin state updates every 1 second
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                I2C.getInstance().updatePinStates(false);
            } catch (Exception e) {
                logger.error("Error updating pin states: " + e.getMessage(), e);
            }
        }, 0, 1, TimeUnit.SECONDS);

        // Shutdown scheduler on application exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down pin state update scheduler...");
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }));

        List<HomekitAccessory> accessories = createAccessories();

        HomekitService homekitService = new HomekitService(Util.getInetAddress(appArguments.getAddress()),
                appArguments.getPort());
        try {
            homekitService.runWithAccessories(accessories);
        } catch (Exception e) {
            exitWithError("Error while starting HomekitService: " + e.getMessage());
        }

        I2C.getInstance().runTest();

    }

    private static List<HomekitAccessory> createAccessories() {
        List<HomekitAccessory> accessories = new ArrayList<>();
        HomekitAccessory accessory = null;

        accessory = new Lightbulb(10, MCP27013_PIN.A0);
        accessories.add(accessory);

        accessory = new Lightbulb(11, MCP27013_PIN.A1);
        accessories.add(accessory);

        accessory = new Outlet(12, MCP27013_PIN.B0);
        accessories.add(accessory);

        accessory = new Outlet(13, MCP27013_PIN.B1);
        accessories.add(accessory);

        // TODO add more accessories

        return accessories;
    }

    private static void exitWithError(String message) {
        if (StringUtils.isBlank(message)) {
            logger.error("Exiting with undefined error.");
        } else {
            logger.error(message);
        }

        logger.info("Exiting with error.");
        System.exit(1);
    }

}
