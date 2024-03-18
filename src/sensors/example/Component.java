package sensors.example;

import java.util.HashSet;
import java.util.Set;

public abstract class Component {

    private final String name;
    Set<Component> observers = new HashSet<>();

    public Component(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    void addObserver(Component component) {
        observers.add(component);
    }

//    void removeObserver(Component component) {
//        observers.remove(component);
//    }

    void removeAllObservers() {
        observers.clear();
    }

    public void notifyAllObservers() {
        for (Component listener : observers) {
            listener.update(this);
        }
    }

    public void run() {
        // Dlaczego prawie pusta, a nie abstrakcyjna?
        // Aby oszczędzić konieczności definiowania dla odbiorców (listeners).
        notifyAllObservers();
    }

    Object getValue() {
        // Dlaczego pusta, a nie abstrakcyjna?
        // Aby oszczędzić konieczności definiowania dla odbiorców (listeners).
        return null;
    }

    public void update(Component source) {
        // Dlaczego pusta, a nie abstrakcyjna?
        // Aby oszczędzić konieczności definiowania dla sensorów.
    }
}
