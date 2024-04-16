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

import example.classloading.DynamicPathQueries;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Klasa Factory służy do tworzenia obiektów Device, Receiver i Route
 * na podstawie konfiguracji. Nie jest to pełne zastosowanie wzorca fabryki
 * abstrakcyjnej (bo mamy tylko jedną fabrykę i to nie-abstrakcyjną),
 * ale dość łatwo (w razie potrzeby) można to rozwinąć do fabryki abstrakcyjnej.
 */
public class Factory {

    // Konfiguracja, na podstawie której będą tworzone obiekty.
    // Co do zasady, jeżeli potrzebna byłaby inna konfiguracja, to po prostu
    // należy stworzyć inną fabrykę.
    //
    private final Configuration configuration;

    /**
     * Konstruktor klasy Factory.
     *
     * @param configuration obiekt klasy Configuration, który dostarcza
     *                      informacji o tym jakie obiekty stworzyć. Jest to
     *                      złożona struktura map i list, taka jaką tworzy
     *                      biblioteka GSON po przeczytaniu pliku JSON.
     */
    public Factory(Configuration configuration) {
        this.configuration = configuration;
    }

    private static File getProgramExecutableDirectory() throws URISyntaxException {
        ProtectionDomain domain = DynamicPathQueries.class.getProtectionDomain();
        CodeSource codeSource = domain.getCodeSource();
        URL url = codeSource.getLocation();
        URI uri = url.toURI();
        Path path = Paths.get(uri);
        Path parent = path.getParent();
        return parent.toFile();
    }

    private static File getWorkingDirectory() {
        String workingDirectoryString = System.getProperty("user.dir");
        return new File(workingDirectoryString);
    }

    private static File getPluginSubdirectory(File file) {
        String subdirectoryName = "plugins";
        if (file != null && file.isDirectory()) {
            File subdirectory = Paths.get(file.toString(), subdirectoryName).toFile();
            if (subdirectory.exists() && subdirectory.isDirectory()) {
                return subdirectory;
            }
        }
        return null;
    }

