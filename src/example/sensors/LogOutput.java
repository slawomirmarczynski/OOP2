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

import com.google.gson.internal.LinkedTreeMap;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Klasa tworząca obiekty zapewniająca efektywne zapisywanie danych do pliku.
 * <p>
 * Obiekty klasy LogOutput są nadal obiektami Receiver (bo klasa LogOutput jest
 * subklasą klasy Receiver, patrz także zasada Liskov), ale wyspecjalizowanymi
 * w tym aby zapisywać dane do pliku.
 */
public class LogOutput extends Receiver {

    private final String fileName;
    private PrintWriter printWriter;

    /**
     * Tworzenie obiektu o podanej nazwie i określonych parametrach.
     * Te parametry określają nazwę pliku na dane. Nazwa obiektu i nazwa pliku
     * to dwie zupełnie różne rzeczy.
     * <p>
     * Obiekty LogOutput są przeznaczone do tworzenia dynamicznie, więc IDE
     * takie jak Intellij może nieprawidłowo rozpoznawać iż nie zostały nigdzie
     * użyte.
     *
     * @param name    nazwa obiektu.
     * @param options opcje, jest to "jakiś obiekt", ale LogOutput je rozpakuje.
     * @throws RuntimeException jeżeli nie uda się utworzenie obiektu.
     */
    public LogOutput(String name, Object options) throws RuntimeException {
        super(name);
        // Są jakieś problemy z rzutowaniem LinkedTreeMap do TreeMap,
        // po prostu Google robiąc LinkedTreeMap zrobiło coś inaczej niż
        // mogło zrobić.
        //
        // Ewentualne problemy z rzutowaniem - że parametry będą zupełnie
        // czymś innym, że nie będzie parametru "file" itp. - spowodują
        // wyjątek, a to zostanie przechwycone przez try-catch.
        // Dlatego nie ma zwyczajowego sprawdzania przez instanceof czy
        // rzutowanie da się przeprowadzić pomyślnie. Bo gdyby nie dało się,
        // to i tak trzeba byłoby rzucić wyjątkiem.
        //
        // Jest tylko odzyskiwana nazwa pliku, samo otwarcie pliku będzie
        // realizowane przez leniwą inicjalizację.
        //
        try {
            @SuppressWarnings("unchecked")
            LinkedTreeMap<String, ?> treeMap = (LinkedTreeMap<String, ?>) options;
            fileName = treeMap.get("file").toString();
            //
            // Bardzo tradycyjne otwarcie pliku z użyciem java.io, można
            // byłoby krócej, ale chcemy wymusić kodowanie UTF-8 (nota bene
            // charset i tak bierzemy z java.nio). Plik pozostanie otwarty
            // przez cały czas, aby zapewnić maksymalną wydajność.
            //
            // @todo: Użycie nio (lub nio2) dałoby możliwość zastosowania
            //        operacji nieblokujących, a tym samym mogłoby być może
            //        przyspieszyć działanie programu.
            //
            File file = new File(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(bufferedOutputStream,
                    StandardCharsets.UTF_8);
            printWriter = new PrintWriter(outputStreamWriter);
        } catch (Exception exception) {
            throw new RuntimeException("nie można utworzyć obiektu LogOutput");
        }
    }

    /**
     * Nadpisana metoda close.
     */
    @Override
    public void close() {

        // Tu mała ciekawostka. Najpierw stosujemy odziedziczone zamykanie.
        // To jest oczywista ostrożność. Potem jednak zamykamy tylko
        // printWriter, a przecież "otwieraliśmy" masę strumieni tak jakoś.
        // No właśnie niezupełnie. Java stosuje wzorzec dekorator w java.io.
        // Wywołania, takie jak new BufferedStream, nie otwierały pliku,
        // a tylko dodawały nowe możliwości pracy z nim. Wywołanie close()
        // obiektu printWriter zamknie wszystko to co jest do zamknięcia
        // i co było podpięte pod printWriter.
        //
        printWriter.close();
        printWriter = null;
        super.close();
    }

    @Override
    public void update(Sensor source) {

        if (printWriter == null) {
            return;
        }

        // Pobieranie nazwy sensora, wartości, nazwy parametru fizycznego
        // i jednostki parametru fizycznego.
        //
        String sensorName = source.getName();
        Object value = source.getValue();
        String physicalParameterName = source.getPhysicalParameterName();
        String physicalParameterUnit = source.getPhysicalUnit();

        // Wypisanie części informacji do pliku w osobnej linii. Linia ta nie
        // jest jeszcze zakończona znakiem nowej linii, będzie kontynuowana.
        //
        printWriter.printf("Sensor %s, %s [%s]: ",
                sensorName, physicalParameterName, physicalParameterUnit);

        // Wypisywanie wartości odczytu, w tej samej linii co poprzedni wpis.
        //
        // Jeżeli wartość jest typu Double, to wypisujemy ją. Jeżeli natomiast
        // wartość jest tablicą typu Double, czyli wektorem, to wypisujemy jej
        // elementy rozdzielone przecinkami.
        //
        if (value instanceof Double) {
            printWriter.println(value);
        } else if (value instanceof Double[]) {
            printWriter.println(Arrays.toString((Double[]) value));
        }
    }
}
