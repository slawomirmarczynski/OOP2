package sensors.example;

import java.util.Map;

public class BMP180Sensor extends Component {
    public BMP180Sensor(Map<String, Object> objectConfiguration) {
        super((String) objectConfiguration.get("name"));
        // ... tu inne parametry jeszcze ustawić ...
    }

    public Double[] getValue() {
        return new Double[] { 293.0, 1025.1};
    }
}
