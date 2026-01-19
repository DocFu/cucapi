package de.plasmawolke.cucapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;

import de.plasmawolke.cucapi.hap.HomekitService;
import de.plasmawolke.cucapi.hap.accessories.Lightbulb;

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

        I2C.init();
        Map<Integer, String> i2cValues = I2C.get();
        logger.info("I2C Values: " + i2cValues);

        List<Lightbulb> lightbulbs = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            Lightbulb lightbulb = new Lightbulb(i + 1, "Lightbulb " + i);
            lightbulbs.add(lightbulb);
        }

        HomekitService homekitService = new HomekitService(Util.getInetAddress(appArguments.getAddress()),
                appArguments.getPort());
        try {
            homekitService.runWithAccessories(lightbulbs);
        } catch (Exception e) {
            exitWithError("Error while starting HomekitService: " + e.getMessage());
        }

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
