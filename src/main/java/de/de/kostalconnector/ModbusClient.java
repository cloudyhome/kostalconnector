package de.de.kostalconnector;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

public class ModbusClient {

    private Socket socket;
    private final String hostName;
    private final int port;
    private int connectionTimeout = 10000;
    private byte unitIdentifier;

    private static final byte[] TRANSACTION_NUMBER = {1, 0};
    private static final byte[] PROTOCOL_INDICATOR = {0, 0};
    private static final byte[] length = {0, 6};

    private static final byte FUNCTION_CODE_READ_HOLDING_REGISTERS = 0x03;
    private static final byte FUNCTION_CODE_WRITE_SINGLE_REGISTER = 0x06;


    public ModbusClient(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
    }

    /**
     * Connects to a Modbus Server
     *
     * @throws IOException For any I/O error
     */
    void connect() throws IOException {
        socket = new Socket(hostName, port);
        socket.setSoTimeout(connectionTimeout);

    }

    /**
     * Converts int array of size 2 into one float.
     *
     * @param values The int array to convert
     * @return The converted array as a float
     */
    static float convertToFloat(int[] values) {
        if (values.length != 2) {
            throw new IllegalArgumentException("Array length must be 2!");
        }
        byte[] leadingBytes = toByteArray(values[1]);
        byte[] trailingBytes = toByteArray(values[0]);
        byte[] floatBytes = {
                leadingBytes[1],
                leadingBytes[0],
                trailingBytes[1],
                trailingBytes[0]
        };
        return ByteBuffer.wrap(floatBytes).getFloat();
    }


    /**
     * Read Holding Registers from Server
     *
     * @param startingAddress Address to read -1
     * @param quantity        number of registers which should be read
     * @return The values held by the queried registers
     * @throws ModbusException If the modbus server responds with an exception response
     * @throws IOException     In case of any I/O error
     */
    public int[] readHoldingRegisters(int startingAddress, int quantity) throws ModbusException,
            IOException {
        checkSocket();
        if (startingAddress > 65535 || quantity > 125)
            throw new IllegalArgumentException("Invalid start address or length");

        byte[] sendData = createOutput(FUNCTION_CODE_READ_HOLDING_REGISTERS, toByteArray(startingAddress), toByteArray(quantity));
        byte[] receiveData = sendAndReceive(sendData);
        if (receiveData[7] == 0x83) {
            handleExceptionResponse(receiveData[8]);
        }
        int[] response = new int[quantity];
        for (int i = 0; i < quantity; i++) {
            byte[] bytes = new byte[2];
            bytes[0] = receiveData[9 + i * 2];
            bytes[1] = receiveData[9 + i * 2 + 1];
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

            response[i] = byteBuffer.getShort();
        }
        return (response);
    }


    public void writeSingleRegister(int startAddress, int value) throws ModbusException,
            IOException {
        checkSocket();
        byte[] sendData = createOutput(FUNCTION_CODE_WRITE_SINGLE_REGISTER, toByteArray(startAddress), toByteArray(value));
        byte[] receiveData = sendAndReceive(sendData);

        if (receiveData[7] == 0x86) {
            handleExceptionResponse(receiveData[8]);
        }
    }


    /**
     * Close connection to Server
     *
     * @throws IOException In case of any I/O error
     */
    public void disconnect() throws IOException {
        if (socket != null) {
            socket.getInputStream().close();
            socket.getOutputStream().close();

            socket.close();
        }
    }


    private static byte[] toByteArray(int value) {
        byte[] result = new byte[2];
        result[1] = (byte) (value >> 8);
        result[0] = (byte) (value);
        return result;
    }

    private byte[] sendAndReceive(byte[] sendData) throws IOException {
        socket.getOutputStream().write(sendData, 0, sendData.length);

        byte[] receiveData = new byte[128];
        socket.getInputStream().read(receiveData, 0, receiveData.length);
        return receiveData;
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
            default:
                throw new ModbusException("Unknown server exception; code: " + exceptionCode);

        }
    }

    private byte[] createOutput(byte functionCode, byte[] startAddress, byte[] quantity) {
        return new byte[]
                {
                        TRANSACTION_NUMBER[1],
                        TRANSACTION_NUMBER[0],
                        PROTOCOL_INDICATOR[1],
                        PROTOCOL_INDICATOR[0],
                        length[1],
                        length[0],
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
    private boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    private void checkSocket() throws SocketException {
        if (!isConnected()) {
            throw new SocketException("Socket not initialized");
        }
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
