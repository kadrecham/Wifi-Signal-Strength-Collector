# Wifi-Signal-Strength-Collector

## Android mobile application for scanning the Wi-Fi signal strength of all available access points and sending the results as JSON object using HTTP request.

### Summary:
Getting the available access points signal strength as JSON objet.

![alt tag](https://user-images.githubusercontent.com/25906706/28717671-0b05da7e-73a3-11e7-987d-a560b2cc95bc.png)

### Goals:
* Using the application with [Indoor-Location-Detection-Scenario](https://github.com/kadrecham/Indoor-Location-Detection-Scenario) to get the indoor location.

### Tools:
* [Android Studio](https://developer.android.com/studio/index.html)
* [Spring for Android's RestTemplate](https://spring.io/guides/gs/consuming-rest-android/)

### Details:
This applecation simplly collects all of Wi-Fi signal strength for available access points then sends the results as JSON object to the web server using HTTP request. And it works in two mode:
* Mapping mode: Where it sends the scan results with the name of the location or the room (User should enter the name of location befor scanning).
* Detecting mode: Where it sends the scan results with the mac address (In this case the [Indoor-Location-Detection-Scenario](https://github.com/kadrecham/Indoor-Location-Detection-Scenario) should get the location of the user).  

Note: The user also should enter the IP address for the web server which is supposed to host the [Indoor-Location-Detection-Scenario](https://github.com/kadrecham/Indoor-Location-Detection-Scenario) and receive the scan results.
