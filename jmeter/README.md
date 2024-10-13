# Cold Chain Tracking Load Test

This load test uses JMeter and a prefilled dataset of the network server devices.

## Generate the devices dataset

Call the following application endpoints to generate the CSV containing the device's IDs for each network server:

**KPN**:
```
http://localhost:8080/tests/kpn?devices=100
```

**TTN**:
```
http://localhost:8080/tests/ttn?devices=100
```

Notice the `devices` argument. It controls how many devices, carriers, and freights are created.

## Execute the Load Test

First, copy the `KPN-devices-data.csv` and `TTN-devices-data.csv` files into the `jmeter` folder.

After that, considering the JMeter is installed, execute the following command to start JMeter:

```
jmeter -t cct-load-test.jmx
```

Check the `User Defined Variables` and click on the `Start` button to run the tests.

Use Grafana ([http://localhost:3000](http://localhost:3000)) to consult the metrics emitted by the application.