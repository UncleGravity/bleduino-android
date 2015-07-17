package com.kytelabs.bleduino.ble;

/**
 * Created by Angel Viera on 6/14/15.
 */

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for
 * demonstration purposes.
 */
public class BLEGattAttributes {
    private static HashMap<String, String> attributes = new HashMap<String, String>();
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    public static String BLEDUINO_UART_SERVICE = "8c6bda7a-a312-681d-025b-0032c0d16a2d";
    public static String BLEDUINO_UART_WRITE_CHARACTERISTIC = "8c6babcd-a312-681d-025b-0032c0d16a2d";
    public static String BLEDUINO_UART_READ_CHARACTERISTIC = "8c6b1010-a312-681d-025b-0032c0d16a2d";

    public static String BLEDUINO_FIRMATA_SERVICE = "8C6B1ED1-A312-681D-025B-0032C0D16A2D";
    public static String BLEDUINO_FIRMATA_CHARACTERISTIC = "8C6B2551-A312-681D-025B-0032C0D16A2D";

    public static String BLEDUINO_NOTIFICATION_SERVICE = "8c6b3141-a312-681d-025b-0032c0d16a2d";
    public static String BLEDUINO_NOTIFICATION_CHARACTERISTIC = "8c6b1618-a312-681d-025b-0032c0d16a2d";

}