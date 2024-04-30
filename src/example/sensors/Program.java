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
import java.util.List;

/**
 * Klasa Program jest główną klasą programu. Jest ona Runnable, bo aby wyrwać
 * się z konieczności pisania kolejnych metod statycznych, będziemy tworzyć jej
 * instancję i wywoływać metodę run().
 */
public class Program implements Runnable {

    private List<Device> devices;
    private List<Receiver> receivers;
    private List<Route> routes;

    /**
     * Metoda main() tworzy instancję programu i wywołuje metodę run() tej
     * instancji. W ten sposób unikamy sytuacji, w której w statycznej metodzie
     * main() wywołujemy statyczne metody klasy, w których możemy wywoływać
     * tylko statyczne metody itd.
     *
     * @param args argumenty wywołania programu, nie są istotne dla programu.
     */
    public static void main(String[] args) {
        Program program = new Program();
        program.run();
    }

    /**
     * Pomocnicza metoda generująca opóźnienie bez ryzyka wygenerowania wyjątku.
     *
     * @param millis wartość opóźnienia w milisekundach.
     */
    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }

    /**
     * Tworzenie obiektów, takich jak urządzenia i odbiorniki oraz łączniki,
     * koniecznych do działania programu. Tworzenie urządzeń tworzy także,
     * automatycznie, sensory będące składnikami urządzeń.
     */
    private void createObjects() {
        Configuration configuration = new Configuration();
        Factory factory = new Factory(configuration);
        devices = factory.createDevices();
        receivers = factory.createReceivers();
        routes = factory.createRoutes();
    }

    /**
     * Uruchamianie wszystkich urządzeń, tak aby zbierały i wysyłały dane.
     */
    private void runDevices() {
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

    /**
     * Trasowanie dróg komunikacji pomiędzy sensorami i odbiornikami.
     *
     */
    private void establishRoutes() {
        //
        // Algorytm jest prosty... ale niezbyt efektywny, można to zrobić
        // znacznie lepiej (szybciej będzie działać), jednak będzie to wtedy
        // nieco mniej czytelne. Na razie efektywność nie jest problemem.
        //
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
        try {
            createGui();
            createObjects();
            establishRoutes();
            runDevices();
        } catch (Exception exception) {
            System.err.println("coś poszło nie tak"); //@todo: lepsza obsługa
        }
        sleep(10_000);
        closeDevices();
        closeReceivers();
    }

    private void closeDevices() {
        for (Device device : devices) {
            device.close();
        }
    }

    private void closeReceivers() {
        for (Receiver receiver : receivers) {
            receiver.close();
        }
    }

    private void createGui() throws InterruptedException, InvocationTargetException {
        DrawingToolsFactory guiFactory;

        //@todo: wybieranie jakie narzędzia do rysowania chcemy mieć,
        //       na razie jest tylko Swing, ale chcemy mieć wszystko co możliwe.

        guiFactory = SwingDrawingToolsFactory.getInstanceDrawingToolsFactory();
    }
}
