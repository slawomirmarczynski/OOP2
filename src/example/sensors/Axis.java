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

/**
 * Axis jest ogólnie osią liczbową, bez określania jeszcze czy jest to oś
 * odciętych, czy oś rzędnych. Niektóre rzeczy najprościej robić zupełnie
 * inaczej dla osi x, a inaczej dla osi y. Niektóre jednak są takie same
 * i można je umieścić w klasie bazowej, jaką jest Axis.
 */
public abstract class Axis {

    protected final static int MAJOR_TICK_SIZE = 10; // długość kresek podziałki głównej
    protected final static int MINOR_TICK_SIZE = 5;  // długość kresek podziałki drobnej

    protected double min = 0.;  // minimalna wartość na osi
    protected double max = 10.; // maksymalna wartość na osi
    protected double majorStep = (max - min) / 4.0; // skok podziałki głównej
    protected double minorStep = majorStep / 10.0;  // skok podziałki drobnej
    protected int decimalDigits = 2; // liczba cyfr znaczących

    protected String label = "label"; // opis osi, lepiej domyślny pusty niż null

    protected double offset; // w pikselach
    protected double length; // w pikselach

    protected int fontHeight;
    protected int ascent;
    protected int descent;
    protected int leading;

    /**
     * Abstrakcyjna funkcja przeliczająca wartości realne (takie jak np.
     * prędkość w km/h) na współrzędną wyrażoną w pikselach w oknie Swing.
     *
     * @param value wartość jako liczba rzeczywista
     * @return współrzędna wyrażona w pikselach w oknie Swing.
     */
    public abstract int valueToPixel(double value);

    /**
     * Abstrakcyjna procedura odmalowywania osi współrzędnej. Ponieważ okno
     * w którym odrysowuje się oś może zmieniać rozmiary to położenie początku
     * współrzędnych i rozmiary ramki wykresu są przekazywane bezpośrednio
     * jako parametry wywołania.
     *
     * @param canvas na tym rysujemy.
     * @param xOffset położenie początku układu współrzędnych w pikselach.
     * @param yOffset położenie początku układu współrzędnych w pikselach.
     * @param width szerokość wykresu będąca długością osi x.
     * @param height wysokość wykresu będąca długością osi y.
     */
    public void paint(MyCanvas canvas, int xOffset, int yOffset, int width, int height){
        fontHeight = canvas.getFontHeight();
        ascent = canvas.getFontAscent();
        descent = canvas.getFontDescent();
        leading = canvas.getFontLeading();
    }

    /**
     * Ustawianie minimalnej wartości na osi.
     *
     * @param min dowolna wartość.
     */
    public void setMin(double min) {
        // @todo: sprawdzanie czy this.min jest mniejsze niż this.max
        this.min = min;
    }

    /**
     * Ustawianie maksymalnej wartości na osi.
     *
     * @param max dowolna wartość.
     */
    public void setMax(double max) {
        // @todo: sprawdzanie czy this.min jest mniejsze niż this.max
        this.max = max;
    }

    /**
     * Ustawianie głównego kroku podziałki.
     *
     * @param majorStep dowolna wartość, powinna być większa niż zero.
     */
    public void setMajorStep(double majorStep) {
        this.majorStep = majorStep;
    }

    /**
     * Ustawianie drobnego kroku podziałki.
     *
     * @param minorStep dowolna wartość, powinna być większa niż zero.
     */
    public void setMinorStep(double minorStep) {
        this.minorStep = minorStep;
    }

    /**
     * Ustawianie liczby cyfr znaczących.
     *
     * @param decimalDigits liczba cyfr znaczących powinna być nieujemna.
     */
    public void setDecimalDigits(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    /**
     * Ustawianie etykiety/nazwy osi.
     *
     * @param label łańcuch znaków.
     */
    public void setLabel(String label) {
        this.label = label != null ? label : "";
    }
}
