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

import java.util.HashSet;
import java.util.Set;

// Klasa Sensor jest klasą abstrakcyjną, która reprezentuje sensor w systemie.
public abstract class Sensor extends Component {

    // Zbiór obserwatorów (odbiorców), którzy są powiadamiani o zmianach w sensorze.
    private final Set<Receiver> observers = new HashSet<>();

    // Konstruktor klasy Sensor.
    public Sensor(String name) {
        super(name);
    }

    // Metoda dodająca odbiorcę do zbioru obserwatorów.
    public void addObserver(Receiver receiver) {
        observers.add(receiver);
    }

    // Metoda usuwająca odbiorcę ze zbioru obserwatorów.
    void removeObserver(Receiver receiver) {
        observers.remove(receiver);
    }

    // Metoda usuwająca wszystkich obserwatorów.
    public void removeAllObservers() {
        observers.clear();
    }

    // Metoda powiadamiająca wszystkich obserwatorów o zmianie.
    public void notifyAllObservers() {
        for (Receiver observer : observers) {
            observer.update(this);
        }
    }

    // Metoda abstrakcyjna zwracająca wartość sensora.
    public abstract Object getValue();

    public abstract String getPhysicalParameterName();

    public abstract String getPhysicalUnit();
}
