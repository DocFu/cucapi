#!/bin/bash

echo "Initializing I2C..."
# Set all ports to input
### IODIR PORT A, alle in
i2cset -y 1 0x20 0x00 0xFF

### IODIR PORT B, alle in
i2cset -y 1 0x20 0x01 0xFF

### Pull Up PORT A aktivieren
i2cset -y 1 0x20 0x0C 0xFF

### Pull Up PORT B aktivieren
i2cset -y 1 0x20 0x0D 0xFF

# Set all ports to output
### IODIR PORT A alle out
i2cset -y 1 0x24 0x00 0x00

### IODIR PORT B alle out
i2cset -y 1 0x24 0x01 0x00

echo "Done!"