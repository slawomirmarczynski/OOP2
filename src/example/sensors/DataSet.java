/*
 * The MIT License
 *
 * Copyright (c) 2023 Sławomir Marczyński.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package example.sensors;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa reprezentująca zestaw danych, jakie mają być pokazywane na wykresie.
 * <p>
 * Każdy zestaw składa się z ciągu odciętych i ciągu rzędnych oraz określenia
 * koloru w jakim te dane mają być wykreślane. Oczywiście jest to tylko przykład
 * i dlatego brak w nim możliwości, które mogłyby być konieczne np. dla osób
 * mających trudności z rozpoznawaniem kolorów.
 */
public class DataSet {

    private final String name;
    private final String code;
    private final List<Double> x = new ArrayList<>();
    private final List<Double> y = new ArrayList<>();

    public DataSet(String name, String code) {
        this.name = name;
        this.code = code;
    }

    /**
     * Zwraca liczbę punktów danych.
     *
     * @return liczba punktów danych.
     */
    public int getNumberOfDataPoints() {
        return x.size();
    }

    /**
     * Zwraca wartość odciętej.
     *
     * @param index numer wierzchołka łamanej, zaczynając od zera.
     * @return wartość odciętej
     */
    public double getX(int index) {
        return x.get(index);
    }

    /**
     * Zwraca wartość rzędnej.
     *
     * @param index numer wierzchołka łamanej, zaczynając od zera.
     * @return wartość rzędnej.
     */
    public double getY(int index) {
        return y.get(index);
    }

    public String getName() {
        return name;
    }

    /**
     * Zwraca kod, zapisany w łańcuch znaków, określający w jaki sposób należy
     * wykreślić linię.
     *
     * @return kod, np. "r--o" oznacza czerwoną linię kreskowaną z zaznaczonymi
     *         jako okręgi punktami; kod ten jest zbliżony do tego którego używa
     *         się w programie Matlab i w bibliotece matplotlib w Pythonie.
     */
    public String getCode() {
        return code;
    }
}
