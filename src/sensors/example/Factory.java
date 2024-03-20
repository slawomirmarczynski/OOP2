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

package sensors.example;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Factory {

    public Map<String, Component> createComponentsMap(List<?> objectsConfiguration) {
        Map<String, Component> map = new ConcurrentHashMap<>(); // bezpieczniej
        for (Object objectConfiguration : objectsConfiguration) {
            try {
                Component Component = createObject(objectConfiguration);
                String name = Component.getName();
                if (map.containsKey(name)) {
                    // Mamy problem, coś o takiej nazwie już mamy.
                    System.out.println("Nie można utworzyć " + name);
                } else {
                    map.put(name, Component);
                }
            } catch (RuntimeException exception) {
                // logika odpowiedzialna za informowanie o niepowodzeniach
            }
        }
        return map;
    }

    public Component createObject(Object objectConfiguration) {
        Map<String, Object> parameters = (Map<String, Object>) objectConfiguration;
        String type = (String) parameters.get("type");
        if (type.equals("bmp180") || type.equals("dht22"))
            return new BMP180Sensor(parameters);
        else if (type.equals("console") || type.equals("log")) {
            return new ConsoleOutput(parameters);
        }
        return null;
    }
}
