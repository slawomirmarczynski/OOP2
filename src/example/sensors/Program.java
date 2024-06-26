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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.*;


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
        //@todo: wybieranie jakie narzędzia do rysowania chcemy mieć,
        //       na razie jest tylko Swing, ale chcemy mieć wszystko co możliwe.
        Program program = new Program();
        program.run();
    }

    @Override
    public void run() {

        setupLookAndFeel();

        try {
            createObjects();
            establishRoutes();
            runDevices();
        } catch (Exception exception) {
            System.err.println("tym razem coś poszło nie tak"); //@todo: lepsza obsługa
        }
        sleep(10_000);
        closeDevices();
        //@todo: W tym miejscu jest problem, bo wywołanie closeDevices() tylko
        //       teoretycznie coś robi, nie ma pewności czy po wywołaniu tej
        //       metody wszystkie urządzenia są zatrzymane. A skoro tak, to
        //       możliwe jest że będą wywoływały odbiorców (receivers), gdy ci
        //       odbiorcy już przestaną działać/istnieć. Panaceum na to może być
        //       odłączanie odbiorców w close() danego urządzenia (Device).
        closeReceivers();
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
        ComponentFactory factory = new ComponentFactory(configuration);
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

    /**
     * Ustalanie look-and-feel (tzw. laf), czyli jaki mają wygladać kontrolki -
     * czy mają przypominać te znane z MS Windows, czy raczej takie jakie są na
     * komputerach Apple, czy może jeszcze inne?!
     */
    private void setupLookAndFeel() {

        if (System.getProperty("sun.java2d.uiScale", null) == null) {
            System.setProperty("sun.java2d.uiScale", "100%");
        }
        if (System.getProperty("flatlaf.uiScale", null) == null) {
            System.setProperty("flatlaf.uiScale", "110%");
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            FlatArcOrangeIJTheme.setup();
        } catch (Exception ignored) {
        }
        return;

        //@todo: Problem black-hole-decoration-resize jest niemal rozwiązany
        //       przez FlatLaF.
//        FlatLightLaf.setup();
//        if (0 < 1)
//            return;

        // Tablica zawierająca preferowane i tablica zawierające dostępne LaF.
        // Są one final, bo nie będą modyfikowane po utworzeniu.
        //
//        final String[] preferredNames = {"Windows", "Nimbus"}; // @todo: dopisać więcej
//        final UIManager.LookAndFeelInfo[] installed = UIManager.getInstalledLookAndFeels();

        // Negocjowanie jaki LaF ma być użyty - ponieważ dostępnych LaF jest
        // niewiele (kilka, może kilkanaście) i niewiele jest preferowanych LaF
        // - to użycie dwóch pętli for (jak poniżej) nie jest aż tak złe... jak
        // mogłoby się to wydawać. Przechwytywanie wyjątków (try-catch) jest
        // konieczne, ale - poza odnotowaniem że coś się dzieje - nie wymaga
        // szczególnych kroków - w najgorszym razie nic się nie uda i pozostanie
        // standardowy wygląd kontrolek - co jest dobrym rozwiązaniem.
        //
//        try {
//            for (String bestName : preferredNames) {
//                for (UIManager.LookAndFeelInfo available : installed) {
//                    if (available.getName().equals(bestName)) {
//                        UIManager.setLookAndFeel(available.getClassName());
//                        return;
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            Logger.getLogger(Program.class.getName()).log(Level.SEVERE, null, ex);
//        }

        /*
        //
        // Przygotowanie skalowania FlatLaf, które efektywnie mnoży się przez
        // skalowanie sun.java2d.uiScale. Nawet jeżeli FlatLaf nie będzie użyty,
        // to przygotowanie skalowania FlatLaf nie zaszkodzi.
        //
        if (System.getProperty("sun.java2d.uiScale", null) == null) {
            System.setProperty("sun.java2d.uiScale", "100%");
        }
        if (System.getProperty("flatlaf.uiScale", null) == null) {
            System.setProperty("flatlaf.uiScale", "110%");
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            FlatCyanLightIJTheme.setup();
        } catch (Exception ignored) {
        }
        */
    }
}
