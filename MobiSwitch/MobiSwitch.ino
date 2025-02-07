#include <WiFi.h>
#include <WebServer.h>

const char *ssid = "MobiSwitch";
const char *password = "12345678";

WebServer server(80);

bool signalState = false;

void handleOn() {
    signalState = true;
    digitalWrite(33, HIGH);
    Serial.println("ON");
    server.send(200, "text/plain", "Signal ON");
}

void handleOff() {
    signalState = false;
    digitalWrite(33, LOW);
    Serial.println("OFF");
    server.send(200, "text/plain", "Signal OFF");
}

void handleStatus() {
    server.send(200, "text/plain", signalState ? "ON" : "OFF");
}

void checkClients() {
    if (WiFi.softAPgetStationNum() > 0) {
        digitalWrite(2, HIGH);
    } else {
        digitalWrite(2, LOW);
    }
}

void setup() {
    Serial.begin(115200);
    pinMode(2, OUTPUT);
    pinMode(33, OUTPUT);
    WiFi.softAP(ssid, password);
    Serial.println("ESP32 AP Started");
    server.on("/on", handleOn);
    server.on("/off", handleOff);
    server.on("/status", handleStatus);
    server.begin();
    Serial.println("Server Started");
}

void loop() {
    server.handleClient();
    checkClients();
}
