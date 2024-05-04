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

public abstract class GraphicsAttributes {

    final static String[] namesFull = {"red", "green", "blue", "black", "magenta", "cyan", "yellow"};
    final static String[] namesShort = {"r", "g", "b", "k", "m", "c", "y"};

    private final String code;
    private final String lowercaseCode;
    private String colorBlindCode;

    private String colorName;
    private String lineStyle;
    private String markerStyle;

    protected GraphicsAttributes(String code) {
        this.code = code;
        lowercaseCode = code.toLowerCase();
        colorBlindCode = lowercaseCode;
        for (String name : namesFull) {
            colorBlindCode = colorBlindCode.replaceAll(name, "");
        }

        colorName = null;
        for (String name : namesFull) {
            if (lowercaseCode.contains(name)) {
                colorName = name;
                break;
            }
        }
        if (colorName == null) {
            for (int i = 0; i < namesShort.length; i++) {
                if (lowercaseCode.contains(namesShort[i])) {
                    colorName = namesFull[i];
                    break;
                }
            }
        }
        if (colorName == null) {
            colorName = "black";
        }

        lineStyle = "none";
        if (code.contains("--")) {
            lineStyle = "dashed";
        } else if (code.contains("-")) {
            lineStyle = "solid";
        } else if (code.contains(":-") && code.contains("-:")) {
            lineStyle = "dashed-dotted";
        } else if (code.contains(":")) {
            lineStyle = "dotted";
        }

        markerStyle = "none";
        if (code.contains(".")) {
            markerStyle = "dot";
        } else if (code.contains("o")) {
            markerStyle = "circle";
        } else if (code.contains("*")) {
            markerStyle = "star";
        } else if (code.contains("+")) {
            markerStyle = "plus";
        } else if (code.contains("x")) {
            markerStyle = "x";
        } else if (code.contains("s")) {
            markerStyle = "square";
        } else if (code.contains("d")) {
            markerStyle = "diamond";
        }
    }

    public void useAttributes() {
        useColor(colorName);
        useLineStyle(lineStyle);
        useMarkerStyle(markerStyle);
    }

    public void useColor() {
        useColor(colorName);
    }

    public void useLineStyle() {
        useLineStyle(lineStyle);
    }

    public void useMarkerStyle() {
        useMarkerStyle(markerStyle);
    }

    protected abstract void useColor(String colorName);

    protected abstract void useLineStyle(String lineStyle);

    protected abstract void useMarkerStyle(String markerStyle);

    public String getColorName() {
        return colorName;
    }

    public String getLineStyle() {
        return lineStyle;
    }

    public String getMarkerStyle() {
        return markerStyle;
    }
}