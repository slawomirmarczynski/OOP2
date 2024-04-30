By# OOP2

## Cel projektu OOP2

Celem projektu OOP2 jest pokazanie, na przykładzie dość prostego programu,
technik programowania obiektowego.

Program, jaki mam nadzieję powstanie, ma umożliwić czytanie danych z rozmaitych
sensorów i przedstawianie ich w rozmaity sposób. Oczywiście istnieją już tego
rodzaju programy. OOP2 nie jest dla nich konkurencją. Ma jedynie służyć jako
poligon do nauki dobrych praktyk.

## Co już jest zrobione?

1. Dodane jest sprawdzanie podpisów plików JAR (podpisy self-signed tworzone
   są programami keytool i jarsigner pochodzącymi z JDK).

1. Dynamiczne ładowanie klas jest już zaimplementowane bez mechanizmów
   zabezpieczających.

1. Dodany jest przykład jak pozyskać ścieżki do katalogu roboczego, katalogu
   użytkownika itp. 

1. Dodany jest przykład dynamicznego ładowania klas i wywoływania ich metod.
   Posłuży on do opracowania takiego sposobu ładowania sensorów i odbiorców
   danych, aby uczynić z nich łatwo konfigurowalne pluginy.

1. Uprościliśmy znacznie kod źródłowy poprzez rezygnację z odrębnych klas
   Sensor i Sink. Połączyliśmy je w jedną klasę Component. Ale okazało się to za
   dużym uproszczeniem. Dlatego wprowadziliśmy więcej klas i klasy abstrakcyjne.
   Mamy abstrakcyjne klasy Device, Sensor i Receiver.
   - Device reprezentuje jakieś urządzenie, mające wiele sensorów, w jakiś
     sposób (WiFi, Bluetooth, USB lub jeszcze inaczej) komunikujące się
     z komputerem, a więc przysyłające dane pomiarowe.
   - Sensor reprezentuje sensor dostarczający danych o jednym konkretnym
     parametrze fizycznym, takim jak ciśnienie, temperatura, przyspieszenie.
   - Receiver jest czymś, co jest odbiorcą-konsumentem danych.

1. Mamy sposób na powiązanie sensorów z komponentami będącymi odbiorcami danych:
   stosujemy wzorzec obserwator.

1. Dzięki użyciu fabryki obiektów (klasa Factory) oddzieliliśmy tworzenie
   obiektów od samych obiektów.

1. Mamy skuteczny sposób na czytanie konfiguracji z pliku tekstowego w formacie
   JSON. Format JSON wydaje się nieco łatwiejszy do zrozumienia i użycia niż XML,
   TOML czy YAML. YAML jest co prawda równie czytelny jak JSON, ale zadecydowała
   łatwa dostępność i prostota użycia biblioteki GSON (do czytania plików)
   oraz fakt, iż pliki YAML są mniej odporne na przypadkowe modyfikacje (takie jak
   wstawienie dodatkowych spacji) niż pliki JSON.

## Co jest do zrobienia?

1. Graficzny interfejs użytkownika. Być może z możliwością wyboru pomiędzy
   Swing (SWT?) i JavaFX.

1. Powinniśmy zastanowić się nad tym co mają jako wyniki przekazywać sensory?
Czy mogą to być wektory? Czy powinny być przekazywane informacje o jednostkach
(np. że pomiary są wyrażane w hektopaskalach)? Czy przekazywać ostatnio
zmierzone dane, czy też tylko najświeższe? Buforować dane?

3. Testy jednostkowe - to ważne, ale na razie odkładamy na później.

4. Zabezpieczenia przed błędnym plikiem config.json - nie ma bowiem gwarancji
co do jego zawartości.

... i oczywiście jeszcze wiele, wiele innych rzeczy.
