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
