/*
 * It supports WiFi LoRa 32 V2 and NodeMCU V3 boards.
 * Install the boards;
 * Select the board;
 * Install the DHT library (https://github.com/Khuuxuanngoc/DHT-sensor-library);
 * Install the LMIC library (https://github.com/mcci-catena/arduino-lmic);
 * Change the LMIC configurations (arduino-lmic/project_config/lmic_project_config.h);
 * Define the Network Server (TTN or KPN);
 * Register the device on the Network Server and set the keys: AppEUI, DevEUI and AppKey (change the FILLMEIN references).
 * Compile and upload the code;
 * Open the Serial Monitor: 9600 baud
 */
#include <lmic.h>
#include <hal/hal.h>
#include <SPI.h>
#include <DHT.h>

// For normal use, we require that you edit the sketch to replace FILLMEIN
// with values assigned by the TTN console. However, for regression tests,
// we want to be able to compile these scripts. The regression tests define
// COMPILE_REGRESSION_TEST, and in that case we define FILLMEIN to a non-
// working but innocuous value.
//
#ifdef COMPILE_REGRESSION_TEST
# define FILLMEIN 0
#else
# warning "You must replace the values marked FILLMEIN with real values from the TTN control panel!"
# define FILLMEIN (#dont edit this, edit the lines that use FILLMEIN)
#endif

#define DHT_SENSOR_TYPE DHT22

//#define TTN_NETWORK true
#define KPN_NETWORK true

#ifdef WIFI_LORA_32_V2
  // Wifi LoRa 32 v2
  // Library: https://espressif.github.io/arduino-esp32/package_esp32_index.json
  // Pins: https://github.com/espressif/arduino-esp32/blob/master/variants/heltec_wifi_lora_32_V2/pins_arduino.h
  // Pin mapping: https://github.com/lnlp/LMIC-node/blob/main/src/boards/bsf_heltec_wifi_lora_32_v2.h
  const lmic_pinmap lmic_pins = {
    .nss = SS, // 18
    .rxtx = LMIC_UNUSED_PIN,
    .rst = RST_LoRa,
    .dio = {DIO0, DIO1, DIO2}, // 26, 35, 34
    .rxtx_rx_active = 0,
    .rssi_cal = 10,
    .spi_freq = 8000000     // 8 MHz
  };

  #define DHT_SENSOR_PIN  T2 // 2
  #define LED_ON_MODE HIGH
  #define LED_OFF_MODE LOW

  #ifdef TTN_NETWORK
    // This EUI must be in little-endian format, so least-significant-byte first. When copying an EUI from ttnctl output, this means to reverse the bytes. For TTN issued EUIs the last bytes should be 0xD5, 0xB3, 0x70.
    static const u1_t PROGMEM APPEUI[8]={ FILLMEIN };
    // This should also be in little endian format, see above.
    static const u1_t PROGMEM DEVEUI[8]={ FILLMEIN };
    // This key should be in big endian format (or, since it is not really a number but a block of memory, endianness does not really apply). In practice, a key taken from ttnctl can be copied as-is.
    static const u1_t PROGMEM APPKEY[16] = { FILLMEIN };
  #endif

  #ifdef KPN_NETWORK
    static const u1_t PROGMEM APPEUI[8]={ FILLMEIN };
    static const u1_t PROGMEM DEVEUI[8]={ FILLMEIN };
    static const u1_t PROGMEM APPKEY[16] = { FILLMEIN };
  #endif
#else
  // ESP8266_NODEMCU_ESP12E
  // Library: http://arduino.esp8266.com/stable/package_esp8266com_index.json
  // Pins: https://github.com/esp8266/Arduino/blob/master/variants/nodemcu/pins_arduino.h
  // Pin mapping: https://github.com/lnlp/LMIC-node/blob/main/src/boards/bsf_nodemcuv2.h
  const lmic_pinmap lmic_pins = {
     .nss = D8,
     .rxtx = LMIC_UNUSED_PIN,
     .rst = LMIC_UNUSED_PIN,
     .dio = { D1, D0, LMIC_UNUSED_PIN }, // it was D1, D2 (5, 4)
     .rxtx_rx_active = 0,
     .rssi_cal = 10,
     .spi_freq = 1000000, // 1 MHz
  };

  #define DHT_SENSOR_PIN  D2

  #ifndef LED_BUILTIN
  #define LED_BUILTIN 2
  #endif

  #define LED_ON_MODE LOW
  #define LED_OFF_MODE HIGH

  #ifdef TTN_NETWORK
    static const u1_t PROGMEM APPEUI[8]={ FILLMEIN };
    static const u1_t PROGMEM DEVEUI[8]={ FILLMEIN };
    static const u1_t PROGMEM APPKEY[16] = { FILLMEIN };
  #endif

  #ifdef KPN_NETWORK
    static const u1_t PROGMEM APPEUI[8]={ FILLMEIN };
    static const u1_t PROGMEM DEVEUI[8]={ FILLMEIN };
    static const u1_t PROGMEM APPKEY[16] = { FILLMEIN };
  #endif
