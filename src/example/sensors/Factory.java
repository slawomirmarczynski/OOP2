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
import java.util.Map;

// Klasa Factory służy do tworzenia obiektów Device, Receiver i Route na podstawie konfiguracji.
public class Factory {

    // Konfiguracja, na podstawie której będą tworzone obiekty.
    private final Configuration configuration;

    // Konstruktor klasy Factory.
    public Factory(Configuration configuration) {
        this.configuration = configuration;
    }

    // Metoda tworząca listę obiektów klasy Device na podstawie konfiguracji.
    public List<Device> createDevices() {
        List<Device> list = new ArrayList<>();
        List<?> devicesConfigurations = configuration.getDevices();
        for (var deviceConfiguration : devicesConfigurations) {
            // Tworzenie obiektu Device na podstawie konfiguracji.
            Device device = createDevice(deviceConfiguration);
            // Dodanie obiektu do listy.
            list.add(device);
        }
        return list;
    }

    // Metoda tworząca listę obiektów klasy Receiver na podstawie konfiguracji.
    public List<Receiver> createReceivers() {
        List<Receiver> list = new ArrayList<>();
        List<?> receiversConfigurations = configuration.getReceivers();
        for (var receiverConfiguration : receiversConfigurations) {
            // Tworzenie obiektu Receiver na podstawie konfiguracji.
            Receiver receiver = createReceiver(receiverConfiguration);
            // Dodanie obiektu do listy.
            list.add(receiver);
        }
        return list;
    }

    // Metoda tworząca listę obiektów klasy Route na podstawie konfiguracji.
    public List<Route> createRoutes() {
        List<Route> list = new ArrayList<>();
        List<List<String>> routesConfigurations = configuration.getRoutes();
        for (List<String> routeConfiguration : routesConfigurations) {
            // Tworzenie obiektu Route na podstawie konfiguracji.
            String deviceName = routeConfiguration.get(0);
            String sensorName = routeConfiguration.get(1);
            String receiverName = routeConfiguration.get(2);
            Route route = new Route(deviceName, sensorName, receiverName);
            // Dodanie obiektu do listy.
            list.add(route);
        }
        return list;
    }

    // Metoda tworząca obiekt klasy Device na podstawie opcji.
    private Device createDevice(Object options) {
        var optionsAsMap = (Map<String, ?>)options;
        String name = optionsAsMap.get("name").toString();
        String type = optionsAsMap.get("type").toString();
        if (type.equals("dev4b")) {
            // Tworzenie obiektu klasy Dev4b.
            return new Dev4b(name, options);
        } else {
            // W przypadku nieznanego typu urządzenia, rzucany jest wyjątek.
            throw new RuntimeException("nieznany typ urządzenia " + type);
        }
    }

    // Metoda tworząca obiekt klasy Receiver na podstawie opcji.
    private Receiver createReceiver(Object options) {
        var optionsAsMap = (Map<String, ?>)options;
        String name = optionsAsMap.get("name").toString();
        String type = optionsAsMap.get("type").toString();
        // Tworzenie obiektu klasy ConsoleOutput.
        return new ConsoleOutput(name, options);
    }
}
