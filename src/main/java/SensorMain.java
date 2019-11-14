import Hibernate.DbStudentService;
import Model.DbStudent;
import Model.Student;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import google.GoogleSheetsService;
import google.GoogleSheetsUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import org.hibernate.cfg.Configuration;
import sk.mimac.fingerprint.FingerprintException;
import sk.mimac.fingerprint.FingerprintSensor;
import sk.mimac.fingerprint.SensorParameters;
import sk.mimac.fingerprint.adafruit.AdafruitSensor;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

public class SensorMain {

    public static String SPREADSHEET_ID = "1fJwkkomVpD5jRGDzFV_jsTr2NzaKSoWc1Odqjc0N26M";

    public static <Employee> void main(String[] args) {
        try {
            List<Student> students = new ArrayList<Student>();
            ;

            System.out.println(students.toString());

            GoogleSheetsService service = new GoogleSheetsService(
                    GoogleSheetsUtil.getSheetsService());
            //Reading from google sheet

//read from B3 to B10
            ValueRange result = service.getRange(SPREADSHEET_ID, "B3:B50");
            //int numRows = result.getValues() != null ? result.getValues().size() : 0;

            List<List<Object>> list = result.getValues();
            //loop starts form here
            System.out.println("loop");
            for (Object l : list) {
                //removing first and last char []
                int id = list.indexOf(l)+3;
                String stdId = l.toString().substring(1, l.toString().length() - 1);

                System.out.println("index: " + id + " stdId: " + stdId);
                students.add(new Student(id, stdId));
            }
            System.out.println(students.toString());
//write------------------------------------------------------------------

            Sheets sheets = GoogleSheetsUtil.getSheetsService();

            List<ValueRange> data = new ArrayList<>();
//write present at E3
            data.add(new ValueRange()
                    .setRange("E3")
                    .setValues(Arrays.asList(
                            Arrays.asList("present"))));

            BatchUpdateValuesRequest batchBody = new BatchUpdateValuesRequest()
                    .setValueInputOption("RAW")
                    .setData(data);

            BatchUpdateValuesResponse batchResult = sheets.spreadsheets().values()
                    .batchUpdate(SPREADSHEET_ID, batchBody)
                    .execute();
            System.out.println("cells updated: " + batchResult.getTotalUpdatedCells());
//sensor--------------------------------------------------------------------------------





            DbStudent std = new DbStudent();

            std.setStd_Id("1610");

            String s="gg";
            byte[] bs=s.getBytes();
            std.setFingerPrint(bs);

            DbStudentService dbService = new DbStudentService();
            dbService.saveStudent(std);

            List stdList= dbService.getStudentsList();
            for (Object o: stdList) {
                System.out.println(o.toString());
            }







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
