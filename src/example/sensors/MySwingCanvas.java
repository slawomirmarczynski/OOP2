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
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

public class MySwingCanvas implements MyCanvas {

    private JPanel jPanel;
    private BufferedImage bufferedImage;
    private Graphics2D graphics;
    private float uiScale;

    public MySwingCanvas(JFrame mainWindowFrame) {
        super();

        // Odzyskiwanie współczynnika skalowania interfejsu przekazywanego
        // do JVM jako parametr -Dsun.java2d.uiScale=2
        //
        // JVM ma (może mieć, od Java 9, ale być może są też JVM bez tej opcji)
        // opcję skalowania interfejsów użytkownika tworzonych w Swing, tak że
        // możliwe jest zapewnienie komfortowej pracy na monitorach mających
        // dużą rozdzielczość (czyli dużą ilość pikseli na cal, tzw. DPI).
        //
        // Problem w tym, że będziemy rysować na bitmapie, a bitmapa nie wie
        // nic o skalowaniu interfejsu GUI. Jeżeli polecimy utworzyć JPanel
        // o rozmiarze 400 x 300 pikseli przy skalowaniu 2.0 (to jest dwukrotne
        // powiększenie), to de facto JPanel będzie miał rozmiar 800 x 600.
        // Gdybyśmy stworzyli bitmapę o rozmiarze takim jaki (wewnątrz programu)
        // raportuje JPanel to mielibyśmy bitmapę 400 x 300 pikseli. Ta bitmapa
        // wrzucona na ekran do JPanel byłaby przeskalowana w górę na 800 x 600,
        // ale z utratą jakości obrazu. Włączenie antyaliasingu nieco pomogłoby,
        // jednak obraz byłby nieco rozmyty. Dlatego, aby mieć idealny obraz,
        // tworzymy bitmapę przeskalowaną do rzeczywistych rozmiarów na ekranie.
        // Tę bitmapę przenosimy (rysujemy) na JPanel. Musimy też użyć scale()
        // by przeskalować operacje rysowania na tej bitmapie.
        //
        // Dodatkowy problem to jak (i czy jest sens) zrobić obsługę dynamicznej
        // zmiany skali możliwej dzięki System.setProperty().
        // W obecnej wersji nie jest to zrobione, program czyta property tak jak
        // poniżej, ale potem nie reaguje na manipulacje skalowaniem.
        //
        try {
            String uiScaleString = System.getProperty("sun.java2d.uiScale");
            uiScale = Float.parseFloat(uiScaleString);
        } catch (Exception ignoredException) {
            // Dlaczego jesteśmy tutaj? Bo albo nie udało się odczytać property,
            // albo w property było wpisane coś dziwnego (da się, sprawdzone).
            uiScale = 1.0f;
        }
        // Jeżeli chcemy zobaczyć czy warto trudzić się z uiScale, to wystarczy
        // odkomentować kolejną linię kodu.
        //
        // uiScale = 1.0f;

        try {
            EventQueue.invokeAndWait(() -> {
                int panelWidth = 300;
                int panelHeight = 200;
                int bitmapWidth = (int) (panelWidth * uiScale); //@todo: a co jeżeli otrzymamy 0 ?
                int bitmapHeight = (int) (panelHeight * uiScale); //@todo: a co jeżeli otrzymamy 0 ?
                Dimension dimension = new Dimension(panelWidth, panelHeight);
                jPanel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics graphics) {
                        super.paintComponent(graphics);
                        graphics.drawImage(bufferedImage, 0, 0, panelWidth, panelHeight, 0, 0, bitmapWidth, bitmapHeight, null);
                    }
                };
                jPanel.setPreferredSize(dimension);
                jPanel.setMinimumSize(dimension);
                jPanel.setMaximumSize(dimension);
                jPanel.setBackground(Color.WHITE);
                Color borderColor = UIManager.getColor("windowBorder");
                if (borderColor == null) {
                    borderColor = Color.LIGHT_GRAY;
                }
                jPanel.setBorder(BorderFactory.createLineBorder(borderColor));
                //jPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                // Oszczędnościowo BufferedImage.TYPE_USHORT_565_RGB
                bufferedImage = new BufferedImage(bitmapWidth, bitmapHeight, BufferedImage.TYPE_3BYTE_BGR);
                graphics = (Graphics2D) bufferedImage.getGraphics();
                graphics.setBackground(Color.WHITE);
                graphics.clearRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
                graphics.scale(uiScale, uiScale);
                adjustRenderingHints(graphics);
                mainWindowFrame.setLayout(new FlowLayout());
                mainWindowFrame.add(jPanel);
                mainWindowFrame.pack();

                // Dlaczego nie ma tu  graphics.dispose() ?
                // Bo łatwiej jest mieć cały czas dostępny obiekt graphics,
                // niż tworzyć go za każdym razem na nowo. Nie jest to kłopotliwe,
                // zwłaszcza że obiektów klasy MyCanvas będzie niewiele.
            });
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void drawLine(int x1, int y1, int x2, int y2) {

        graphics.drawLine(x1, y1, x2, y2);
    }