#endif

void os_getArtEui (u1_t* buf) { memcpy_P(buf, APPEUI, 8);}
void os_getDevEui (u1_t* buf) { memcpy_P(buf, DEVEUI, 8);}
void os_getDevKey (u1_t* buf) {  memcpy_P(buf, APPKEY, 16);}

// payload to send to gateway
static uint8_t payload[5];
static osjob_t sendjob;
static int fPort;

// Schedule TX every this many seconds (might become longer due to duty
// cycle limitations).
const unsigned TX_INTERVAL = 60;

DHT dht_sensor(DHT_SENSOR_PIN, DHT_SENSOR_TYPE);

void printHex2(unsigned v) {
    v &= 0xff;
    if (v < 16)
        Serial.print('0');
    Serial.print(v, HEX);
}

void onEvent (ev_t ev) {
    Serial.print(os_getTime());
    Serial.print(": ");

    switch(ev) {
        case EV_SCAN_TIMEOUT:
            Serial.println(F("EV_SCAN_TIMEOUT"));
            break;
        case EV_BEACON_FOUND:
            Serial.println(F("EV_BEACON_FOUND"));
            break;
        case EV_BEACON_MISSED:
            Serial.println(F("EV_BEACON_MISSED"));
            break;
        case EV_BEACON_TRACKED:
            Serial.println(F("EV_BEACON_TRACKED"));
            break;
        case EV_JOINING:
            Serial.println(F("EV_JOINING"));
            break;
        case EV_JOINED:
            Serial.println(F("EV_JOINED"));
            {
              u4_t netid = 0;
              devaddr_t devaddr = 0;
              u1_t nwkKey[16];
              u1_t artKey[16];
              LMIC_getSessionKeys(&netid, &devaddr, nwkKey, artKey);
              Serial.print("netid: ");
              Serial.println(netid, DEC);
              Serial.print("devaddr: ");
              Serial.println(devaddr, HEX);
              Serial.print("AppSKey: ");
              for (size_t i=0; i<sizeof(artKey); ++i) {
                if (i != 0)
                  Serial.print("-");
                printHex2(artKey[i]);
              }
              Serial.println("");
              Serial.print("NwkSKey: ");
              for (size_t i=0; i<sizeof(nwkKey); ++i) {
                      if (i != 0)
                              Serial.print("-");
                      printHex2(nwkKey[i]);
              }
              Serial.println();
            }
            // Disable link check validation (automatically enabled
            // during join, but because slow data rates change max TX
	          // size, we don't use it in this example.
            LMIC_setLinkCheckMode(0);
            break;
        /*
        || This event is defined but not used in the code. No
        || point in wasting codespace on it.
        ||
        || case EV_RFU1:
        ||     Serial.println(F("EV_RFU1"));
        ||     break;
        */
        case EV_JOIN_FAILED:
            Serial.println(F("EV_JOIN_FAILED"));
            break;
        case EV_REJOIN_FAILED:
            Serial.println(F("EV_REJOIN_FAILED"));
            break;
        case EV_TXCOMPLETE:
            Serial.println(F("EV_TXCOMPLETE (includes waiting for RX windows)"));
            if (LMIC.txrxFlags & TXRX_ACK)
              Serial.println(F("Received ack"));
            if (LMIC.dataLen) {
              Serial.print(F("Received "));
              Serial.print(LMIC.dataLen);
              Serial.println(F(" bytes of payload: "));
              Serial.write(LMIC.frame + LMIC.dataBeg, LMIC.dataLen);
              fPort = LMIC.frame[LMIC.dataBeg - 1]; // (dataBeg-1 is port)
              Serial.println(fPort);
              handleConditionViolation();
              Serial.println();
            }
            // Schedule next transmission
            os_setTimedCallback(&sendjob, os_getTime()+sec2osticks(TX_INTERVAL), do_send);
            break;
        case EV_LOST_TSYNC:
            Serial.println(F("EV_LOST_TSYNC"));
            break;
        case EV_RESET:
            Serial.println(F("EV_RESET"));
            break;
        case EV_RXCOMPLETE:
            // data received in ping slot
            Serial.println(F("EV_RXCOMPLETE"));
            break;
        case EV_LINK_DEAD:
            Serial.println(F("EV_LINK_DEAD"));
            break;
        case EV_LINK_ALIVE:
            Serial.println(F("EV_LINK_ALIVE"));
            break;
        /*
        || This event is defined but not used in the code. No
        || point in wasting codespace on it.
        ||
        || case EV_SCAN_FOUND:
        ||    Serial.println(F("EV_SCAN_FOUND"));
        ||    break;
        */
        case EV_TXSTART:
            Serial.println(F("EV_TXSTART"));
            break;
        case EV_TXCANCELED:
            Serial.println(F("EV_TXCANCELED"));
            break;
        case EV_RXSTART:
            /* do not print anything -- it wrecks timing */
            break;
        case EV_JOIN_TXCOMPLETE:
            Serial.println(F("EV_JOIN_TXCOMPLETE: no JoinAccept"));
            break;

        default:
            Serial.print(F("Unknown event: "));
            Serial.println((unsigned) ev);
            break;
    }
}

