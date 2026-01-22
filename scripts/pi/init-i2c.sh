#!/bin/bash

echo "Initializing I2C..."
# Set all ports to input
### IODIR PORT A, alle in
i2cset -y 1 0x20 0x00 0xff

### IODIR PORT B, alle in
i2cset -y 1 0x20 0x01 0xff

### IPOLA PORT A invertieren
i2cset -y 1 0x20 0x02 0xff

### IPOLB PORT B invertieren
i2cset -y 1 0x20 0x03 0xff

### Pull Up PORT A aktivieren
i2cset -y 1 0x20 0x0C 0xff

### Pull Up PORT B aktivieren
i2cset -y 1 0x20 0x0D 0xff

# Set all ports to output
### IODIR PORT A alle out
i2cset -y 1 0x24 0x00 0x00

### IODIR PORT B alle out
i2cset -y 1 0x24 0x01 0x00

echo "Done!"