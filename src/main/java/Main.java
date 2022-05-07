import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        //writeString("data.json", listToJson(parseCSV(columnMapping, "data.csv")));
        writeString("data2.json", listToJson(parseXML("data.xml")));
    }

    static List<Employee> parseCSV(String[] conlumnMapping, String fileName) {
        List<Employee> result = null;
        ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
        strategy.setType(Employee.class);
        strategy.setColumnMapping(conlumnMapping);
        try (FileReader reader = new FileReader(fileName)) {
            CsvToBeanBuilder<Employee> builder = new CsvToBeanBuilder<>(reader);
            builder.withMappingStrategy(strategy);
            result = builder.build().parse();
        } catch (IOException e) { e.printStackTrace(); }
        return result;
    }

    static List<Employee> parseXML(String fileName) {
        List<Employee> result = new ArrayList<>();
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fileName);
            NodeList employeers = doc.getDocumentElement().getChildNodes();
            for (int i = 0; i < employeers.getLength(); i++) {
                Node employeer = employeers.item(i);
                if (employeer.getNodeType() != Node.TEXT_NODE) {
                    Employee instance = new Employee();
                    NodeList list = employeer.getChildNodes();
                    for (int j = 0; j < list.getLength(); j++) {
                        Node node = list.item(j);
                        if (node.getNodeType() != Node.TEXT_NODE) {
                            Node element = node.getFirstChild();
                            String value = element.getNodeValue();
                            Field field = instance.getClass().getField(node.getNodeName());
                            if (field.getGenericType().getTypeName().equals("long")) {
                                field.setLong(instance, Long.parseLong(value));
                            } else if (field.getGenericType().getTypeName().equals("int")) {
                                field.setInt(instance, Integer.parseInt(value));
                            } else {
                                field.set(instance, value);
                            }
                        }
                    }
                    result.add(instance);
                }
            }
        } catch (SAXException | IOException | ParserConfigurationException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    static String listToJson(List<Employee> list) {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(list, new TypeToken<List<Employee>>(){}.getType());
    }

    static void writeString(String fileName, String fileContents) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(fileContents);
        } catch (IOException e) { e.printStackTrace(); }
    }
}
