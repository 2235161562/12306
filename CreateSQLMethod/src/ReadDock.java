import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.*;

public class ReadDock {
    public static void main(String[] args) {

        String line;
        Set<String> stationNames = new HashSet<>();
        Set<String> trainNames = new HashSet<>();
        Map<String,String> station = new HashMap<>();
        StringBuilder sb = new StringBuilder();

        try (BufferedReader bufferedReader = new BufferedReader
                (new FileReader("station.txt"))) {
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

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("dock.csv"))) {
            bufferedReader.readLine();

            String start = "";

            while ((line = bufferedReader.readLine()) != null) {
                String[] dock = line.split(",");
                String name = "";

                if (trainNames.contains((dock[0] + dock[1])))
                    continue;
                trainNames.add(dock[0] + dock[1]);

                if (dock[3].equals(" º∑¢’æ")){
                    start = station.get(dock[2]);
                }
                else if (dock[4].equals("÷’µ„’æ")){
                    String end = station.get(dock[2]);

                    if (dock[0].contains("/")){
                        name = dock[0].split("/")[1];
                        dock[0] = dock[0].split("/")[0];
                    }

                    int [] seat = new int[3];

                    Random ran = new Random();
                    switch (getType(dock[0])){
                        case "0":
                        case "K":
                        case "T":
                        case "Y":
                        case "Z":{
                            seat[0] = 24+ran.nextInt(1)*8;
                            seat[1] = 5*(66+ran.nextInt(1)*6);
                            seat[2] = 8*(108+ran.nextInt(1)*10);
                            break;
                        }
                        case "C":
                        case "G": {
                            seat[0] = ran.nextInt(1)*24;
                            seat[1] = 2*(52+ran.nextInt(3)*6);
                            seat[2] = (21+ran.nextInt(4))*5*12;
//
                            break;
                        }
                        case "D":{
                            seat[0] = 18+ran.nextInt(2)*3;
                            seat[1] = 2*(52+ran.nextInt(3)*6);
                            seat[2] = (20+ran.nextInt(5))*5*12;
                            break;
                        }
                    }

                    String newRow = "INSERT INTO Train VALUES('" + dock[0] + "','" + start + "','" + end + "',"
                            + seat[0] + "," + seat[1] + "," + seat[2] + ");";
                    sb.append(newRow + "\n");

                    if (!name.equals("")){
                        newRow = "INSERT INTO Train VALUES('" + name + "','" + start + "','" + end + "',"
                                + seat[0] + "," + seat[1] + "," + seat[2] + ");";
                        sb.append(newRow + "\n");
                    }

                }
                else continue;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("InsertTrain.sql"))) {

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
