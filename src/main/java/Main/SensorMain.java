package Main;

import Hibernate.DbStudentService;
import Model.DbStudent;
import Model.Student;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;

import com.google.api.services.sheets.v4.model.ValueRange;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import google.GoogleSheetsService;
import google.GoogleSheetsUtil;

import i2c.I2CLCD;
import sk.mimac.fingerprint.FingerprintException;
import sk.mimac.fingerprint.FingerprintSensor;

import sk.mimac.fingerprint.adafruit.AdafruitSensor;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

public class SensorMain {
    //Input SPREADSHEET_ID----------------------------------------------------------------------
    public static String SPREADSHEET_ID = "1fJwkkomVpD5jRGDzFV_jsTr2NzaKSoWc1Odqjc0N26M";

    public static void main(String[] args) {
        try {

            //Display-----------------------------------------------------------------------------
            I2CDevice _device = null;
            I2CLCD lcd = null;
            I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
            _device = bus.getDevice(0x27);
            lcd = new I2CLCD(_device);
            lcd.init();
            lcd.backlight(true);
            lcd.display_string_pos("App started", 1, 0);
            Thread.sleep(10000);

            List<Student> sheetStudents = new ArrayList<Student>();
            GoogleSheetsService service = new GoogleSheetsService(
                    GoogleSheetsUtil.getSheetsService());

            //Attendance index selector from A1----------------------------------------------------------
            ValueRange checkingIndex = service.getRange(SPREADSHEET_ID, "A1:A2");
            List<List<Object>> tempIndex = checkingIndex.getValues();
            String index = tempIndex.toString().substring(2, tempIndex.toString().length() - 2);
            System.out.println("index :" + index);

            //read from B3 to B50 to know list of students---------------------------------------
            ValueRange result = service.getRange(SPREADSHEET_ID, "B3:B50");
            //int numRows = result.getValues() != null ? result.getValues().size() : 0;

            List<List<Object>> list = result.getValues();
            //loop starts form here
            System.out.println("loop");
            for (Object l : list) {
                //removing first and last char []
                int id = list.indexOf(l) + 3;
                String stdId = l.toString().substring(1, l.toString().length() - 1);

                System.out.println("index: " + id + " stdId: " + stdId);
                sheetStudents.add(new Student(id, stdId));
            }
            System.out.println(sheetStudents.toString());


            //sensor Connection--------------------------------------------------------------------------------
            // Connect (sensor is connected through UART to USB converter)
            //"COM3" for pc com port3,   "/dev/ttyUSB0" for pi usb0
            FingerprintSensor sensor = new AdafruitSensor("/dev/ttyUSB0");
            byte[] model = null;
            HashMap<Integer, HashMap> db = new HashMap<>();
            sensor.connect();

//DB operations to SAVE STUDENT fingerprint in DB --------------------------------------
//            sensor.clearAllSaved();
//            for (int i = 0; i < 1; i++) {
//                System.out.println("place finger " + i);
//                Thread.sleep(3000);
//                if (sensor.hasFingerprint()) {
//
//                    model = sensor.createModel();
//                    Thread.sleep(50);
//                    System.out.println("created model");
//
//                    DbStudent dbStudent = new DbStudent();
//                    dbStudent.setStd_Id("16101239");
//                    dbStudent.setFingerPrint(model);
//                    DbStudentService dbService = new DbStudentService();
//                    dbService.saveStudent(dbStudent);
//                }
//            }


            //SAVING DB fingerprint to sensor memory according to STD_ID cell no in sheets-----------------------
            sensor.clearAllSaved();
            DbStudentService dbStudentService = new DbStudentService();
            List<DbStudent> dbStdList = dbStudentService.getStudentsList();
            System.out.println("list of stdss" + dbStdList.toString());

            for (Student sheetStd : sheetStudents) {
                for (DbStudent dbStd : dbStdList) {
                    if (sheetStd.getStdId().equals(dbStd.getStd_Id())) {
                        sensor.saveModel(dbStd.getFingerPrint(), sheetStd.getId());
                        System.out.println("db std id: " + dbStd.getStd_Id() + "sheet std id: " + sheetStd.getId());
                    }
                }
            }
            //fingerprint match checker------------------------------------------------------------------
            while (true) {
                lcd.clear();
                lcd.display_string_pos("Please", 1, 0);
                lcd.display_string_pos("Input Finger", 2, 0);
                Thread.sleep(1000);

                System.out.println("checking finger match");

                if (sensor.hasFingerprint()) {
                    Integer fingerId = sensor.searchFingerprint();
                    if (fingerId != null) { // Already known fingerprint
                        System.out.println("Scanned fingerprint with ID " + fingerId);

                        Sheets sheets = GoogleSheetsUtil.getSheetsService();

                        List<ValueRange> data = new ArrayList<>();
                        data.add(new ValueRange()
                                //determining in which index to put authenticated attendance  index=A1,
                                .setRange(index + fingerId)
                                .setValues(Arrays.asList(
                                        Arrays.asList("1"))));

                        BatchUpdateValuesRequest batchBody = new BatchUpdateValuesRequest()
                                .setValueInputOption("RAW")
                                .setData(data);

                        BatchUpdateValuesResponse batchResult = sheets.spreadsheets().values()
                                .batchUpdate(SPREADSHEET_ID, batchBody)
                                .execute();
                        System.out.println("cells updated: " + batchResult.getTotalUpdatedCells());


                        lcd.clear();
                        lcd.display_string_pos(sheetStudents.get(fingerId-3).getStdId(), 1, 0);
                        lcd.display_string_pos("Confirm", 2, 0);
                        Thread.sleep(2000);
                    } else {
                        System.out.println("no match");
                        lcd.clear();
                        lcd.display_string_pos("No match", 1, 0);
                        Thread.sleep(1000);
                    }
                }
            }

        } catch (FingerprintException | NullPointerException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
