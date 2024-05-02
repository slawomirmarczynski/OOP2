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
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

public class MySwingCanvas implements MyCanvas {

    JPanel jPanel;
    BufferedImage bufferedImage;

    public MySwingCanvas(JFrame mainWindowFrame) {
        super();
        EventQueue.invokeLater(() -> {
            int width = 400;
            int height = 300;
            Dimension dimension = new Dimension(width, height);
            jPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics graphics) {
                    graphics.drawImage(bufferedImage, 0, 0, null);
                }
            };
            jPanel.setPreferredSize(dimension);
            jPanel.setMinimumSize(dimension);
            jPanel.setMaximumSize(dimension);
            jPanel.setBorder(BorderFactory.createLineBorder(Color.RED));
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics graphics = bufferedImage.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);
            graphics.dispose();
            mainWindowFrame.setLayout(new FlowLayout());
            mainWindowFrame.add(jPanel);
            mainWindowFrame.pack();
        });
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        //EventQueue.invokeLater(()->{});
        EventQueue.invokeLater(() -> {
            Graphics graphics = bufferedImage.getGraphics();
            graphics.setColor(Color.BLACK);
            graphics.drawLine(x1, y1, x2, y2);
            graphics.dispose();
        });
    }

    @Override
    public void repaint() {
        EventQueue.invokeLater(() -> {
            jPanel.repaint();
        });

    }

    @Override
    public int getWidth() {
        final int[] width = {0};
        try {
            EventQueue.invokeAndWait(() -> {
                width[0] = jPanel.getWidth();
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return width[0];
    }

    @Override
    public int getHeight() {
        final int[] height = {0};
        try {
            EventQueue.invokeAndWait(() -> {
                height[0] = jPanel.getHeight();
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return height[0];
    }

}
