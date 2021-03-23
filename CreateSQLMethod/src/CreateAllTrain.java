import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

public class CreateAllTrain {
    public static void main(String[] args) throws ClientProtocolException, IOException {

        ArrayList<String> trainList = new ArrayList<>();

        String line;
        Set<String> stationNames = new HashSet<>();
        Set<String> trainNames = new HashSet<>();

        Map<String,String> station = new HashMap<>();

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

        HttpclientUtil httpclient = new HttpclientUtil();
        String url = "https://kyfw.12306.cn/otn/resources/js/query/train_list.js?scriptVersion=1.0";

        HttpGet httpget = new HttpGet(url);
        CloseableHttpClient httPclient = httpclient.createSSLClientDefault();

        CloseableHttpResponse Response = httPclient.execute(httpget);
        HttpEntity entity = Response.getEntity();
        String result = EntityUtils.toString(entity, "utf-8");
        String[] ss = result.split("station_train_code");

        int count = 0;

        for (String s : ss) {
            String[] str = s.split("\"");
            if (str[1].equals(":")) {
                String[] train = str[2].split("\\(");
                String from = train[1].split("-")[0];
                String to = train[1].split("-")[1];
                to = to.substring(0, to.length() - 1);
                trainList.add(train[0] + "," + from + "," + to);
            }
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter
                (new FileWriter("InsertAllTrain.sql"))) {
//                (new FileWriter("table.csv"))) {

            for (int i = 0; i < trainList.size(); i++) {

                int [] seat = new int[3];
                String[] train = trainList.get(i).split(",");

                if (station.get(train[2]) == null || station.get(train[1]) == null)
                    continue;

                if (trainNames.contains(train[0]))
                    continue;

                Random ran = new Random();
                switch (getType(train[0])){
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

                trainNames.add(train[0]);

                String newRow = "INSERT INTO Train VALUES('" + train[0] + "','"
                        + station.get(train[1]) + "','" + station.get(train[2]) + "',"
                        + seat[0] + "," + seat[1] + "," + seat[2] + ");";
                bufferedWriter.write(newRow + "\n");

//                String newRow = train[0] + "," + train[1] + "-" + train[2];
//                bufferedWriter.write(newRow + "\n");

                count++;
            }

            System.out.println("count ==>" + count);

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
