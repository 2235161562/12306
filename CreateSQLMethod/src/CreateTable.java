import java.io.*;
import java.util.*;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

public class CreateTable {
    public static void main(String[] args) throws ClientProtocolException, IOException {

        ArrayList<String> trainList = new ArrayList<>();

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
                String fromTo = train[1].substring(0, train[1].length() - 1);

                trainList.add(train[0] + "," + fromTo);


            }
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter
                (new FileWriter("table.csv"))) {

            for (int i = 0; i < trainList.size(); i++) {

                count++;

//                System.out.println(trainList.get(i));
                bufferedWriter.write(trainList.get(i) + "\n");
            }

            System.out.println("count ==>" + count);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static int[] getSeat(String trainName) {
        Random ran = new Random();
        String type = trainName.substring(0, 1);
        if (type.equals("G") || type.equals("D")) {
            ;
            return new int[]{24, 2 * 52, (20 + ran.nextInt(5)) * 5 * 12};
        } else if (type.equals("C")) {
            ;
            return new int[]{0, 2 * 52, (21 + ran.nextInt(4)) * 5 * 12};
        } else {
            return new int[]{36, 5 * 66, 6 * 118};
        }
    }

}
