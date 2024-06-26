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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlotOutput extends Receiver {

    // Dlaczego to są finalne pola statyczne klasy, a nie zmienne lokalne metody
    // paintComponent? W przyszłości planujemy możliwość automatycznego
    // ustalania jak duże mają być marginesy i ewentualnie ich zmiany w razie
    // potrzeby. To oznacza że powstaną metody takie jak setTopMargin itp.
    //
    private final static int topMargin = 50;
    private final static int bottomMargin = 50;
    private final static int leftMargin = 80;
    private final static int rightMargin = 50;

    // Wykres zawiera dwie osie - jedną odciętych, drugą rzędnych.
    //
    final XAxis xAxis = new XAxis();
    final YAxis yAxis = new YAxis();

    // Nazwa całego wykresu jest tu. Nazwy osi są w obiektach xAxis i yAxis.
    // Lepiej dać pusty łańcuch znaków niż null, bo null wymagałby odrębnego
    // sprawdzania, a pusty łańcuch znaków może (powinien) być bezpiecznie
    // rysowany zawsze.
    //
    private final String title = "Mój tytuł";
    private final MyCanvas canvas;

    /**
     * Konstruktor klasy PlotOutput.
     *
     * @param name nazwa odbiornika danych, która potem pozwoli zidentyfikować
     *             ten odbiornik w trakcie konfiguracji łączenia nadawców danych
     *             takich jak obiekty Sensor z odbiornikami danych takimi jak
     *             obiekty PlotOutput.
     */
    public PlotOutput(String name, Object options) throws InterruptedException, InvocationTargetException {
        super(name);

        // Tu, w PlotOutput nie możemy używać ani EventQueue.invokeLater(),
        // ani analogicznych wynalazków powiązanych z konkretną biblioteką
        // graficzną. Tu mamy być agnostyczni wobec bibliotek, więc tego rodzaju
        // synchronizację z EDT itp. musimy mieć już załatwioną w subklasie
        // klasy MyCavanas (np. w MySwingCanvas).
        //
        CanvasFactory drawingToolsFactory = SwingCanvasFactory.getInstanceDrawingToolsFactory();
        canvas = drawingToolsFactory.createCanvas();

        // @todo: dla niewielkich rozmiarów okna możliwe jest aby client_width
        //        i/lub client_height były ujemne, co doprowadzi do dziwacznych
        //        rezultatów - należałoby temu przeciwdziałać.
        //
        final int client_width = canvas.getWidth() - leftMargin - rightMargin;
        final int client_height = canvas.getHeight() - topMargin - bottomMargin;
        final int xOffset = leftMargin;
        final int yOffset = topMargin + client_height;
        final int fontHeight = canvas.getFontHeight();
        final int leading = canvas.getFontLeading();

        // Rysowanie osi. Ponieważ osie rysujemy na początku, to efektywnie
        // będą one "na spodzie" wykresu.
        //
        xAxis.paint(canvas, xOffset, yOffset, client_width, client_height);
        yAxis.paint(canvas, xOffset, yOffset, client_width, client_height);

        // Rysowanie tytułu wykresu.
        //
        final int titleWidth = canvas.getStringWidth(title);
        final int centered = xOffset + (client_width - titleWidth) / 2;
        final int above = yOffset - client_height - fontHeight - leading;
        canvas.setColor("black");
        canvas.setLineStyle("solid");
        canvas.drawString(title, centered, above);
        canvas.repaint();

        // Rysowanie danych poprzedzone zawężeniem obszaru przycinania tak,
        // aby wypadał on wyłącznie wewnątrz osi współrzędnych.
        //
        canvas.drawRect(leftMargin, topMargin, client_width, client_height);
        canvas.setClip(leftMargin, topMargin, client_width, client_height);

//@todo: to tu niepotrzebne!
//        for (PlotDataSet data : dataSets) {
//            plotter.paint(canvas, xAxis, yAxis, data);
//        }
    }


    private Random random = new Random();
    private double t = 0.0;

    @Override
    public void update(Sensor sensor) {

        Object value = sensor.getValue();
        if (value instanceof Double) {
            double temp = (double) value;
            temp = (temp - 293);
            int x = xAxis.valueToPixel(temp);
            int y = yAxis.valueToPixel(temp);
            canvas.setColor("red");
            canvas.setLineStyle("solid");
            canvas.drawRect(x, y, 3, 3);
        }

        double x_random = random.nextDouble() * 10.0;
        double y_random = random.nextDouble() * 10.0;
        int x = xAxis.valueToPixel(x_random);
        int y = yAxis.valueToPixel(y_random);
        canvas.setColor("blue");
        canvas.setLineStyle("solid");
        canvas.drawRect(x, y, 3, 3);
        canvas.repaint();
    }
}
