package de.de.kostalconnector;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class KostalConnectorTest {

    @Mock
    private ModbusClient modbusClient;

    private KostalConnector kostalConnector;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test() {}
}