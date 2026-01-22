package de.plasmawolke.cucapi.i2c;

public enum MCP27013_PIN {

    A0("0x12", "0x01"),
    A1("0x12", "0x02"),
    A2("0x12", "0x04"),
    A3("0x12", "0x08"),
    A4("0x12", "0x10"),
    A5("0x12", "0x20"),
    A6("0x12", "0x40"),
    A7("0x12", "0x80"),
    B0("0x13", "0x01"),
    B1("0x13", "0x02"),
    B2("0x13", "0x04"),
    B3("0x13", "0x08"),
    B4("0x13", "0x10"),
    B5("0x13", "0x20"),
    B6("0x13", "0x40"),
    B7("0x13", "0x80");

    private final String register;
    private final String bit;

    MCP27013_PIN(String register, String bit) {
        this.register = register;
        this.bit = bit;
    }

    public String getRegister() {
        return register;
    }

    public String getBit() {
        return bit;
    }
}
