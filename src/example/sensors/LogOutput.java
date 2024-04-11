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

import java.util.Arrays;

public class LogOutput extends Receiver {
    public LogOutput(String name, Object ignoredOptions) {
        super(name);
    }

    @Override
    public void update(Sensor source) {
        System.out.print("Do PLIKU LOG !!! --> ");

        // Pobieranie nazwy sensora
        String sensorName = source.getName();
        // Pobieranie wartości z sensora
        Object value = source.getValue();
        // Pobieranie nazwy parametru fizycznego
        String physicalParameterName = source.getPhysicalParameterName();
        // Pobieranie jednostki parametru fizycznego
        String physicalParameterUnit = source.getPhysicalUnit();

        // Wydrukowanie informacji o sensorze
        System.out.printf("Sensor %s, %s [%s]: ",
                sensorName, physicalParameterName, physicalParameterUnit);

        // Jeżeli wartość jest typu Double, to wypisujemy ją. Jeżeli natomiast
        // wartość jest tablicą typu Double, czyli wektorem, to wypisujemy jej
        // elementy rozdzielone przecinkami.
        //
        if (value instanceof Double) {
            System.out.println(value);
        } else if (value instanceof Double[]) {
            System.out.println(Arrays.toString((Double[]) value));
        }
    }
}
