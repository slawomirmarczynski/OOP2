package sensors.example;

import java.util.List;
import java.util.Map;


public class Program {

    /**
     * Klasa main służy do testowania koncepcji programowania obiektowego
     * do obsługi sensorów.
     *
     * @param args argumenty wywołania programu, nie są istotne dla programu.
     */
    public static void main(String[] args) {

        // Czymkolwiek jest konfiguracja, to jest to coś dość potężnego,
        // bo określa strukturę, w jaką połączą się obiekty programu
        // i jak będą one współpracować.
        //
        Configuration configuration = new Configuration();
        List<?> sourcesConfigurations = configuration.getSources();
        List<?> listenersConfigurations = configuration.getListeners();
        List<List<String>> routesConfiguration = configuration.getRoutes();

        Factory factory = new Factory();

        Map<String, Component> sources = factory.createComponentsMap(sourcesConfigurations);
        Map<String, Component> listeners = factory.createComponentsMap(listenersConfigurations);

        for (List<String> route : routesConfiguration) {
            String sourceName = route.get(0); // from
            String listenerName = route.get(1); // to
            Component source = sources.get(sourceName);
            Component listener = listeners.get(listenerName);
            if (source == null || listener == null) {
                throw new RuntimeException("błąd konfiguracji routingu");
            } else {
                source.addObserver(listener);
            }
        }

        // while(true) ??? {
        for (int i = 0; i < 5; i++)
            for (Component source : sources.values()) {
                source.run();
            }

        for (Component source : sources.values()) {
            source.removeAllObservers();
        }
    }

}
