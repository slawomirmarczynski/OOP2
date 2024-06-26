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

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class SwingCanvasFactory implements CanvasFactory {

    private static SwingCanvasFactory swingToolsFactory = null;
    private JFrame mainWindowFrame;

    public static synchronized SwingCanvasFactory getInstanceDrawingToolsFactory()
            throws InterruptedException, InvocationTargetException {
        if (swingToolsFactory == null) {
            swingToolsFactory = new SwingCanvasFactory();
        }
        return swingToolsFactory;
    }

    private SwingCanvasFactory() throws InterruptedException, InvocationTargetException {
        EventQueue.invokeAndWait(() -> {
            mainWindowFrame = new JFrame("Program do obsługi sensorów");
            mainWindowFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainWindowFrame.setSize(640, 480);  // 640x480 to historyczna wartość dla PC
            mainWindowFrame.setVisible(true);
        });
    }

    @Override
    public MyCanvas createCanvas() {
        return new MySwingCanvas(mainWindowFrame);
    }

}
