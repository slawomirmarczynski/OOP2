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
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.KeyStore;
import java.security.ProtectionDomain;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Klasa Factory służy do tworzenia obiektów Device, Receiver i Route
 * na podstawie konfiguracji. Nie jest to pełne zastosowanie wzorca fabryki
 * abstrakcyjnej (bo mamy tylko jedną fabrykę i to nie-abstrakcyjną),
 * ale dość łatwo (w razie potrzeby) można to rozwinąć do fabryki abstrakcyjnej.
 */
@SuppressWarnings("FieldCanBeLocal")
public class ComponentFactory {

    // Konfiguracja, na podstawie której będą tworzone obiekty.
    // Co do zasady, jeżeli potrzebna byłaby inna konfiguracja, to po prostu
    // należy stworzyć inną fabrykę.
    //
    private final Configuration configuration;

    // Potrzebne do procedur weryfikacji podpisów. Można byłoby umieścić te
    // zmienne jako zmienne lokalne (co sugeruje IDE Intellij), ale po pierwsze
    // tu są bardziej widoczne, po drugie oczywiście w wersji "produkcyjnej"
    // należałoby wybrać inne hasło i zupełnie inaczej je przechowywać itp. itd.
    //
    private final String keyStoreFileName = "myTrustStore.jks"; // w katalogu roboczym
    private final String keyStorePassword = "123456"; // ok, to tylko ćwiczenia
    private final String keyAlias = "myAlias";
    private KeyStore keyStore;

