import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        List<Employee> list = parseCSV(columnMapping, "data.csv");
        writeString("data.json", listToJson(parseCSV(columnMapping, "data.csv")));
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
