package de.plasmawolke.cucapi;

import com.beust.jcommander.Parameter;

public class AppArguments {

    @Parameter(names = { "-h", "--host" }, description = "The inet address of the bridge, e.g. 192.168.23.138")
    private String address = null;

    @Parameter(names = { "-p", "--port" }, description = "The port of the bridge, e.g. 9123")
    private int port = 9123;

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "AppArguments{" +
                "address='" + address + '\'' +
                ", port=" + port +
                '}';
    }

}
