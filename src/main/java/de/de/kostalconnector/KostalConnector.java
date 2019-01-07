package de.de.kostalconnector;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;


public class KostalConnector {

    private ModbusClient modbusClient;



    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        KostalConnector kostalConnector = applicationContext.getBean("kostalConnector", KostalConnector.class);
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
        } catch (IOException | ModbusException e) {
            e.printStackTrace();
        }
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
            return ModbusClient.convertToFloat(resp);

    }

    private void initModbusClient() throws IOException {
        modbusClient.connect();
    }

    @Required
    public void setModbusClient(ModbusClient modbusClient) {
        this.modbusClient = modbusClient;
    }
}