    /**
     * Konstruktor klasy Factory.
     *
     * @param configuration obiekt klasy Configuration, który dostarcza
     *                      informacji o tym jakie obiekty stworzyć. Jest to
     *                      złożona struktura map i list, taka jaką tworzy
     *                      biblioteka GSON po przeczytaniu pliku JSON.
     */
    public ComponentFactory(Configuration configuration) {
        this.configuration = configuration;
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
     * Dynamiczne tworzenie komponentu poprzez dynamiczne załadowanie klasy.
     *
     * @param classToCreate klasa obiektów (np. MyFoo.class).
     * @param name          nazwa obiektu, może być wywnioskowywana z options,
     *                      ale chcemy jawnie jej użyć do tworzenia obiektów.
     * @param type          typ obiektu, może (a może nie?) być wywnioskowana
     *                      z options, ale ją chcemy podać ją tu jawnie.
     * @param options       opcje różne; w options może być i nazwa, i typ
     *                      zapisane, ale nie są używane przez
     *                      createPluginComponent(), choć konstruktor (ładowany
     *                      dynamicznie) dostaje całe options.
     * @param <T>           klasa, która jest tą klasą, której obiekty są
     *                      tworzone, oczywiście będzie to (sub)klasa klasy
     *                      zapodanej przez classToCreate.
     * @return utworzony obiekt klasy generycznej T, czyli jakiejś klasy.
     * @throws Exception wiele różnych rzeczy może się zdarzyć,
     *                   nie jest gwarantowane że uda się utworzenie obiektu.
     */
    private <T> T createPluginComponent(Class<?> classToCreate, String name, String type, Object options)
            throws Exception {

        //@todo: Mechanizm znajdowania pluginów jaki jest poniżej działa całkiem
        //       nieźle. Jednak zamiast każdorazowo szukać plików JAR (ewentualnie
        //       CLASS) można zrobić to raz: albo poprzez leniwą inicjalizację,
        //       albo (jeszcze lepiej) w konstruktorze klasy ComponentFactory.

        String packagePrefix = "example.sensors.";
        List<URL> classLoaderURLs = new ArrayList<>();

        List<File> directories = new ArrayList<>();
        directories.add(getWorkingDirectory());
        directories.add(getProgramExecutableDirectory());
        int n = directories.size();
        for (int i = 0; i < n; i++) {
            File pluginSubdirectory = getPluginSubdirectory(directories.get(i));
            if (pluginSubdirectory != null) {
                directories.add(pluginSubdirectory);
            }
        }

        for (File directory : directories) {
            //@todo: Pozwalamy ładować pliki CLASS, a to może być problem
            //       z bezpieczeństwem, gdyż nie jest potem sprawdzane czy mają
            //       one poprawny podpis cyfrowy. Sprawdzanie podpisów cyfrowych
            //       plików CLASS nie ma sensu, bo wprowadza niepotrzebne
            //       komplikacje a przecież to samo łatwo osiągnąć dla plików
            //       JAR. Po prostu lepiej jest spakować CLASS do JAR i podpisać
            //       - niż trudzić się z wprowadzeniem sprawdzania plików CLASS.
            //       Ale jeżeli nie pozwolimy ładować plików CLASS możemy mieć
            //       utrudnienia w trakcie prac nad programem.
            //
            //@todo: Jest rzeczą ciekawą, że można zmienić nazwę pliku JAR,
            //       na przykład z myplugin.jar na myplugin.plugin (a nawet
            //       myplugin.jpeg) a i tak URLClassLoader powinien poprawnie
            //       załadować klasy z takiego pliku.
            //
            boolean UNSAFE_LOAD_FROM_CLASS_FILES = true;
            if (UNSAFE_LOAD_FROM_CLASS_FILES) {
                classLoaderURLs.add(directory.toURI().toURL());
            }
            File[] jarFiles = directory.listFiles((dir, fileName) -> fileName.endsWith(".jar"));
            if (jarFiles != null) {
                for (File jarFile : jarFiles) {
                    if (isProperlySignedJar(jarFile)) {
                        classLoaderURLs.add(jarFile.toURI().toURL());
                    }
                }
            }
        }

        try (URLClassLoader classLoader = new URLClassLoader(classLoaderURLs.toArray(new URL[0]))) {
            String pluginClassName = packagePrefix + type;
            Class<?> loadedClass = classLoader.loadClass(pluginClassName);
            if (classToCreate.isAssignableFrom(loadedClass)) {
                // Kompilator widzi tylko surowe rzutowanie typów i dlatego
                // generuje ostrzeżenie “unchecked cast”. Nie jest w stanie
                // zrozumieć, że rzutowanie jest chronione przez poprzednie
                // sprawdzenie przez isAssignableFrom().
                //
                // Nota bene: jeżeli nie ma odpowiedniego konstruktora,
                // to powstają błędy trudne to zdiagnozowania. Dynamiczne
                // ładowanie klas przez URLClassLoader jest sytuacją, w której
                // kontrola w czasie kompilacji może być trudna do osiągnięcia.
                // Rozwiązanie z użyciem metody fabrykującej, która rzuca
                // wyjątek RuntimeException w klasie bazowej, wydaje się być
                // dobrym podejściem do radzenia sobie z tym wyzwaniem.
                // Pozwala to na wyraźne sygnalizowanie problemu, jeśli subklasa
                // nie dostarcza oczekiwanego konstruktora.
                //
                @SuppressWarnings("unchecked")
                Class<T> pluginClass = (Class<T>) loadedClass;
                Constructor<T> constructor = pluginClass.getConstructor(String.class, Object.class);
                return constructor.newInstance(name, options);
            } else {
                throw new ClassCastException("Loaded class " + pluginClassName + " cannot be cast to Class<T>");
            }
        }
    }

    /**
     * Metoda sprawdzająca zgodność podpisów cyfrowych plików JAR.
     * <p>
     * Klucz do podpisu (ważny ponad 1000 lat) można, mając zaistalowane JDK, utworzyć poleceniem:
     * <p>
     * keytool -genkey -alias myAlias -keyalg rsa -validity 366000 -keystore myPrivateKeystore.jks -storepass 123456
     * <p>
     * Można, mając ten klucz, podpisywać pliki jar narzędziem jarsigner:
     * <p>
     * jarsigner -keystore myPrivateKeystore.jks -signedjar foo_signed.jar foo.jar myAlias -storepass 123456
     * <p>
     * Teraz wystarczy tylko wyeksportować klucz publiczny z keystore myPrivateKeystore.jks
     * i zaimportować do keystore myTrustedStore.jks (hasła mogą być oczywiście zupełnie różne,
     * a nawet powinny być różne, tu są jednakowe 123456):
     * <p>
     * keytool -exportcert -keystore myPrivateKeystore.jks -alias myAlias -file myKey.pub -storepass 123456
     * keytool -import -keystore myTrustStore.jks -alias myAlias -file myKey.pub -storepass 123456
     * <p>
     * Oczywiście zamiast myPrivateKeystore, myTrustStore, myAlias i myKey można
     * użyć innych nazw. Hasło "123456" jest oczywiście tylko dla przykładu
     * - w realnym przypadku potrzebne jest znacznie mocniejsze hasło!
     * Należy też pamiętać, że klucz publiczny można co do zasady swobodnie
     * udostępniać każdemu, podobnie myTrustStore.jks (gdy zawiera wyłącznie
     * zaimportowany klucz publiczny), natomiast myPrivateKeystore powinien być
     * nie udostępniany w żaden sposób.
     * <p>
     * Więcej wskazówek jest w dokumentacji narzędzi keytool i jarsigner.
     *
     * @param jarFile plik JAR do sprawdzenia.
     * @return true jeżeli podpisy są dobre, false jeżeli są złe.
     */
    private boolean isProperlySignedJar(File jarFile) {
        try {
            // Wczytywanie magazynu kluczy. Taki magazyn może być w pliku JKS,
            // ale może też być przechowywany w katalogu użytkownika, ogólnie
            // możliwe jest także że klucz jest w osobnym pliku. Rzecz w tym,
            // że jeżeli klucz nie jest w magazynie kluczy, to nie może być
            // zaufanym kluczem nie mając certyfikatu głównego. A więc nie da
            // się - bez magazynu kluczy - użyć kluczy self-signed (darmowych).
            //
            if (keyStore == null) { // leniwa inicjalizacja
                try (FileInputStream fileInputStream = new FileInputStream(keyStoreFileName)) {
                    keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                    keyStore.load(fileInputStream, keyStorePassword.toCharArray());
                }
            }

            // Weryfikowanie podpisów zawartości JAR. Zakładamy że każdy plik,
            // musi mieć prawidłowy i ważny podpis. Nieprawidłowy podpis, lub
            // brak podpisu, oznaczają że plik JAR nie może być uznany za dobry.
            //
            JarFile jar = new JarFile(jarFile);
            for (JarEntry entry : Collections.list(jar.entries())) {
                if (entry.isDirectory() == false) { // Przetwarzane są tylko nie-katalogi
                    jar.getInputStream(entry).readAllBytes();
                    Certificate[] certificates = entry.getCertificates();
                    if (certificates == null) {
                        // Nie ma podpisu? Traktujemy to jako zły podpis.
                        // Jest to konieczne aby zapobiec ewentualnej próbie
                        // obejścia, poprzez podłożenie niepodpisanych plików,
                        // mechanizmu zabezpieczeń.
                        return false;
                    }
                    for (Certificate certificate : certificates) {
                        PublicKey publicKey = keyStore.getCertificate(keyAlias).getPublicKey();
                        certificate.verify(publicKey); // zły podpis jest zgłaszany jako wyjątek
                    }
                }
            }
            return true; // wszystkie podpisy były zgodne, więc jesteśmy tu gdzie jesteśmy
        } catch (Exception exception) {
            return false; // zły podpis lub coś poszło nie tak, z ostrożności dajemy false
        }
    }

    /**
     * Metoda tworząca jeden obiekt klasy Device na podstawie opcji.
     *
     * @param options wyodrębnione opcje związane z jednym konkretnym
     *                urządzeniem.
     * @return utworzony obiekt klasy Device.
     */
    private Device createDevice(Object options) {
        try {
            @SuppressWarnings("unchecked")
            var optionsAsMap = (Map<String, ?>) options;
            String name = optionsAsMap.get("name").toString();
            String type = optionsAsMap.get("type").toString();
            return createPluginComponent(Device.class, name, type, options);
        } catch (Exception exception) {
            throw new RuntimeException("nie można utworzyć urządzenia");
        }
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
        try {
            @SuppressWarnings("unchecked")
            var optionsAsMap = (Map<String, ?>) options;
            String name = optionsAsMap.get("name").toString();
            String type = optionsAsMap.get("type").toString();
            return createPluginComponent(Receiver.class, name, type, options);
        } catch (Exception exception) {
            throw new RuntimeException("nie można utworzyć odbiornika danych");
        }
    }
}

