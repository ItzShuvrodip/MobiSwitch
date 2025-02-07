#include <WiFi.h>
#include <HTTPClient.h>

#define FIREBASE_HOST "https://mobiswitch-89d10-default-rtdb.asia-southeast1.firebasedatabase.app"
#define FIREBASE_AUTH "NQ47wBW9Ff5c2We3KHW29usDByI4SAvbErOi7HAv"
#define FIREBASE_PATH "/esp32/command.json?auth=" FIREBASE_AUTH
#define FIREBASE_PATH_SSID "/esp32/wifi/ssid.json?auth=" FIREBASE_AUTH
#define FIREBASE_PATH_PASSWORD "/esp32/wifi/password.json?auth=" FIREBASE_AUTH

void connectToWiFi(String ssid, String password) {
    Serial.println("Connecting to WiFi: " + ssid);
    WiFi.begin(ssid.c_str(), password.c_str());

    while (WiFi.status() != WL_CONNECTED) {
        Serial.print(".");
        digitalWrite(2, LOW);
        delay(1000);
    }

    if (WiFi.status() == WL_CONNECTED) {
        Serial.println("\nConnected to WiFi!");
        digitalWrite(2, HIGH);
    } else {
        Serial.println("\nFailed to connect!");
    }
}

void setup() {
    Serial.begin(115200);
    pinMode(2, OUTPUT);
    pinMode(33, OUTPUT);
    
    WiFi.begin("BTS", "02468000"); // Temporary default WiFi
    Serial.print("Connecting to Default WiFi");
    while (WiFi.status() != WL_CONNECTED) {
        Serial.print(".");
        delay(1000);
    }
    Serial.println("\nConnected to Default WiFi!");

    HTTPClient http;
    
    // Fetch SSID
    http.begin(String(FIREBASE_HOST) + FIREBASE_PATH_SSID);
    int httpCode = http.GET();
    String ssid = "";
    if (httpCode == 200) {
        ssid = http.getString();
        ssid.replace("\"", "");
        Serial.println("SSID from Firebase: " + ssid);
    }
    http.end();
    http.begin(String(FIREBASE_HOST) + FIREBASE_PATH_PASSWORD);
    httpCode = http.GET();
    String password = "";
    if (httpCode == 200) {
        password = http.getString();
        password.replace("\"", "");
        Serial.println("Password from Firebase: " + password);
    }
    http.end();

    if (!ssid.isEmpty() && !password.isEmpty()) {
        connectToWiFi(ssid, password);
    }
}

void loop() {
    if (WiFi.status() == WL_CONNECTED) {
        HTTPClient http;
        String url = String(FIREBASE_HOST) + FIREBASE_PATH;

        http.begin(url);
        int httpCode = http.GET();

        if (httpCode == 200) {
            String payload = http.getString();
            Serial.println("Received: " + payload);
            if (payload=="true") {
                digitalWrite(33, HIGH);
                Serial.println("LED ON");
            } else if (payload=="false") {
                digitalWrite(33, LOW);
                Serial.println("LED OFF");
            }
        } else {
            Serial.println("Failed to connect to Firebase, HTTP Code: " + String(httpCode));
        }

        http.end();
    } else {
        Serial.println("WiFi Disconnected!");
    }

    delay(1);
}
