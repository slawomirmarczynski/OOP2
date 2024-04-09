/*
 * Copyright (c) 2024 Sławomir Marczyński. All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1. Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2. Redistributions in binary form must reproduce the
 * above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with
 * the distribution. 3. Neither the name of the copyright holder nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package example.sensors;

import java.util.ArrayList;
import java.util.List;

// Klasa Dev4b reprezentuje konkretny typ urządzenia w systemie.
public class Dev4b extends Device {

    // Lista sensorów przypisanych do tego urządzenia.
    List<Sensor> sensors = new ArrayList<>();

    // Konstruktor klasy Dev4b.
    public Dev4b(String name, Object options) {
        super(name);
        // Tworzenie sensorów i dodawanie ich do listy.
        Sensor accelerometer = new Adxl345("ADXL345");
        Sensor manometer = new Bmp180p("BMP180P");
        Sensor thermometer = new Bmp180t("BMP180T");
        sensors.add(accelerometer);
        sensors.add(manometer);
        sensors.add(thermometer);
    }

    // Metoda inicjalizująca urządzenie. W tym przypadku zawsze zwraca prawdę.
    @Override
    public boolean initialize() {
        return true;
    }

    // Metoda zwracająca listę sensorów przypisanych do tego urządzenia.
    @Override
    public List<Sensor> getSensors() {
        return sensors;
    }

    // Metoda uruchamiająca urządzenie. Wysyła powiadomienia do wszystkich obserwatorów dla każdego sensora.
    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            for (var s : sensors) {
                s.notifyAllObservers();
            }
        }
    }
}
