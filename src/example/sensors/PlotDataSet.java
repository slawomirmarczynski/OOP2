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
 * Każdy zestaw składa się z ciągu odciętych i ciągu rzędnych oraz
 * nazwy opisujacej te dane i określenia sposobu wykreślania.
 */
public class PlotDataSet {

    private final String name;
    private final List xValues = new ArrayList();
    private final List yValues = new ArrayList();
    private String color;
    private String lineType;

    public PlotDataSet(String name, String color, String lineType) {
        this.name = name;
        this.color = color;
        this.lineType = lineType;
    }

    /**
     * Zwraca liczbę punktów danych.
     *
     * @return liczba punktów danych.
     */
    public int getNumberOfDataPoints() {
        return xValues.size();
    }

    /**
     * Zwraca wartość odciętej.
     *
     * @param index numer wierzchołka łamanej, zaczynając od zera.
     * @return wartość odciętej
     */
    public Object getX(int index) {
        return xValues.get(index);
    }

    /**
     * Zwraca wartość rzędnej.
     *
     * @param index numer wierzchołka łamanej, zaczynając od zera.
     * @return wartość rzędnej.
     */
    public Object getY(int index) {
        return yValues.get(index);
    }

    public String getName() {
        return name;
    }

    public void append(Object x, Object y) {
        xValues.add(x);
        yValues.add(y);
    }

    public void clear() {
        xValues.clear();
        yValues.clear();
    }

    public String getColor() {
        return color;
    }

    public String getLineStyle() {
        return lineType;
    }

    public Double[] getXs() {
        List<Double> list = xValues;
        return list.toArray(new Double[0]);
    }
}
