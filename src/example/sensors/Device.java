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

// Klasa Device jest klasą abstrakcyjną, która reprezentuje urządzenie w systemie.
public abstract class Device extends Component implements Runnable, AutoCloseable {

    // Lista wszystkich sensorów. Dzięki niej będzie można wykonywać operacje
    // zbiorczo, na wszystkich sensorach.
    //
    private final List<Sensor> sensors = new ArrayList<>();

    // Konstruktor klasy Device.
    public Device(String deviceName) {
        super(deviceName);
    }

    /**
     * Metoda inicjalizująca urządzenie.
     *
     * @return w tej wersji programu zawsze zwraca true, docelowo ma zwracać
     * true jeżeli inicjalizacja zakończy się sukcesem, a false jeżeli nie.
     */
    public boolean initialize() {
        return true;
    }

    @Override
    public void close() {
        for (var sensor : sensors) {
            sensor.removeAllObservers();
        }
        super.close();
    }

    /**
     * Metoda zwracająca listę sensorów przypisanych do tego urządzenia.
     *
     * @return lista sensorów, tj. obiektów klasy Sensor.
     */
    public List<Sensor> getSensors() {
        //@todo: Jeżeli zwracamy prywatną listę sensorów, to możliwe jest
        //       zmodyfikowanie tej listy "z zewnątrz" pomimo tego że jest ona
        //       prywatna. Programowanie defensywne wymagałoby więc zwracania
        //       sklonowanej kopii listy, ale i wtedy możliwe byłyby efekty
        //       uboczne, bo przecież obiekty Sensor służyć mają do manipulacji
        //       sensorami jako hardware.
        return sensors;
    }

    protected void addSensor(Sensor sensor) {
        sensors.add(sensor);
    }

    protected void notifyAllObservers() {
        for (var sensor : sensors) {
            sensor.notifyAllObservers();
        }
    }
}
