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
    Graphics2D graphics;

    public MySwingCanvas(JFrame mainWindowFrame) {
        super();
        try {
            EventQueue.invokeAndWait(() -> {
                int width = 400;
                int height = 300;
                Dimension dimension = new Dimension(width, height);
                jPanel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics graphics) {
                        //graphics.drawImage(bufferedImage, 0, 0, null);
                        graphics.drawImage(bufferedImage,
                                0, 0, width, height,
                                0, 0, 2*width, 2*height,
                                null);
                    }
                };
                jPanel.setPreferredSize(dimension);
                jPanel.setMinimumSize(dimension);
                jPanel.setMaximumSize(dimension);
                jPanel.setBorder(BorderFactory.createLineBorder(Color.RED));
                bufferedImage = new BufferedImage(2*width, 2*height, BufferedImage.TYPE_INT_RGB);
                graphics = (Graphics2D)bufferedImage.getGraphics();
                graphics.setColor(Color.WHITE);
                graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
                graphics.scale(2.0, 2.0);

                // Bez włączenia antyaliasingu obraz nie jest zbyt ładny, włączamy
                // antyaliasing, co na współczesnych komputerach nie będzie problemem.
                //
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                graphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
                graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, 150);
                // Dlaczego nie ma tu  graphics.dispose() ?
                // Bo łatwiej jest mieć cały czas dostępny obiekt graphics,
                // niż tworzyć go za każdym razem na nowo. Nie jest to kłopotliwe,
                // zwłaszcza że obiektów klasy MyCanvas będzie niewiele.
                mainWindowFrame.setLayout(new FlowLayout());
                mainWindowFrame.add(jPanel);
                mainWindowFrame.pack();
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        graphics.drawLine(x1, y1, x2, y2);
    }

    @Override
    public int getStringWidth(String text) {
        Font font = graphics.getFont();
        FontMetrics metrics = graphics.getFontMetrics(font);
        return metrics.stringWidth(text);
    }

    @Override
    public int getStringHeight(String text) {
        Font font = graphics.getFont();
        FontMetrics metrics = graphics.getFontMetrics(font);
        return metrics.getHeight();
    }

    @Override
    public void drawString(String string, int x, int y) {
        graphics.drawString(string, x, y);
    }

    @Override
    public void repaint() {
        EventQueue.invokeLater(() -> jPanel.repaint());
    }

    @Override
    public int getWidth() {
        return bufferedImage.getWidth() / 2;
    }

    @Override
    public int getHeight() {
        return bufferedImage.getHeight() / 2;
    }

    @Override
    public void setColor(String colorName) {
        Color color = switch (colorName.toLowerCase()) {
            case "red" -> Color.RED;
            case "green" -> Color.GREEN;
            case "blue" -> Color.BLUE;
            default -> Color.BLACK;
        };
        graphics.setColor(color);
    }
}
