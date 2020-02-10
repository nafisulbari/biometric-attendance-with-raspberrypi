# IoT Based Biometric Attendance System
This is a class project of mine where we took attendance via fingerprint sensor, process the data with a raspberry pi and live update the attendance in google sheet.

## Components used*
1. Raspberry Pi 3 Model B
2. FPN 10 Fingerprint Sensor
3. USB to TTL adapter
4. 16*2 LCD Display
5. I2C adapter

## Deploy
* Using google developers console enable google sheet API
* Create a service email
* Download the json credential file
* Replace the json credential file in the resources folder
* Configure the fingerprint sensor in SensorMain class
* Build and Deploy in Raspberry Pi