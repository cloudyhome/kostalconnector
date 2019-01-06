package de.de.kostalconnector;

import java.io.IOException;


public class KostalConnector {

    private ModbusClient modbusClient = new ModbusClient("scb.localdomain", 1502);



    public static void main(String[] args) {
        KostalConnector kostalConnector = new KostalConnector();
        try {
            kostalConnector.initModbusClient();
            System.out.println("Consumption from grid: " + kostalConnector.getCurrentConsumptionFromGrid());
            System.out.println("Consumption from battery: " + kostalConnector.getCurrentConsumptionFromBattery());

            System.out.println("Consumption from PV: " + kostalConnector.getCurrentConsumptionFromPV());
            System.out.println("Total consumption: " + kostalConnector.getCurrentTotalConsumption());

            System.out.println("Battery Charge State: " + kostalConnector.getBatteryChargeState());
            System.out.println("Power Phase 1: " + kostalConnector.getPowerPhase1());
            System.out.println("Power Phase 2: " + kostalConnector.getPowerPhase2());
            System.out.println("Power Phase 3: " + kostalConnector.getPowerPhase3());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ModbusException e) {
            e.printStackTrace();
        }

//        try {
//
//
//            System.out.println("Connected " + modbusClient.isConnected());
//            System.out.println("Ip " + modbusClient.getipAddress());
//            System.out.println("Port " + modbusClient.getPort());
//            System.out.println("UnitIdentifier " + modbusClient.getUnitIdentifier());
//            System.out.println("UDPFlag " + modbusClient.getUDPFlag());
//            System.out.println("SerialFlag " + modbusClient.getSerialFlag());
//            System.out.println("Connect timeout " + modbusClient.getConnectionTimeout());
//
////            for(int x : resp) {
////                System.out.println(x);
////            }
//
//
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public float getCurrentConsumptionFromPV() throws IOException, ModbusException {
        return receiveFloat(ModbusAddress.CONSUMPTION_PV_CURRENT);
    }

    public float getCurrentConsumptionFromBattery() throws IOException, ModbusException {
        return receiveFloat(ModbusAddress.CONSUMPTION_BATTERY_CURRENT);
    }

    public float getCurrentConsumptionFromGrid() throws IOException, ModbusException {
        return receiveFloat(ModbusAddress.CONSUMPTION_GRID_CURRENT);
    }

    public float getCurrentTotalConsumption() throws IOException, ModbusException {
        return getCurrentConsumptionFromBattery() + getCurrentConsumptionFromGrid() + getCurrentConsumptionFromPV();
    }

    public float getBatteryChargeState() throws IOException, ModbusException {
        return receiveFloat(ModbusAddress.BATTERY_CHARGE_STATE);
    }

    public float getPowerPhase1() throws IOException, ModbusException {
        return receiveFloat(ModbusAddress.POWER_PHASE_1);
    }

    public float getPowerPhase2() throws IOException, ModbusException {
        return receiveFloat(ModbusAddress.POWER_PHASE_2);
    }

    public float getPowerPhase3() throws IOException, ModbusException {
        return receiveFloat(ModbusAddress.POWER_PHASE_1);
    }

    private float receiveFloat(ModbusAddress modbusAddress) throws IOException, ModbusException {
        final int[] resp = modbusClient.readHoldingRegisters(modbusAddress.getAddress(), 2);
            return ModbusClient.convertRegistersToFloat(resp);

    }

    private void initModbusClient() throws IOException {
        modbusClient.setConnectionTimeout(2000);
        modbusClient.setUnitIdentifier((byte)71);
        modbusClient.connect();
    }
}
