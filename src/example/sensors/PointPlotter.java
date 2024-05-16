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

import java.awt.*;

/**
 * Klasa PointPlotter jest prostą implementacją Plottera umiejącą rysować
 * dane jako oddzielone od siebie punkty.
 */
public class PointPlotter implements Plotter {

    /**
     * Rysuje dane jako punkty używając danych osi współrzędnych.
     *
     * @param canvas obiekt Graphics jaki dostaje z nadrzędnego paintComponent.
     * @param xAxis oś odciętych.
     * @param yAxis oś rzędnych.
     * @param data dane (ciąg punktów), jakie mają być wykreślone.
     */
    @Override
    public void paint(MyCanvas canvas, Axis xAxis, Axis yAxis, PlotDataSet data) {
        final int RADIUS = 2;
        final int DIAMETER = 2 * RADIUS;
        final int n = data.getNumberOfDataPoints();
        if (n >= 1) {
            for (int i = 0; i < n; i++) {
                int x = xAxis.valueToPixel((double)data.getX(i));
                int y = yAxis.valueToPixel((double)data.getY(i));
                canvas.setColor("white");
                canvas.fillOval(x - RADIUS, y - RADIUS, DIAMETER, DIAMETER);
                canvas.setColor(data.getColor());
                canvas.setLineStyle(data.getLineStyle());
                canvas.drawOval(x - RADIUS, y - RADIUS, DIAMETER, DIAMETER);
            }
        }
    }
}
