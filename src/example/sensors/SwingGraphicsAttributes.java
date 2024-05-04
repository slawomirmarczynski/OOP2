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

import java.awt.*;

public class SwingGraphicsAttributes extends GraphicsAttributes {

    private final Graphics2D graphics;

    public SwingGraphicsAttributes(Graphics2D graphics, String code) {
        super(code);
        this.graphics = graphics;
    }

    @Override
    protected void useColor(String colorName) {
        Color color = switch (getColorName()) {
            case "red" -> Color.RED;
            case "green" -> Color.GREEN;
            case "blue" -> Color.BLUE;
            case "magenta" -> Color.MAGENTA;
            case "cyan" -> Color.CYAN;
            case "yellow" -> Color.YELLOW;
//            case "black" -> Color.BLACK;
            default -> Color.BLACK;
        };
        graphics.setColor(color);
    }

    @Override
    protected void useLineStyle(String lineStyle) {
        int strokeWidth = 2;
        Stroke stroke = switch (lineStyle) {
//            case "solid" -> new BasicStroke(strokeWidth);
            case "dashed" -> new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{10, 10}, 0);
            case "dotted" -> new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2, 10}, 0);
            case "dashed-dotted" -> new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{10, 10, 2, 10}, 0);
            case "none" -> new BasicStroke(0);
            default -> new BasicStroke(strokeWidth);
        };
        graphics.setStroke(stroke);
    }

    @Override
    protected void useMarkerStyle(String markerStyle) {
        //@todo: ???
    }
}
