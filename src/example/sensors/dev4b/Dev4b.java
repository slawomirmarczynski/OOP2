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

package example.sensors.dev4b;

import example.sensors.*;

/**
 * Klasa Dev4b reprezentuje konkretny typ urządzenia w systemie.
 * <p>
 * Choć klasa Dev4b to klasa "konkretna", to jest to jedynie atrapa klasy, jaka
 * rzeczywiście powinna być i współpracować z realnym hardware mającym prawdziwe
 * czujniki. Ciekawostka: rzeczywiście istnieje coś takiego, nazwanego roboczo
 * Dev4b, co ma czujniki ADXL345 i BMP180, mikrokontroler ATMega328P i moduł
 * Bluetooth (popularny HC-06).
 */
public class Dev4b extends Device {

    private Adxl345 accelerometer;
    private Bmp180p manometer;
    private Bmp180t thermometer;

    /**
     * Konstruktor klasy Dev4b.
     *
     * Każde urządzenie musi mieć konstruktor mający dokładnie dwa parametry:
     * nazwę, pod jaką będzie rozpoznawane urządzenie i obiekt klasy Object
     * (lub subklasy klasy Object), w którym są pozostałe opcje potrzebne
     * do stworzenia obiektu-urządzenia. W ten sposób zostaje osiągnięta pełna
     * elastyczność - w Javie każdy klasa jest subklasą klasy Object, mamy
     * swobodę w określeniu czym będzie drugi parametr.
     *
     * @param name nazwa urządzenia jako komponentu.
     * @param ignoredOptions ten parametr jest tym razem ignorowany, ale musi
     *                       być, bo program oczekuje że taki parametr będzie.
     */
    public Dev4b(String name, Object ignoredOptions) {
        super(name);

        // Tworzenie sensorów i dodawanie ich do listy. Być może sensowne byłoby
        // używanie tych zmiennych jeszcze gdzie indziej, poza konstruktorem,
        // ale na razie jest to niepotrzebne.
        //
        accelerometer = new Adxl345("ADXL345");
        manometer = new Bmp180p("BMP180P");
        thermometer = new Bmp180t("BMP180T");
        addSensor(accelerometer);
        addSensor(manometer);
        addSensor(thermometer);
    }

    /**
     * Metoda uruchamiająca urządzenie. Wysyła powiadomienia do wszystkich
     * obserwatorów dla każdego sensora.
     */
    @Override
    public void run() {
        //@todo: obecna wersja to prowizorka, służy jedynie sprawdzeniu
        //       koncepcji działania mechanizmów przekazywania danych.
        for (int i = 0; i < 100; i++) {
            try {
                accelerometer.setDemoAcceleration();
                manometer.setDemoPressure();
                thermometer.setDemoRandomTemperature();
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            notifyAllObservers();
        }
    }
}
