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

package example.classloading;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Klasa demonstrująca techniki pozyskiwania ścieżki do pliku JAR z programem,
 * katalogu/folderu roboczego, katalogu domowego (home) i pulpitu. Demonstrowane
 * są też techniki sprawdzania istnienia folderów i plików. Pokazane jest też
 * pozyskiwanie informacji o wersji Javy i maszynie wirtualnej.
 */
public class DynamicPathQueries {
    public static void main(String[] args) throws URISyntaxException {

        // Odstęp, dla poprawy czytelności wyników.
        //
        System.out.println();

        // Pobieranie i wyświetlanie nazwy JVM
        //
        String javaVmName = System.getProperty("java.vm.name");
        display("Nazwa JVM", javaVmName);

        // Wersja Javy
        //
        String javaVersion = System.getProperty("java.version");
        display("java.version", javaVersion);

        // Pobieranie JAVA_HOME
        //
        String javaHome = System.getenv("JAVA_HOME");
        display("JAVA_HOME", javaHome);

        // Pobieranie classpath
        //
        String classpath = System.getProperty("java.class.path");
        display("classpath", classpath);

        // Pobieranie ścieżki do folderu z plikami CLASS lub do pliku JAR.
        // W tym folderze mogą być pliki CLASS, mogą też być foldery z plikami
        // CLASS (rekurencyjnie, foldery mają nazwy odzwierciedlające nazwy
        // pakietów). Pliki w JAR w Javie (np. program.jar) są to spakowane
        // archiwa zawierające pliki CLASS, więc jeżeli program jest w pliku
        // JAR, to dostaniemy w ten sposób nazwę pliku JAR.
        //
        Path jarFilePath = Paths.get(DynamicPathQueries.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI());
        boolean isJar = new File(jarFilePath.toString()).isFile();
        if (isJar) {
            System.out.println("Ścieżka do pliku JAR: " + jarFilePath);
        } else {
            System.out.println("Ścieżka do folderu z plikami CLASS: " + jarFilePath);
        }

        // Pobieranie ścieżki do katalogu nadrzędnego, tj. do tego w którym
        // rezyduje plik JAR (lub folder, gdy to był nie JAR, ale plik CLASS).
        //
        Path directoryPath = jarFilePath.getParent();
        display("Ścieżka do katalogu z programem", directoryPath.toString());

        // Pobieranie ścieżki do bieżącego katalogu roboczego.
        //
        String workingDirectory = System.getProperty("user.dir");
        display("Ścieżka do katalogu roboczego", workingDirectory);

        // Pobieranie ścieżki do katalogu domowego użytkownika.
        //
        String homeDirectory = System.getProperty("user.home");
        display("Ścieżka do katalogu domowego", homeDirectory);

        // Pobierz ścieżkę do katalogu domowego użytkownika (Windows)
        //
        String userProfile = System.getenv("USERPROFILE");
        display("Ścieżka do katalogu domowego (Windows)", userProfile);

        // Pobieranie ścieżki do pulpitu zaczynamy od folderu domowego.
        // To rozwiązanie działa dobrze także i w MS Windows (Windows 10),
        // można je ewentualnie uzupełnić o System.getenv("USERPROFILE"),
        // patrz wyżej.
        //
        String propertyString = System.getProperty("user.home");
        String desktopPath = null;
        if (propertyString != null) {
            // Określanie ścieżki do pulpitu użytkownika, po prostu folder
            // Desktop w katalogu użytkownika. Nazwa Desktop jest dobrze
            // rozumiana przez MS Windows nawet wtedy, gdy jest ona prezentowana
            // jako Pulpit (czyli w tłumaczeniu na język polski).
            //
            desktopPath = Paths.get(propertyString, "Desktop").toString();
        }
        display("Ścieżka do pulpitu", desktopPath);

        if (workingDirectory != null) {
            boolean directoryExists;
            boolean fileExists;

            // Sprawdzanie, czy katalog roboczy istnieje.
            //
            directoryExists = Files.exists(Path.of(workingDirectory));
            if (directoryExists) {
                System.out.println("Katalog istnieje: " + workingDirectory);
            } else {
                System.out.println("Katalog nie istnieje: " + workingDirectory);
            }

            // Sprawdzanie, czy plik README.md istnieje w katalogu roboczym.
            //
            fileExists = Files.exists(Paths.get(workingDirectory, "README.md"));
            if (fileExists) {
                System.out.println("Plik istnieje: README.md");
            } else {
                System.out.println("Plik nie istnieje: README.md");
            }

            // Sprawdzanie, czy plik not.exist istnieje w katalogu roboczym.
            //
            fileExists = Files.exists(Paths.get(workingDirectory, "not.exist"));
            if (fileExists) {
                System.out.println("Plik istnieje: not.exist");
            } else {
                System.out.println("Plik nie istnieje: not.exist");
            }
        }
    }

    /**
     * Wyświetlanie opisanej wartości lub komunikatu o błędzie (jeżeli null).
     *
     * @param description opis
     * @param value       wartość
     */
    private static void display(String description, String value) {
        System.out.print(description + ": " + value);
        if (value != null) {
            System.out.println();
        } else {
            System.out.println(" niepowodzenie, operacja nieudana");
        }
    }
}
