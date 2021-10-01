# kostalconnector
Connector to a Kostal (Plenticore) PV inverter

It provides methods to query data from a Plenticore inverter.
The data is provided by the modbus interface of the inverter.

This is ongoing.
Currently, there is a main method in the class KostalConnector.java

There are several methods provided to read and convert data.
There is a [specification](https://www.kostal-solar-electric.com%2Fde-de%2Fprodukte%2Fsolar-wechselrichter%2F-%2Fmedia%2Fdocument-library-folder---kse%2F2020%2F12%2F15%2F13%2F38%2Fba_kostal-interface-description-modbus-tcp_sunspec_hybrid.pdf) which specifies the Modbus interface including a list of readable registers as well as return values and units.

The base functionality for writing into registers is also implemented although it is not tested.

This project will stay a jar project. The plan is, to implement a web application which either provides a well formed json API or to create a web interface which will act as an administration console for this and other related projects such as a weather forecast.

 
Base Setup:

- Change the ip address in application.properties to the ip address of your Kostal Plenticore inverter.
- Enable modbus in the web interface of your inverter (Settings -> Modbus / Sunspec (TCP)).
- Check if port and unit id are equal to the corresponding properties in application.properties.
- Run the main method in KostalConnector
