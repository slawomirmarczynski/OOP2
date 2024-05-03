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

public class PlotOutput extends Receiver {

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
        DrawingToolsFactory drawingToolsFactory = SwingDrawingToolsFactory.getInstanceDrawingToolsFactory();
        MyCanvas canvas = drawingToolsFactory.createCanvas();

        //@todo: To powinno być zdecydowanie w innym miejscu, tu jest tylko
        //       do prób.
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        canvas.setColor("blue");
        canvas.drawLine(0, 0, width, height);
        canvas.setColor("red");
        canvas.drawLine(width, 0, 0, height);
        canvas.setColor("green");
        String s = "Ala ma kota";
        canvas.drawString(s,
                width / 2 - canvas.getStringWidth(s) / 2,
                height / 2 + canvas.getStringHeight(s) / 2);
        canvas.repaint();
    }

    @Override
    public void update(Sensor sensor) {

    }
}
