package de.plasmawolke.cucapi.hap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.plasmawolke.cucapi.Version;
import de.plasmawolke.cucapi.hap.accessories.Lightbulb;
import io.github.hapjava.server.impl.HomekitRoot;
import io.github.hapjava.server.impl.HomekitServer;

public class HomekitService {

    private static final Logger logger = LoggerFactory.getLogger(HomekitService.class);

    private static final File authFile = new File("cucapi-auth.bin");

    private static final String manufacturer = "https://github.com/DocFu/cucapi";
    private static final String model = "CucaPI";
    private static final String serialNumber = "SN-1234567890";
    private static final String firmwareRevision = Version.getVersionAndRevision();
    private static final String hardwareRevision = "-";

    private int port;
    private InetAddress address;

    public HomekitService(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public void runWithAccessories(Collection<Lightbulb> lightbulbs) throws Exception {

        HomekitServer homekitServer = new HomekitServer(address, port);
        AuthInfo authInfo = createAuthInfo();
        HomekitRoot bridge = homekitServer.createBridge(authInfo, model, 1, manufacturer, model, serialNumber,
                firmwareRevision, hardwareRevision);

        for (Lightbulb lightbulb : lightbulbs) {
            logger.info("Adding HomeKit Accessory: " + lightbulb);
            bridge.addAccessory(lightbulb);
        }

        bridge.start();

        authInfo.onChange(state -> {
            try {
                logger.debug("Updating auth file after state has changed.");
                FileOutputStream fileOutputStream = new FileOutputStream(authFile);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(state);
                objectOutputStream.flush();
                objectOutputStream.close();
            } catch (IOException e) {
                logger.error("Updating auth file has failed!", e);
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stopping homekit service.");
            homekitServer.stop();
        }));

        String pp = authInfo.getPin();

        logger.info("Started homekit service successfully on port " + port + ".");
        logger.info("****************");
        logger.info("**    PIN:    **");
        logger.info("** " + pp + " **");
        logger.info("****************");
    }

    private AuthInfo createAuthInfo() throws Exception {
        AuthInfo authInfo;
        if (authFile.exists()) {
            FileInputStream fileInputStream = new FileInputStream(authFile);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            try {
                logger.debug("Using state from existing auth file.");
                AuthState authState = (AuthState) objectInputStream.readObject();
                authInfo = new AuthInfo(authState);
            } finally {
                objectInputStream.close();
            }
        } else {
            authInfo = new AuthInfo(createRandomPin());
        }

        return authInfo;
    }

    private String createRandomPin() {
        Random random = new Random();

        String number1 = String.valueOf(random.nextInt(999));
        String number2 = String.valueOf(random.nextInt(99));
        String number3 = String.valueOf(random.nextInt(999));

        number1 = StringUtils.leftPad(number1, 3, "0");
        number2 = StringUtils.leftPad(number2, 2, "0");
        number3 = StringUtils.leftPad(number3, 3, "0");

        return number1 + "-" + number2 + "-" + number3;
    }

}
