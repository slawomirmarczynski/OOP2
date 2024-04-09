# OOP2

## Cel projektu OOP2

Celem projektu OOP2 jest pokazanie,
na przykładzie dość prostego programu,
technik programowania obiektowego.

Program, jaki mam nadzieję powstanie,
ma umożliwić czytanie danych z rozmaitych
sensorów i przedstawianie ich w rozmaity
sposób. Oczywiście istnieją już tego
rodzaju programy. OOP2 nie jest dla nich
konkurencją. Ma jedynie służyć jako
poligon do nauki dobrych praktyk.

## Co już jest zrobione?

1. Mamy skuteczny sposób na czytanie
konfiguracji z pliku tekstowego w formacie
JSON. Format JSON wydaje się nieco łatwiejszy
do zrozumienia i użycia niż XML, TOML czy YAML.
YAML jest co prawda równie czytelny,
ale zadecydowała łatwa dostępność i prostota
użycia biblioteki GSON (do czytania plików)
oraz fakt, iż pliki YAML są mniej odporne na
przypadkowe modyfikacje (takie jak wstawienie
dodatkowych spacji) niż pliki JSON.

1. Dzięki użyciu fabryki obiektów oddzieliliśmy
tworzenie obiektów od samych obiektów.

1. Mamy sposób na powiązanie sensorów z komponentami
będącymi odbiorcami danych: stosujemy wzorzec obserwator.

## Co jest do zrobienia?

1. Uprościliśmy znacznie kod źródłowy poprzez rezygnację
z odrębnych klas Sensor i Sink. Połączyliśmy je w jedną
klasę Component. Ale być może jest to za duże uproszczenie?
Może powinniśmy jednak wprowadzić więcej klas, może
klasa Sensor powinna być subklasą klasy Component?

2. Powinniśmy zastanowić się nad tym co mają jako
wyniki przekazywać sensory? Czy mogą to być wektory?
Czy powinny być przekazywane informacje o jednostkach
(np. że pomiary są wyrażane w hektopaskalach)?

3. W jaki sposób dynamicznie ładować - jako plug-iny -
klasy obsługujące konkretne sensory? To względnie proste,
ale wymaga zapoznania się z class loaderem.

4. Testy jednostkowe dla klasy Configuration.

5. Zabezpieczenia przed błędnym plikiem config.json - nie ma bowiem gwarancji co do jego zawartości.

... i oczywiście jeszcze wiele, wiele innych rzeczy.
