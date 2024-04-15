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

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * Klasa demonstrująca techniki pozyskiwania ścieżki do pliku JAR z programem,
 * katalogu/folderu roboczego, katalogu domowego (home) i pulpitu. Demonstrowane
 * są też techniki sprawdzania istnienia folderów i plików.
 */
public class DynamicPathQueries {
    public static void main(String[] args) throws URISyntaxException {
        // Pobierz ścieżkę do katalogu, w którym znajduje się plik JAR
        Path jarFilePath = Paths.get(DynamicPathQueries.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI());
        Path directoryPath = jarFilePath.getParent();
        System.out.print("Ścieżka do katalogu z plikiem JAR: " + directoryPath);
        if (directoryPath != null) {
            System.out.println();
        } else {
            System.out.println(" niepowodzenie, operacja nieudana");
        }

        String workingDirectory = System.getProperty("user.dir");
        System.out.print("Ścieżka do katalogu roboczego: " + workingDirectory);
        if (workingDirectory != null) {
            System.out.println();
        } else {
            System.out.println(" niepowodzenie, operacja nieudana");
        }

        String homeDirectory = System.getProperty("user.home");
        System.out.print("Ścieżka do katalogu domowego: " + homeDirectory);
        if (homeDirectory != null) {
            System.out.println();
        } else {
            System.out.println(" niepowodzenie, operacja nieudana");
        }

        String userProfile = System.getenv("USERPROFILE");
        System.out.print("Ścieżka do katalogu domowego (Windows): " + userProfile);
        if (userProfile != null) {
            System.out.println();
        } else {
            System.out.println(" niepowodzenie, operacja nieudana");
        }

        String propertyString = System.getProperty("user.home");
        if (propertyString != null) {
            String desktopPath = Paths.get(propertyString, "Desktop").toString();
            System.out.println("Ścieżka do pulpitu: " + desktopPath);
        } else {
            System.out.println("Ścieżka do pulpitu: null niepowodzenie");
        }

        if (workingDirectory != null) {
            boolean directoryExists;
            boolean fileExists;

            directoryExists = Files.exists(Path.of(workingDirectory));
            if (directoryExists) {
                System.out.println("Katalog istnieje: " + workingDirectory);
            } else {
                System.out.println("Katalog nie istnieje: " + workingDirectory);
            }

            fileExists = Files.exists(Paths.get(workingDirectory, "README.md"));
            if (fileExists) {
                System.out.println("Plik istnieje: README.md");
            } else {
                System.out.println("Plik nie istnieje: README.md");
            }

            fileExists = Files.exists(Paths.get(workingDirectory, "not.exist"));
            if (fileExists) {
                System.out.println("Plik istnieje: not.exist");
            } else {
                System.out.println("Plik nie istnieje: not.exist");
            }
        }
    }
}
