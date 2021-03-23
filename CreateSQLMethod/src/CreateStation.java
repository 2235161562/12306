import java.io.*;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

public class CreateStation {
	public static void main(String[] args) throws ClientProtocolException, IOException {
		HttpclientUtil httpclient=new HttpclientUtil();
		String url="https://kyfw.12306.cn/otn/resources/js/framework/station_name.js?station_version=1.9142";

		HttpGet httpget=new HttpGet(url);
		CloseableHttpClient httPclient=httpclient.createSSLClientDefault();
		
		CloseableHttpResponse Response=httPclient.execute(httpget);
		HttpEntity entity=Response.getEntity();
		String result=EntityUtils.toString(entity,"utf-8");
		String[] ss=result.split("@");



//		try (FileWriter writer = new FileWriter("station.txt", true)) {
//			for (int i = 1; i < ss.length; i++) {
//				String[] str = ss[i].split("\\|");
//				writer.write(str[2] + "," + str[1] + "\n");
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		try (BufferedWriter bufferedWriter = new BufferedWriter
				(new FileWriter("InsertStation.sql"))) {
			for (int i = 1; i < ss.length; i++) {
				String[] str = ss[i].split("\\|");

				System.out.println(get_city(str[1]));                 //bug
				
//				writer.write("INSERT INTO Station VALUES('" + str[2] + "','"
//						+ str[1] + "','" + get_city(str[1]) + ");" + "\n");

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	static String get_city(String station){
		String tem = station.substring(station.length()-1);
		if (tem.equals("东") || tem.equals("南") || tem.equals("西") || tem.equals("北"))
			return station.substring(0, station.length()-1);
		return station;
	}
}