void do_send(osjob_t* j){
    // Check if there is not a current TX/RX job running
    if (LMIC.opmode & OP_TXRXPEND) {
        Serial.println(F("OP_TXRXPEND, not sending"));
    } else {
        readTemperatureAndHumidty();

        // prepare upstream data transmission at the next possible time.
        // transmit on port 1 (the first parameter); you can use any value from 1 to 223 (others are reserved).
        // don't request an ack (the last parameter, if not zero, requests an ack from the network).
        // Remember, acks consume a lot of network resources; don't ask for an ack unless you really need it.
        LMIC_setTxData2(1, payload, sizeof(payload)-1, 0);
    }
    // Next TX is scheduled after TX_COMPLETE event.
}

void readTemperatureAndHumidty() {
  // read the temperature from the DHT22
  float temperature = dht_sensor.readTemperature();
  Serial.print("Temperature: ");
  Serial.print(temperature);
  Serial.println(" degree Celsius");
  // adjust for the f2sflt16 range (-1 to 1)
  temperature = temperature / 100;

  // read the humidity from the DHT22
  float rHumidity = dht_sensor.readHumidity();
  Serial.print("%RH ");
  Serial.println(rHumidity);
  // adjust for the f2sflt16 range (-1 to 1)
  rHumidity = rHumidity / 100;

  // float -> int
  // note: this uses the sflt16 datum (https://github.com/mcci-catena/arduino-lmic#sflt16)
  uint16_t payloadTemp = LMIC_f2sflt16(temperature);
  // int -> bytes
  byte tempLow = lowByte(payloadTemp);
  byte tempHigh = highByte(payloadTemp);
  // place the bytes into the payload
  payload[0] = tempLow;
  payload[1] = tempHigh;

  // float -> int
  uint16_t payloadHumid = LMIC_f2sflt16(rHumidity);
  // int -> bytes
  byte humidLow = lowByte(payloadHumid);
  byte humidHigh = highByte(payloadHumid);
  payload[2] = humidLow;
  payload[3] = humidHigh;
}

void handleConditionViolation() {
  Serial.println("Condition violated. Turning LED on...");
  digitalWrite(LED_BUILTIN, LED_ON_MODE);
}

void setup() {
  Serial.begin(9600);
  Serial.println(F("Starting"));

  // Set LED as output to show violation
  pinMode(LED_BUILTIN, OUTPUT);
  digitalWrite(LED_BUILTIN, LED_OFF_MODE);

  // Initialize the DHT sensor
  dht_sensor.begin();

  // LMIC init
  os_init();

  // Reset the MAC state. Session and pending data transfers will be discarded.
  LMIC_reset();

  // Start job (sending automatically starts OTAA too)
  do_send(&sendjob);
}

void loop() {
  os_runloop_once();
}
