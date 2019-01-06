package de.de.kostalconnector;

public enum ModbusAddress {

    BATTERY_CHARGE_STATE(0xD2),
    CONSUMPTION_BATTERY_CURRENT(0x6A),
    CONSUMPTION_GRID_CURRENT(0x6C),
    CONSUMPTION_PV_CURRENT(0x74),
    POWER_PHASE_1(0x9C),

    POWER_PHASE_2(0xA2),
    POWER_PHASE_3(0xA8);
    private int address;

    private ModbusAddress(int address) {
        this.address = address;
    }

    public int getAddress() {
        return address;
    }
}
