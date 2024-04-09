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

import com.google.gson.Gson;

import java.io.FileReader;
import java.util.List;
import java.util.Map;

/**
 * Klasa Configuration służy do wczytywania konfiguracji z pliku JSON.
 */
public class Configuration {

    // Domyślna nazwa pliku konfiguracyjnego.
    //
    final static String DEFAULT_FILE_NAME = "config.json";

    // Mapa przechowująca konfigurację. Zakładamy, że konfiguracja jest zapisana
    // w pliku w formacie JSON, tak że klucze takie jak "devices", "receivers",
    // "routes" itp. opisują listy takie jak lista urządzeń, lista odbiorców,
    // lista ścieżek/połączeń itd. Każda taka lista zawiera (w notacji JSON)
    // określenie nazwy, typu i ewentualnie innych parametrów obiektu takiego
    // jak lista urządzeń itd.
    //
    private final Map<String, List<?>> config;

    /**
     * Konstruktor klasy Configuration.
     */
    public Configuration() {

        try (FileReader reader = new FileReader(DEFAULT_FILE_NAME)) {

            // Do czytania plików w formacie JSON istnieje wiele bibliotek,
            // my wybraliśmy GSON od Google (bo jest za darmo i jest od Google).
            //
            // Utworzenie obiektu Gson do parsowania pliku JSON.
            //
            Gson gson = new Gson();

            // Wczytanie konfiguracji z pliku JSON do mapy.
            //
            config = gson.fromJson(reader, Map.class);

        } catch (Exception exception) {

            // W przypadku problemów podczas wczytywania konfiguracji
            // rzucany jest wyjątek.
            //
            throw new RuntimeException("błąd konfiguracji " + DEFAULT_FILE_NAME);
        }
    }

    /**
     * Metoda zwracająca listę konfiguracji urządzeń.
     *
     * @return lista obiektów, z których każdy reprezentuje konfigurację jednego
     * urządzenia.
     */
    public List<?> getDevices() {
        return config.get("devices");
    }

    /**
     * Metoda zwracająca listę konfiguracji odbiorców (receivers) danych.
     *
     * @return listę obiektów, z których każdy reprezentuje konfigurację jednego
     * odbiorcy danych.
     */
    public List<?> getReceivers() {
        return config.get("receivers");
    }

    /**
     * Metoda zwracająca, jako listę, czytelny opis połączeń (routing) pomiędzy
     * sensorami w urządzeniach a odbiorcami danych.
     *
     * @return lista list łańcuchów, z których pierwszy jest nazwą urządzenia,
     * drugi nazwą sensora, trzeci nazwą odbiorcy.
     */
    public List<List<String>> getRoutes() {
        var cfg = config.get("routes");
        return (List<List<String>>) cfg;
    }
}

