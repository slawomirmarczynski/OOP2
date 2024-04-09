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

import java.util.List;


public class Program implements Runnable {

    List<Device> devices;
    List<Receiver> receivers;
    List<Route> routes;

    /**
     * Klasa main służy do testowania koncepcji programowania obiektowego
     * do obsługi sensorów.
     *
     * @param args argumenty wywołania programu, nie są istotne dla programu.
     */
    public static void main(String[] args) throws InterruptedException {
        Program program = new Program();
        program.run();
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException exception) {
        }
    }

    private void createObjects() {
        Configuration configuration = new Configuration();
        Factory factory = new Factory(configuration);
        devices = factory.createDevices();
        receivers = factory.createReceivers();
        routes = factory.createRoutes();
    }

    private void runDevices() {
        // Run all devices
        for (Device device : devices) {
            boolean isInitialized = device.initialize();
            if (isInitialized) {
                Thread thread = new Thread(device);
                thread.start();
            } else {
                throw new RuntimeException("błąd inicjalizacji " + device.getName());
            }
        }
    }

    private void establishRoutes() {
        // Establish routes from sensors to receivers
        // Algorytm jest prosty... ale niezbyt efektywny, można lepiej przez map
        for (Route route : routes) {
            String deviceName = route.deviceName();
            String sensorName = route.sensorName();
            String receiverName = route.receiverName();
            for (Device device : devices) {
                for (Sensor sensor : device.getSensors()) {
                    for (Receiver receiver : receivers) {
                        boolean d = device.getName().equals(deviceName);
                        boolean s = sensor.getName().equals(sensorName);
                        boolean r = receiver.getName().equals(receiverName);
                        if (d && s && r) {
                            sensor.addObserver(receiver);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        createObjects();
        establishRoutes();
        runDevices();
        sleep(10_000);
    }
}
