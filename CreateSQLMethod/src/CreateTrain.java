import java.io.*;
import java.util.*;

public class CreateTrain {

    public static void main(String[] args) {

        String line;
        Set<String> trainNames = new HashSet<>();
        StringBuilder sb = new StringBuilder();


        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("InsertTrain.sql"))) {
            bufferedReader.readLine();

            while ((line = bufferedReader.readLine()) != null) {

                String train = line.split(",")[0];
                if (trainNames.contains((train)))
                    continue;
                trainNames.add(train);

                sb.append(line + "\n");

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("InsertAllTrain.sql"))) {
            bufferedReader.readLine();

            while ((line = bufferedReader.readLine()) != null) {

                String train = line.split(",")[0];
                if (trainNames.contains((train)))
                    continue;
                trainNames.add(train);

                sb.append(line + "\n");

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("train.sql"))) {

            bufferedWriter.write(sb.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}