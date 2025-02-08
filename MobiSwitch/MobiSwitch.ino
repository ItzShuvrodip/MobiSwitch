#include <WiFi.h>

const char *ssid = "MobiSwitch";
const char *password = "12345678";
WiFiServer server(80);

bool signalState = false;

void setup() {
  Serial.begin(115200);
  pinMode(2, OUTPUT);
  pinMode(33, OUTPUT);
  WiFi.disconnect();
  WiFi.softAP(ssid, password);
  server.begin(); 
  Serial.println("ESP32 AP Started & TCP Server Running");
}

void loop() {
  WiFiClient client = server.available(); 
  if (client) {
    Serial.println("Client connected");
    String request = "";

    while (client.connected()) {
      if (client.available()) {
        char c = client.read();
        request += c;
        if (c == '\n') break; 
      }
    }

    request.trim();
    Serial.println("Received: " + request);

    if (request.startsWith("GET ")) {
      int start = request.indexOf(" ") + 1;
      int end = request.indexOf(" ", start); 
      if (end > start) {
        request = request.substring(start, end);
      }
    }

    Serial.println("Parsed Command: " + request);

    String responseText;
    if (request == "/on") {
      signalState = true;
      digitalWrite(33, HIGH);
      Serial.println("Signal ON");
      responseText = "Signal ON";
    } 
    else if (request == "/off") {
      signalState = false;
      digitalWrite(33, LOW);
      Serial.println("Signal OFF");
      responseText = "Signal OFF";
    } 
    else if (request == "/status") {
      responseText = signalState ? "ON" : "OFF";
    } 
    else {
      responseText = "Invalid Command";
    }

    client.println("HTTP/1.1 200 OK");
    client.println("Content-Type: text/plain");
    client.println("Connection: close");
    client.println();
    client.println(responseText);

    client.stop(); 
    Serial.println("Client disconnected");
  }

  digitalWrite(2, WiFi.softAPgetStationNum() > 0 ? HIGH : LOW);
}
