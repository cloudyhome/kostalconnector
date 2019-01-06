package de.de.kostalconnector;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.io.InputStream;


public class ModbusClient {
    private Socket socket;
    private final String hostName;
    private final int port;
    private static final byte[] TRANSACTION_NUMBER = {1, 0};
    private static final byte[] PROTOCOL_INDICATOR = {0, 0};
    private static final byte[] length = {0, 6};
    private byte unitIdentifier;
    private final static byte FUNCTION_CODE_READ_HOLDING_REGISTERS = 0x03;
    private final static byte FUNCTION_CODE_WRITE_SINGLE_REGISTER = 0x06;
    private int connectionTimeout = 10000;


    public ModbusClient(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    /**
     * Connects to ModbusServer
     *
     * @throws UnknownHostException
     * @throws IOException
     */
    public void connect() throws UnknownHostException, IOException {
        socket = new Socket(hostName, port);
        socket.setSoTimeout(connectionTimeout);

    }

    /**
     * Convert two 16 Bit Registers to 32 Bit real value
     *
     * @param registers 16 Bit Registers
     * @return 32 bit real value
     */
    public static float convertRegistersToFloat(int[] registers) throws IllegalArgumentException {
        if (registers.length != 2)
            throw new IllegalArgumentException("Input Array length invalid");
        int highRegister = registers[1];
        int lowRegister = registers[0];
        byte[] highRegisterBytes = toByteArray(highRegister);
        byte[] lowRegisterBytes = toByteArray(lowRegister);
        byte[] floatBytes = {
                highRegisterBytes[1],
                highRegisterBytes[0],
                lowRegisterBytes[1],
                lowRegisterBytes[0]
        };
        return ByteBuffer.wrap(floatBytes).getFloat();
    }

    /**
     * Convert two 16 Bit Registers to 64 Bit double value  Reg0: Low Word.....Reg3: High Word
     *
     * @param registers 16 Bit Registers
     * @return 64 bit double value
     */
    public static double convertRegistersToDouble(int[] registers) throws IllegalArgumentException {
        if (registers.length != 4)
            throw new IllegalArgumentException("Input Array length invalid");
        byte[] highRegisterBytes = toByteArray(registers[3]);
        byte[] highLowRegisterBytes = toByteArray(registers[2]);
        byte[] lowHighRegisterBytes = toByteArray(registers[1]);
        byte[] lowRegisterBytes = toByteArray(registers[0]);
        byte[] doubleBytes = {
                highRegisterBytes[1],
                highRegisterBytes[0],
                highLowRegisterBytes[1],
                highLowRegisterBytes[0],
                lowHighRegisterBytes[1],
                lowHighRegisterBytes[0],
                lowRegisterBytes[1],
                lowRegisterBytes[0]
        };
        return ByteBuffer.wrap(doubleBytes).getDouble();
    }


    /**
     * Convert four 16 Bit Registers to 64 Bit long value Reg0: Low Word.....Reg3: High Word
     *
     * @param registers 16 Bit Registers
     * @return 64 bit value
     */
    public static long convertRegistersToLong(int[] registers) throws IllegalArgumentException {
        if (registers.length != 4)
            throw new IllegalArgumentException("Input Array length invalid");
        byte[] highRegisterBytes = toByteArray(registers[3]);
        byte[] highLowRegisterBytes = toByteArray(registers[2]);
        byte[] lowHighRegisterBytes = toByteArray(registers[1]);
        byte[] lowRegisterBytes = toByteArray(registers[0]);
        byte[] longBytes = {
                highRegisterBytes[1],
                highRegisterBytes[0],
                highLowRegisterBytes[1],
                highLowRegisterBytes[0],
                lowHighRegisterBytes[1],
                lowHighRegisterBytes[0],
                lowRegisterBytes[1],
                lowRegisterBytes[0]
        };
        return ByteBuffer.wrap(longBytes).getLong();
    }

    /**
     * Convert two 16 Bit Registers to 32 Bit long value
     *
     * @param registers 16 Bit Registers
     * @return 32 bit value
     */
    public static int convertRegistersToInt(int[] registers) throws IllegalArgumentException {
        if (registers.length != 2)
            throw new IllegalArgumentException("Input Array length invalid");
        int highRegister = registers[1];
        int lowRegister = registers[0];
        byte[] highRegisterBytes = toByteArray(highRegister);
        byte[] lowRegisterBytes = toByteArray(lowRegister);
        byte[] doubleBytes = {
                highRegisterBytes[1],
                highRegisterBytes[0],
                lowRegisterBytes[1],
                lowRegisterBytes[0]
        };
        return ByteBuffer.wrap(doubleBytes).getInt();
    }

    /**
     * Convert 32 Bit real Value to two 16 Bit Value to send as Modbus Registers
     *
     * @param floatValue real to be converted
     * @return 16 Bit Register values
     */
    public static int[] convertFloatToRegisters(float floatValue) {
        byte[] floatBytes = toByteArray(floatValue);
        byte[] highRegisterBytes =
                {
                        0, 0,
                        floatBytes[0],
                        floatBytes[1],

                };
        byte[] lowRegisterBytes =
                {
                        0, 0,
                        floatBytes[2],
                        floatBytes[3],

                };
        int[] returnValue =
                {
                        ByteBuffer.wrap(lowRegisterBytes).getInt(),
                        ByteBuffer.wrap(highRegisterBytes).getInt()
                };
        return returnValue;
    }

    /**
     * Convert 32 Bit Value to two 16 Bit Value to send as Modbus Registers
     *
     * @param intValue Value to be converted
     * @return 16 Bit Register values
     */
    public static int[] convertIntToRegisters(int intValue) {
        byte[] doubleBytes = toByteArrayInt(intValue);
        byte[] highRegisterBytes =
                {
                        0, 0,
                        doubleBytes[0],
                        doubleBytes[1],

                };
        byte[] lowRegisterBytes =
                {
                        0, 0,
                        doubleBytes[2],
                        doubleBytes[3],

                };
        int[] returnValue =
                {
                        ByteBuffer.wrap(lowRegisterBytes).getInt(),
                        ByteBuffer.wrap(highRegisterBytes).getInt()
                };
        return returnValue;
    }


    /**
     * Convert 64 Bit Value to four 16 Bit Value to send as Modbus Registers
     *
     * @param longValue Value to be converted
     * @return 16 Bit Register values
     */
    public static int[] convertLongToRegisters(long longValue) {
        byte[] doubleBytes = toByteArrayLong(longValue);
        byte[] highhighRegisterBytes =
                {
                        0, 0,
                        doubleBytes[0],
                        doubleBytes[1],

                };
        byte[] highlowRegisterBytes =
                {
                        0, 0,
                        doubleBytes[2],
                        doubleBytes[3],

                };
        byte[] lowHighRegisterBytes =
                {
                        0, 0,
                        doubleBytes[4],
                        doubleBytes[5],
                };
        byte[] lowlowRegisterBytes =
                {
                        0, 0,
                        doubleBytes[6],
                        doubleBytes[7],

                };
        int[] returnValue =
                {
                        ByteBuffer.wrap(lowlowRegisterBytes).getInt(),
                        ByteBuffer.wrap(lowHighRegisterBytes).getInt(),
                        ByteBuffer.wrap(highlowRegisterBytes).getInt(),
                        ByteBuffer.wrap(highhighRegisterBytes).getInt(),
                };
        return returnValue;
    }

    /**
     * Convert 64 Bit Value to four 16 Bit Value to send as Modbus Registers
     *
     * @param doubleValue Value to be converted
     * @return 16 Bit Register values
     */
    public static int[] convertDoubleToRegisters(double doubleValue) {
        byte[] doubleBytes = toByteArrayDouble(doubleValue);
        byte[] highhighRegisterBytes =
                {
                        0, 0,
                        doubleBytes[0],
                        doubleBytes[1],

                };
        byte[] highlowRegisterBytes =
                {
                        0, 0,
                        doubleBytes[2],
                        doubleBytes[3],

                };
        byte[] lowHighRegisterBytes =
                {
                        0, 0,
                        doubleBytes[4],
                        doubleBytes[5],
                };
        byte[] lowlowRegisterBytes =
                {
                        0, 0,
                        doubleBytes[6],
                        doubleBytes[7],

                };
        int[] returnValue =
                {
                        ByteBuffer.wrap(lowlowRegisterBytes).getInt(),
                        ByteBuffer.wrap(lowHighRegisterBytes).getInt(),
                        ByteBuffer.wrap(highlowRegisterBytes).getInt(),
                        ByteBuffer.wrap(highhighRegisterBytes).getInt(),
                };
        return returnValue;
    }

    /**
     * Converts 16 - Bit Register values to String
     *
     * @param registers    Register array received via Modbus
     * @param offset       First Register containing the String to convert
     * @param stringLength number of characters in String (must be even)
     * @return Converted String
     */
    public static String convertRegistersToString(int[] registers, int offset, int stringLength) {
        byte[] result = new byte[stringLength];
        byte[] registerResult = new byte[2];

        for (int i = 0; i < stringLength / 2; i++) {
            registerResult = toByteArray(registers[offset + i]);
            result[i * 2] = registerResult[0];
            result[i * 2 + 1] = registerResult[1];
        }
        return new String(result);
    }

    /**
     * Converts a String to 16 - Bit Registers
     *
     * @param stringToConvert String to Convert<
     * @return Converted String
     */
    public static int[] convertStringToRegisters(String stringToConvert) {
        byte[] array = stringToConvert.getBytes();
        int[] returnarray = new int[stringToConvert.length() / 2 + stringToConvert.length() % 2];
        for (int i = 0; i < returnarray.length; i++) {
            returnarray[i] = array[i * 2];
            if (i * 2 + 1 < array.length) {
                returnarray[i] = returnarray[i] | ((int) array[i * 2 + 1] << 8);
            }
        }
        return returnarray;
    }


    /**
     * Read Holding Registers from Server
     *
     * @param startingAddress Fist Address to read; Shifted by -1
     * @param quantity        Number of Inputs to read
     * @return Holding Registers from Server
     * @throws ModbusException
     * @throws UnknownHostException
     * @throws SocketException
     */
    public int[] readHoldingRegisters(int startingAddress, int quantity) throws ModbusException,
            SocketException, IOException {
        if (socket == null) {
            throw new SocketException("Socket not initialized");
        }
            if (startingAddress > 65535 || quantity > 125)
                throw new IllegalArgumentException("Starting adress must be 0 - 65535; quantity must be 0 - 125");

        byte[] data = createOutput(FUNCTION_CODE_READ_HOLDING_REGISTERS, toByteArray(startingAddress), toByteArray(quantity));

        if (socket.isConnected()) {
            socket.getOutputStream().write(data, 0, data.length);
            data = new byte[2100];
            InputStream inputStream = socket.getInputStream();
            inputStream.read(data, 0, data.length);
        }
        if (data[7] == 0x83) {
            handleExceptionResponse(data[8]);
        }
        int[] response = new int[quantity];
        for (int i = 0; i < quantity; i++) {
            byte[] bytes = new byte[2];
            bytes[0] = data[9 + i * 2];
            bytes[1] = data[9 + i * 2 + 1];
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

            response[i] = byteBuffer.getShort();
        }
        return (response);
    }




    /**
     * Write Single Register to Server
     *
     * @param startAddress Address to write; Shifted by -1
     * @param value           Value to write to Server
     * @throws UnknownHostException
     * @throws SocketException
     */
    public void writeSingleRegister(int startAddress, int value) throws ModbusException,
            UnknownHostException, SocketException, IOException {
        if (socket == null)
            throw new SocketException("Socket not initialized");
        byte[] data = createOutput(FUNCTION_CODE_WRITE_SINGLE_REGISTER, toByteArray(startAddress), toByteArray(value));
        if (socket.isConnected()) {

            socket.getOutputStream().write(data, 0, data.length);

            data = new byte[2100];
            InputStream inputStream = socket.getInputStream();
            int responseLength = inputStream.read(data, 0, data.length);

        }
        if (data[7] == 0x86) {
            handleExceptionResponse(data[8]);
        }
    }


    /**
     * Close connection to Server
     *
     * @throws IOException
     */
    public void disconnect() throws IOException {

       socket.getInputStream().close();
        socket.getOutputStream().close();
        if (socket != null)
            socket.close();
        socket = null;


    }


    public static byte[] toByteArray(int value) {
        byte[] result = new byte[2];
        result[1] = (byte) (value >> 8);
        result[0] = (byte) (value);
        return result;
    }

    public static byte[] toByteArrayInt(int value) {
        return ByteBuffer.allocate(4).putInt(value).array();
    }

    public static byte[] toByteArrayLong(long value) {
        return ByteBuffer.allocate(8).putLong(value).array();
    }

    public static byte[] toByteArrayDouble(double value) {
        return ByteBuffer.allocate(8).putDouble(value).array();
    }

    public static byte[] toByteArray(float value) {
        return ByteBuffer.allocate(4).putFloat(value).array();
    }

    private void handleExceptionResponse(int exceptionCode) throws ModbusException {
        switch (exceptionCode) {
            case 1:
                throw new ModbusException("ILLEGAL FUNCTION");
            case 2:
                throw new ModbusException("ILLEGAL DATA ADDRESS");
            case 3:
                throw new ModbusException("ILLEGAL DATA VALUE");
            case 4:
                throw new ModbusException("SERVER DEVICE FAILURE");
        }
    }

    private byte[] createOutput(byte functionCode,  byte[]  startAddress, byte[] quantity) {
        return new byte[]
                {
                        TRANSACTION_NUMBER[1],
                        TRANSACTION_NUMBER[0],
                        this.PROTOCOL_INDICATOR[1],
                        this.PROTOCOL_INDICATOR[0],
                        this.length[1],
                        this.length[0],
                        this.unitIdentifier,
                        functionCode,
                        startAddress[1],
                        startAddress[0],
                        quantity[1],
                        quantity[0]
                };
    }

    /**
     * client connected to Server
     *
     * @return if Client is connected to Server
     */
    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }





    public String getHostName() {
        return hostName;
    }

    /**
     * Returns port of Server listening
     *
     * @return port of Server listening
     */
    public int getPort() {
        return port;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }


    public void setUnitIdentifier(byte unitIdentifier) {
        this.unitIdentifier = unitIdentifier;
    }

    public byte getUnitIdentifier() {
        return this.unitIdentifier;
    }

}
