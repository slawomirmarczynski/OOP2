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

public class YAxis extends Axis {

    public int valueToPixel(double value) {
        return (int) Math.round(-(value - min) / (max - min) * length + offset);
    }

    @Override
    public void paint(MyCanvas canvas, int xOffset, int yOffset, int width, int height) {

        super.paint(canvas, xOffset, yOffset, width, height);

        this.offset = yOffset;
        this.length = height;

        final int labelWidth = canvas.getStringWidth(label);
        final String formatString = "%." + decimalDigits + "f";
        double value;

        value = min + minorStep;
        canvas.setColor("lightgray");
        canvas.setLineStyle("dotted");
        while (value <= max) {
            int q = valueToPixel(value);
            canvas.drawLine(xOffset, q, xOffset + width, q);
            value += minorStep;
        }
        value = min + majorStep;
        canvas.setColor("gray");
        canvas.setLineStyle("dashed");
        while (value <= max) {
            int q = valueToPixel(value);
            canvas.drawLine(xOffset, q, xOffset + width, q);
            value += majorStep;
        }

        canvas.setColor("black");
        canvas.setLineStyle("solid");
        value = min;
        while (value <= max) {
            int q = valueToPixel(value);
            canvas.drawLine(xOffset, q, xOffset - MINOR_TICK_SIZE, q);
            value += minorStep;
        }
        value = min;
        while (value <= max) {
            int q = valueToPixel(value);
            canvas.drawLine(xOffset, q, xOffset - MAJOR_TICK_SIZE, q);
            String tickLabel = String.format(formatString, value);
            int stringWidth = canvas.getStringWidth(tickLabel);
            int x = xOffset - stringWidth - MAJOR_TICK_SIZE - MINOR_TICK_SIZE;
            int y = q + ascent / 2 - 1;
            canvas.drawString(tickLabel, x, y);
            value += majorStep;
        }

        canvas.drawLine(xOffset, yOffset, xOffset, yOffset - height);

        final int xPivot = fontHeight + leading;
        final int yPivot = yOffset - (height - labelWidth) / 2;
        canvas.drawStringRotated(label, xPivot, yPivot);
    }
}
