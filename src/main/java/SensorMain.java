import com.google.api.services.sheets.v4.model.ValueRange;
import google.GoogleSheetsService;
import google.GoogleSheetsUtil;
import sk.mimac.fingerprint.FingerprintException;
import sk.mimac.fingerprint.FingerprintSensor;
import sk.mimac.fingerprint.SensorParameters;
import sk.mimac.fingerprint.adafruit.AdafruitSensor;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SensorMain {

    public static String SPREADSHEET_ID = "1fJwkkomVpD5jRGDzFV_jsTr2NzaKSoWc1Odqjc0N26M";

    public static void main(String[] args) {
        try {


            GoogleSheetsService service = new GoogleSheetsService(
                    GoogleSheetsUtil.getSheetsService());
            //Reading from google sheet
            System.out.println(service.getRange(SPREADSHEET_ID, "C3"));


            // Connect (sensor is connected through UART to USB converter)
            FingerprintSensor sensor = new AdafruitSensor("COM3");

            byte[] model = null;

            HashMap<Integer, HashMap> db = new HashMap<>();
            sensor.connect();

            for (int i = 0; i < 3; i++) {
                System.out.println("place finger " + i);
                Thread.sleep(3000);
                if (sensor.hasFingerprint()) {

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
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
