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

package example.miscellany;

import example.sensors.Receiver;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

/**
 * Klasa demonstrująca technikę dynamicznego ładowania klasy i dynamicznego
 * wywołania metody klasy. Aby mogła prawidłowo działać potrzebna jest klasa
 * example.sensors.Console (która jak widać nie jest w importach).
 */
public class DynamicClassLoadingExample {

    public static void main(String[] args) {

        // Aby ułatwić sobie życie (nie trzeba budować artefaktu w postaci pliku
        // JAR), możemy dynamicznie ładować klasy także z plików CLASS.
        // Wystarczy tylko zmienić ścieżkę, aby zamiast pliku JAR określała
        // folder z plikami CLASS.
        //
        // Nota bene: ./out/production jest odpowiednia dla IntelliJ i Eclipse.
        //

        // Dane, potrzebne do załadowania klasy.
        //
        String pluginClassName = "example.sensors.ConsoleOutput";
        String pluginDirectory;
        String pluginJarName;
        boolean isLoaded;

        // Ładowanie klasy z pliku CLASS, dlatego pluginJarName jest pusty.
        //
        // Próba załadowania klasy z folderu (ścieżka jest względem katalogu
        // roboczego) zwykle tworzonego przez IDE... lub, jeżeli to się nie uda,
        // z jakiegoś standardowego miejsca. Bo class loader, gdy nie może
        // wykonać swojej roboty, może się zwrócić do nadrzędnego class loadera.
        //
        pluginDirectory = "out/production";
        pluginJarName = "";
        isLoaded = load(pluginDirectory, pluginJarName, pluginClassName);
        System.out.println("isLoaded: " + isLoaded);

        // Próba załadowania klasy z pliku JAR z folderu z artefaktami.
        //
        pluginDirectory = "out/artifacts/OOP2_jar";
        pluginJarName = "OOP2.jar";
        isLoaded = load(pluginDirectory, pluginJarName, pluginClassName);
        System.out.println("isLoaded: " + isLoaded);

        // Próba załadowania klasy z pliku JAR tak jakoś, bez podania folderu.
        //
        pluginDirectory = "";
        pluginJarName = "OOP2.jar";
        isLoaded = load(pluginDirectory, pluginJarName, pluginClassName);
        System.out.println("isLoaded: " + isLoaded);

        // Próba załadowania klasy z miejsca, w którym tej klasy (tj. ani pliku
        // CLASS, ani archiwum JAR z plikami CLASS) po prostu nie ma.
        // To się może "udać" w tym sensie, że klasa może być już wcześniej
        // załadowana lub niepowodzenie URLClassLoader poskutkowało znalezieniem
        // innego sposobu załadowania potrzebnej klasy.
        //
        pluginDirectory = "X:/folder-does-not-exist";
        pluginJarName = "OOP2-file-does-not-exist.jar";
        isLoaded = load(pluginDirectory, pluginJarName, pluginClassName);
        System.out.println("isLoaded: " + isLoaded);
    }

    @SuppressWarnings("CommentedOutCode")
    private static boolean load(String pluginDirectory, String pluginJarName,
                                String pluginClassName) {

        File pluginJarFile = new File(pluginDirectory, pluginJarName);

        System.out.println();
        System.out.println("pluginDirectory: " + pluginDirectory);
        System.out.println("pluginJarName: " + pluginJarName);
        System.out.println("pluginClassName: " + pluginClassName);
        System.out.println("pluginJarFile: " + pluginJarFile);

        URL[] url;
        try {
            url = new URL[]{pluginJarFile.toURI().toURL()};
            System.out.println("url: " + Arrays.toString(url));
        } catch (MalformedURLException exception) {
            return false;
        }

        try (URLClassLoader classLoader = new URLClassLoader(url)) {
            // Załaduj klasę o nazwie przechowywanej w zmiennej pluginClassName.
            //
            Class<?> pluginClass = classLoader.loadClass(pluginClassName);

            // Uzyskaj dostęp do konstruktora obiektu określonego przez pluginClass.
            // Oczywiście można sięgać po różne konstruktory, wystarczy podać
            // odpowiednie parametry wywołując getConstructor(). Tym razem chcemy
            // mieć konstruktor odpowiedni dla wywołania z dwoma parametrami:
            // z łańcuchem znaków (czyli obiektem String) i obiektem klasy Object.
            //
            Constructor<?> constructor = pluginClass.getConstructor(String.class, Object.class);

            // Utwórz obiekt tej klasy konstruktorem o odpowiednich parametrach.
            // Wywołaj metodę o nazwie getName() i jej wynik wypisz na ekranie.
            // Efektywnie będzie to odpowiadało instrukcjom "w zwykłej Javie":
            //
            //      Receiver console1 = new ConsoleOutput(name1, null);
            //      String result1 = console1.getName();
            //      System.out.println("Wynik metody getName(): " + result1);
            //
            String name1 = "console 1 - to jest napis ćwiczebny";
            Object console1 = constructor.newInstance(name1, null);
            Method getMethod = pluginClass.getMethod("getName");
            String result = (String) getMethod.invoke(console1);
            System.out.println("Wynik metody getName(): " + result);

            // Trochę prostsze podejście, zakładając iż mamy superklasę klasy
            // ładowanej dynamicznie, a nie potrzebujemy innych metod niż tych,
            // które są w superklasie (może być nawet abstrakcyjna).
            //
            // Dobrym pomysłem byłoby sprawdzenie, czy rzutowanie na Receiver
            // jest wykonalne, czyli użycie instanceof.
            //
            String name2 = "console 2 - to też jest napis ćwiczebny";
            Receiver console2 = (Receiver) constructor.newInstance(name2, null);
            System.out.println("Wynik metody getName(): " + console2.getName());
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException | IOException exception) {
            return false;
        }

        return true;
    }
}