    @Override
    public int getFontHeight() {
        Font font = graphics.getFont();
        FontMetrics metrics = graphics.getFontMetrics(font);
        return metrics.getHeight();
    }

    @Override
    public int getFontAscent() {
        Font font = graphics.getFont();
        FontMetrics metrics = graphics.getFontMetrics(font);
        return metrics.getAscent();
    }

    @Override
    public int getFontDescent() {
        Font font = graphics.getFont();
        FontMetrics metrics = graphics.getFontMetrics(font);
        return metrics.getDescent();
    }

    @Override
    public int getFontLeading() {
        Font font = graphics.getFont();
        FontMetrics metrics = graphics.getFontMetrics(font);
        return metrics.getLeading();
    }

    @Override
    public int getStringWidth(String text) {
        Font font = graphics.getFont();
        FontMetrics metrics = graphics.getFontMetrics(font);
        return metrics.stringWidth(text);
    }

    @Override
    public void drawString(String string, int x, int y) {
        graphics.drawString(string, x, y);
    }

    @Override
    public void drawStringRotated(String text, int x, int y) {
        Graphics2D rotated_graphics = (Graphics2D) graphics.create();
        rotated_graphics.rotate(Math.toRadians(-90.0), x, y);
        rotated_graphics.drawString(text, x, y);
        rotated_graphics.dispose();
    }

    @Override
    public void repaint() {
        EventQueue.invokeLater(() -> jPanel.repaint());
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {
        graphics.drawRect(x, y, width, height);
    }

    @Override
    public void setClip(int x, int y, int width, int height) {
        graphics.setClip(x, y, width, height);
    }

    @Override
    public void setColor(String colorName) {
        Color color = switch (colorName) {
            case "red" -> Color.RED;
            case "green" -> Color.GREEN;
            case "blue" -> Color.BLUE;
            case "magenta" -> Color.MAGENTA;
            case "cyan" -> Color.CYAN;
            case "yellow" -> Color.YELLOW;
            case "lightgray" -> Color.LIGHT_GRAY;
            case "gray" -> Color.GRAY;
            case "darkgray" -> Color.DARK_GRAY;
            default -> Color.BLACK; // to czyni zbędnym "black" -> Color.BLACK;
        };
        graphics.setColor(color);
    }

    @Override
    public void setLineStyle(String lineStyle) {
        int strokeWidth = 1;
        Stroke stroke = switch (lineStyle) {
            case "dashed" ->
                    new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5, 2}, 0);
            case "dotted" ->
                    new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{1, 1}, 0);
            case "dashed-dotted" ->
                    new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5, 2, 1, 2}, 0);
            case "none" -> new BasicStroke(0);
            default -> new BasicStroke(strokeWidth); // zawiera case "solid" -> new BasicStroke(strokeWidth);
        };
        graphics.setStroke(stroke);
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        graphics.fillOval(x, y, width, height);
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        graphics.drawOval(x, y, width, height);
    }

    @Override
    public void drawPolyLine(int n, int[] x, int[] y) {
        Path2D path = new Path2D.Float();
        path.moveTo(x[0], y[0]);
        for (int i = 0; i < n; i++) {
            path.lineTo(x[i], y[i]);
        }
        final int strokeWidth = 2;
        // @todo: sprawdzić czy setStroke jest tu potrzebne
        graphics.setStroke(new BasicStroke(strokeWidth));
        graphics.draw(path);
    }

    @Override
    public int getWidth() {
        return jPanel.getWidth();
    }

    @Override
    public int getHeight() {
        return jPanel.getHeight();
    }

    private void adjustRenderingHints(Graphics2D graphics) {
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
    }


}
