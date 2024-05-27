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

/**
 * Klasa Sensor jest klasą abstrakcyjną, która reprezentuje sensor w systemie.
 */
public abstract class Sensor extends Component {

    // Zbiór obserwatorów (odbiorców), którzy są powiadamiani o zmianach w sensorze.
    //
    private final Set<Receiver> observers = new HashSet<>();

    /**
     * Konstruktor klasy Sensor.
     *
     * @param name nazwa sensora, która jest określana przez konfigurację programu,
     *             powinna być unikalna, bo przeznaczona jest do określenia sposobu
     *             przepływu danych w programie.
     */
    public Sensor(String name) {
        super(name);
    }

    /**
     * Metoda dodająca jednego odbiorcę do zbioru obserwatorów.
     *
     * @param receiver odbiorca, czyli obiekt mający być obserwatorem.
     */
    public void addObserver(Receiver receiver) {
        observers.add(receiver);
    }

    /**
     * Metoda usuwająca odbiorcę ze zbioru obserwatorów.
     *
     * @param receiver obserwator do usunięcia.
     */
    void removeObserver(Receiver receiver) {
        synchronized (this) {
            observers.remove(receiver);
        }
    }

    //
    //

    /**
     * Metoda usuwająca wszystkich obserwatorów.
     *
     * Ważne: synchronizacja ma chronić przed niejednoznacznością działania
     *        dwóch wątków, z których jeden informuje prenumeratorów,
     *        a drugi jednocześnie ich usuwa. Chroni też przed ewentualnym
     *        usuwaniem usuniętego (gdyby clear() nie była operacją atomową).
     */
    public void removeAllObservers() {
        synchronized (this) {
            observers.clear();
        }
    }

    /**
     * Metoda powiadamiająca wszystkich obserwatorów o zmianie.
     */
    public void notifyAllObservers() {
        synchronized (this) {
            for (Receiver observer : observers) {
                observer.update(this);
            }
        }
    }

    /**
     * Metoda abstrakcyjna zwracająca wartość wielkości fizycznej mierzonej
     * za pomocą danego sensora. Wartość ta, w subklasach, może być skalarem
     * (liczbą Double), może być wektorem (tablicą liczb Double[]), może też
     * być czymś innym. Wybierając Object jako typ zwracany przez metodę
     * superklasy możemy niemal dowolnie przedefiniować typ wartości zwracanej
     * przez subklasę. Jedyne co musi, to musi być obiektem.
     */
    public abstract Object getValue();

    /**
     * Aby nie było wątpliwości co jest mierzone, subklasy powinny zdefiniować
     * tę metodę tak, aby dostarczyć czytelnej informacji możliwej do użycia
     * jako np. opis osi liczbowej.
     *
     * @return napis taki jak np. "przyspieszenie", ew. "acceleration".
     * Oczywiście tekst napisu zależeć będzie od tego jaką wielkość fizyczną
     * mierzy sensor.
     */
    public abstract String getPhysicalParameterName();

    /**
     * Napis określajacy jednostki fizyczne, np. "m/s**2".
     *
     * Uwaga: możliwe jest, że różne sensory będą informowały o tych samych
     *        jednostkach w różny sposób, np. "m/s**2" i "m/s^2" lub nawet
     *        "m * s^(-2)" itp. Czyli identyczne łańcuch znaków to identyczne
     *        jednostki, różne napisy to niekoniecznie różne jednostki.
     *
     * @return napis, informujący o użytych jednostkach.
     */
    public abstract String getPhysicalUnit();

    /**
     * Znacznik czasowy informujący, kiedy przeprowadzony był pomiar.
     *
     * @return ilość milisekund od chwili startu programu do czasu w którym
     *         sensor wykonał pomiar dostępny przez getValue().
     */
    public abstract long getTimeStamp();
}
