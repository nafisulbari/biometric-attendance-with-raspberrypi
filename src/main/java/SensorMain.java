import sk.mimac.fingerprint.FingerprintException;
import sk.mimac.fingerprint.FingerprintSensor;
import sk.mimac.fingerprint.SensorParameters;
import sk.mimac.fingerprint.adafruit.AdafruitSensor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SensorMain {
    public static void main(String[] args) {

        try {

// Connect (sensor is connected through UART to USB converter)
            FingerprintSensor sensor = new AdafruitSensor("COM3");


            byte[] model = null;

            HashMap<Integer, HashMap> db = new HashMap<>();
            sensor.connect();


            for (int i = 0; i < 3; i++) {
                System.out.println("place finger " + i);
                Thread.sleep(3000);
                if (sensor.hasFingerprint()) {
                    // ... and put it back on (model has to be calculated from two images)


                    model = sensor.createModel();
                    Thread.sleep(50);
                    System.out.println("created model");
                    sensor.saveStoredModel(i);


                }
            }
            while (true) {
                System.out.println("checking finger match");
                if (sensor.hasFingerprint()) {
                    Integer fingerId = sensor.searchFingerprint();
                    if (fingerId != null) { // Already known fingerprint
                        System.out.println("Scanned fingerprint with ID " + fingerId);
                    } else {
                        System.out.println("no match");
                    }
                }
            }

        } catch (FingerprintException | InterruptedException | NullPointerException e) {
            e.printStackTrace();
        }

    }
}
