import java.io.*;
import java.util.*;

public class CreateDock {
    public static void main(String[] args) {

        String line;

        Set<String> stationNames = new HashSet<>();
        Set<String> trainNames = new HashSet<>();
        Map<String,String> station = new HashMap<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("station.txt"))) {

            while ((line = bufferedReader.readLine()) != null) {
                String [] st = line.split(",");
                if (!stationNames.contains(st[0])) {
                    station.put(st[1],st[0]);
                    stationNames.add(st[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("dock.csv"))) {

            bufferedReader.readLine();

            while ((line = bufferedReader.readLine()) != null) {

                double [] seat = new double[3];
                String[] dock = line.split(",");
                String name = "";

                if (trainNames.contains((dock[0] + dock[1])))
                    continue;
                trainNames.add(dock[0] + dock[1]);

                if (dock[0].contains("/")){
                    name = dock[0].split("/")[1];
                    dock[0] = dock[0].split("/")[0];
                }

                dock[2] = station.get(dock[2]);

                if (dock[3].equals(" º∑¢’æ"))
                    dock[3] = "start";
                if (dock[4].equals("÷’µ„’æ"))
                    dock[4] = "end";

                switch (getType(dock[0])){
                    case "0":
                    case "K":
                    case "T":
                    case "Y":
                    case "Z":{
                        String [] second = dock[7].split("/");

                        seat[1] = Double.parseDouble(second[1]);

                        second = dock[8].split("/");
                        seat[0] = Double.parseDouble(second[0]);
                        seat[2] = Double.parseDouble(second[1]);
                        seat[0] = (seat[0] + seat[2]) / 2;

                        if (dock[9].equals("-"))
                            seat[2] = 0;
                        else seat[2] = Double.parseDouble(dock[9]);
                        break;
                    }
                    case "C":
                    case "G": {
                        String [] second = dock[9].split("/");

                        seat[0] = 0;
                        seat[1] = Double.parseDouble(second[1]);
                        seat[2] = Double.parseDouble(second[0]);
//
                        break;
                    }
                    case "D":{
                        String [] second = dock[7].split("/");
                        seat[1] = Double.parseDouble(second[1]);

                        second = dock[8].split("/");
                        seat[0] = Double.parseDouble(second[0]);
                        seat[2] = Double.parseDouble(second[1]);
                        seat[0] = (seat[0] + seat[2]) / 2;

                        second = dock[9].split("/");
                        seat[2] = Double.parseDouble(second[0]);
                        if (seat[1] == 0)
                            seat[1] = Double.parseDouble(second[1]);

                        break;
                    }
                }

                String newRow = "INSERT INTO Dock VALUES('" + dock[0] + "','" + dock[1] + "','" + dock[2] + "','"
                        + dock[3] + "','" + dock[4] + "'," +
                        seat[0] + "," + seat[1] + "," + seat[2] + ");";
                sb.append(newRow + "\n");

                if (!name.equals("")){
                    newRow = "INSERT INTO Dock VALUES('" + name + "','" + dock[1] + "','" + dock[2] + "','"
                            + dock[3] + "','" + dock[4] + "'," +
                            seat[0] + "," + seat[1] + "," + seat[2] + ");";
                    sb2.append(newRow + "\n");
                }

            }

            sb.append(sb2.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("InsertDock.sql"))) {

            bufferedWriter.write(sb.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static String getType(String train){
        if (Character.isDigit(train.charAt(0)))
            return "0";
        return train.substring(0,1);
    }
}
