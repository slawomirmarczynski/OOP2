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

package sensors.example;

import java.util.List;
import java.util.Map;


public class Program {

    /**
     * Klasa main służy do testowania koncepcji programowania obiektowego
     * do obsługi sensorów.
     *
     * @param args argumenty wywołania programu, nie są istotne dla programu.
     */
    public static void main(String[] args) {

        // Czymkolwiek jest konfiguracja, to jest to coś dość potężnego,
        // bo określa strukturę, w jaką połączą się obiekty programu
        // i jak będą one współpracować.
        //
        Configuration configuration = new Configuration();
        List<?> sourcesConfigurations = configuration.getSources();
        List<?> listenersConfigurations = configuration.getListeners();
        List<List<String>> routesConfiguration = configuration.getRoutes();

        Factory factory = new Factory();

        Map<String, Component> sources = factory.createComponentsMap(sourcesConfigurations);
        Map<String, Component> listeners = factory.createComponentsMap(listenersConfigurations);

        //@todo: W obecnej wersji w konfiguracji opisane są pary, złożone z
        //       źródła danych (sensora) i odbiorcy danych (to może być np.
        //       jakiś komponent pokazujący dane jako linie na wykresie).
        //       Być może byłoby sensowne umożliwienie podania więcej niż
        //       jednego odbiorcy (listener-a) na raz, czyli route.get(0) to
        //       nadal nadawca, ale odbiorcami są route.get(1), route.get(2)
        //       itd. Rzecz jest dość prosta do zrobienia - warto spróbować!
        for (List<String> route : routesConfiguration) {
            String sourceName = route.get(0); // from
            String listenerName = route.get(1); // to
            Component source = sources.get(sourceName);
            Component listener = listeners.get(listenerName);
            if (source == null || listener == null) {
                throw new RuntimeException("błąd konfiguracji routingu");
            } else {
                source.addObserver(listener);
            }
        }

        // while(true) ??? {
        for (int i = 0; i < 5; i++)
            for (Component source : sources.values()) {
                source.run();
            }

        //@todo: Tu jest clean-up, posprzątanie po tym co program robił.
        //       Trochę niepotrzebne, bo - jak na razie - zakończenie programu
        //       zrobi porządki samo z siebie. Jednak warto byłoby pomyśleć
        //       aby przedtem zapisać aktualną konfigurację. Dlaczego?
        //       Konfiguracja programu może się zmieniać w trakcie działania.
        //       Problemem do rozwiązania będzie taki zapis konfiguracji,
        //       aby w nawet najbardziej niekorzystnym scenariuszu (np. reset)
        //       nie było sytuacji że konfiguracja (plik z konfiguracją) została
        //       zniszczona. Być może rozwiązaniem będzie tworzenie backupu,
        //       przemyślenie jak zapisywać poszczególne etapy. Dobrym pomysłem
        //       jest trzymanie konfiguracji w bazie danych takiej jak sqlite3.
        //       Ale to nie pasuje do koncepcji używania JSON w zwykłym pliku.
        //       Samo wywołanie czegoś w rodzaju configuration.save() będzie
        //       niewystarczające - konieczne bowiem jest aby przedtem obiekt
        //       configuration został zaktualizowany, a to prawdopodobnie będzie
        //       wymagało użycia wzorców odwiedzający i pamiątka. Być może
        //       dobrym pomysłem będzie stworzenie spisu (listy?) obiektów,
        //       których stan powinien być zachowany w konfiguracji.

        for (Component source : sources.values()) {
            source.removeAllObservers();
        }
    }


}
