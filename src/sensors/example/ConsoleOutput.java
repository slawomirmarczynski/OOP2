package sensors.example;

import java.util.Map;

public class ConsoleOutput extends Component {
    public ConsoleOutput(Map<String, Object> parameters) {
        super((String) parameters.get("name"));
    }

    @Override
    public void update(Component source) {
        String name = source.getName();
        Object result = source.getValue();
        System.out.print("Dane z " + name + ":");
        if (result instanceof Double) {
            System.out.println(" " + result);
        }
        if (result instanceof Double[]) {
            Double[] array = (Double[]) result;
            for (Double element : array) {
                System.out.print(" " + element + ",");
            }
            System.out.println();
        }
    }
}
