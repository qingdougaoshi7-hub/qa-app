package service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class DiscordService {

 public static void send(String msg){

  try{

   URL url=URI.create("WEBHOOK_URL").toURL();

   HttpURLConnection con=(HttpURLConnection)url.openConnection();

   con.setRequestMethod("POST");

   con.setDoOutput(true);

   String json="{\"content\":\""+msg+"\"}";

   OutputStream os=con.getOutputStream();

   os.write(json.getBytes());

   os.close();

  }catch(Exception e){

   e.printStackTrace();

  }

 }

}