    /**
     * Dynamiczne tworzenie komponentów.
     *
     * @param classToCreate klasa obiektów (np. MyFoo.class).
     * @param name          nazwa obiektu, może być wywnioskowywana z options,
     *                      ale chcemy aby było widać że jest używana do tworzenia
     *                      obiektów i mieć ewentualnie mieć co do niej więcej kontroli.
     * @param type          typ obiektu, może (a może nie?) być wywnioskowana z options,
     *                      ale chcemy podać ją tu jawnie.
     * @param options       opcje różne; w options może być i nazwa, i typ zapisane,
     *                      ale to nie jest używane przez createPluginComponent(),
     *                      choć konstruktor (ładowany dynamicznie) dostaje całe
     *                      options, więc coś może z tym zrobić.
     * @param <T>           klasa, która jest tą klasą, której obiekty są tworzone,
     *                      będzie to (sub)klasa klasy określonej przez classToCreate.
     * @return utworzony obiekt klasy generycznej T, czyli jakiejś klasy.
     */
    private static <T> T createPluginComponent(Class<?> classToCreate, String name, String type, Object options) {
        //@todo: zabezpieczenie przed iniekcją złośliwego lub niekompatybilnego kodu.
        String packagePrefix = "example.sensors.";
        List<URL> classLoaderURLs = new ArrayList<>();
        try {
            List<File> directories = new ArrayList<>();
            directories.add(getWorkingDirectory());
            directories.add(getProgramExecutableDirectory());
            directories.remove(null);
            int n = directories.size();
            for (int i = 0; i < n; i++) {
                File pluginSubdirectory = getPluginSubdirectory(directories.get(i));
                directories.add(pluginSubdirectory);
            }
            directories.remove(null);

            for (File directory : directories) {
                File[] jarFiles = directory.listFiles((dir, fileName) ->
                        fileName.endsWith(".jar") | fileName.endsWith(".class"));
                if (jarFiles != null) {
                    for (File jarFile : jarFiles) {
                        classLoaderURLs.add(jarFile.toURI().toURL());
                    }
                }
            }
        } catch (URISyntaxException | MalformedURLException exception) {
            throw new RuntimeException("folder z plugin'ami nie został odnaleziony");
        }

        try (URLClassLoader classLoader = new URLClassLoader(classLoaderURLs.toArray(new URL[0]))) {
            String pluginClassName = packagePrefix + type;
            Class<?> loadedClass = classLoader.loadClass(pluginClassName);
            if (classToCreate.isAssignableFrom(loadedClass)) {
                // Kompilator widzi tylko surowe rzutowanie typów i dlatego
                // generuje ostrzeżenie “unchecked cast”. Nie jest w stanie
                // zrozumieć, że rzutowanie jest chronione przez poprzednie
                // sprawdzenie przez isAssignableFrom().
                @SuppressWarnings("unchecked")
                Class<T> pluginClass = (Class<T>) loadedClass;
                Constructor<T> constructor = pluginClass.getConstructor(String.class, Object.class);
                return constructor.newInstance(name, options);
            } else {
                throw new ClassCastException("Loaded class " + pluginClassName + " cannot be cast to Class<T>");
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException | IOException exception) {
            throw new RuntimeException("nie udało się stworzyć żądanego obiektu");
        }
    }

    /**
     * Metoda tworząca listę obiektów klasy Device na podstawie konfiguracji.
     * <p>
     * Są tworzone obiekty Device, przy ich tworzeniu powstają obiekty Sensor.
     * Utworzone obiekty Device są dopisywane do listy, która jest zwracana.
     *
     * @return lista obiektów Device.
     */
    public List<Device> createDevices() {
        List<Device> list = new ArrayList<>();
        List<?> devicesConfigurations = configuration.getDevices();
        for (var deviceConfiguration : devicesConfigurations) {

            // Tworzenie obiektu Device na podstawie konfiguracji.
            //
            Device device = createDevice(deviceConfiguration);

            // Dodanie obiektu do listy.
            list.add(device);
        }
        return list;
    }

    /**
     * Metoda tworząca listę obiektów klasy Receiver na podstawie konfiguracji.
     * <p>
     * Najpierw każdy obiekt jest tworzony jako taki, potem jest dopisywany do
     * listy. Metoda ta jest w istocie bardzo podobna do createDevices(),
     * ale ponieważ Java jest taka jaka jest, to prościej (tym razem) było
     * powielić kod naruszając zasadę DRY, niż męczyć się ze stworzeniem
     * jednej-metody-która-potrafi-wszystko.
     *
     * @return lista obiektów Receivers.
     */
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

    /**
     * Metoda tworząca listę obiektów klasy Route na podstawie konfiguracji.
     * <p>
     * Tworzone są obiekty Route i dopisywane do (będącej rezultatem) listy.
     * Obiekty Route (używamy nie klasy ale rekordu, taki wynalazek od Java 11)
     * jedynie opisują jak połączenie ma być zestawione. Utworzenie obiektu
     * Route nie jest utworzeniem połączenia - tzn. nie tworzy żadnego
     * mechanizmu przekazywania danych od nadawców (sensorów) do odbiorców.
     *
     * @return lista opisująca jak zestawić połączenia pomiędzy nadawcami
     * a odbiorcami danych.
     */
    public List<Route> createRoutes() {
        List<Route> list = new ArrayList<>();
        List<List<String>> routesConfigurations = configuration.getRoutes();
        for (List<String> routeConfiguration : routesConfigurations) {

            // Tworzenie obiektu Route na podstawie konfiguracji.
            //
            String deviceName = routeConfiguration.get(0);
            String sensorName = routeConfiguration.get(1);
            String receiverName = routeConfiguration.get(2);
            Route route = new Route(deviceName, sensorName, receiverName);

            // Dodanie obiektu do listy.
            //
            list.add(route);
        }
        return list;
    }

    /**
     * Metoda tworząca jeden obiekt klasy Device na podstawie opcji.
     *
     * @param options wyodrębnione opcje związane z jednym konkretnym
     *                urządzeniem.
     * @return utworzony obiekt klasy Device.
     */
    private Device createDevice(Object options) {
        var optionsAsMap = (Map<String, ?>) options;
        String name = optionsAsMap.get("name").toString();
        String type = optionsAsMap.get("type").toString();
        return createPluginComponent(Device.class, name, type, options);
    }

    /**
     * Metoda tworząca jeden obiekt klasy Receiver na podstawie opcji.
     *
     * @param options wyodrębnione opcje związane z jednym konkretnym
     *                odbiorcą danych.
     * @return utworzony obiekt klasy Receiver.
     */
    private Receiver createReceiver(Object options) {
        // Naruszamy nieco zasadę DRY, bo podobny fragment kodu jest też
        // w metodzie createDevice. Dlaczego to robimy? Bo nie chcemy aby
        // metoda createPluginComponent musiała wiedzieć cokolwiek na temat
        // tego czym są options. Czyli options ma być dla niej "jakimś obiektem"
        // bez wchodzenia w szczegóły co w nim jest. Patrz też wzorzec memento.
        //
        var optionsAsMap = (Map<String, ?>) options;
        String name = optionsAsMap.get("name").toString();
        String type = optionsAsMap.get("type").toString();
        return createPluginComponent(Receiver.class, name, type, options);
    }
}
