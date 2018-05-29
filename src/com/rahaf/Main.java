package com.rahaf;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class RequestHandler implements Callable<Void>{
    private Socket socket;
    RequestHandler(Socket socket){
        this.socket = socket;
    }
    @Override
    public Void call() throws Exception {
        System.out.println("connect");//test
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintStream writer = new PrintStream(socket.getOutputStream());
        String line;
        while ( (line=reader.readLine()) != null){
            System.out.println("receive from client: " + line);

            JSONObject jsonObject = new JSONObject(line);

            String funName = jsonObject.getString("fun");
            System.out.println(funName);

            if(funName.equalsIgnoreCase("add")){
                System.out.println("fun name="+funName);
                double n1 = jsonObject.getDouble("n1");
                double n2 = jsonObject.getDouble("n2");

                String result = new Server().add(n1,n2) + "";

                JSONObject jsonObjectResult = new JSONObject();
                jsonObjectResult.put("status","success");
                jsonObjectResult.put("result",result);

                writer.println(jsonObjectResult.toString());
            }


        }

        socket.close();

        return null;
    }
}
public class Main {
   public static final int PORT = 3000;
    public static void main(String[] args) throws Exception{
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("server start");
        ExecutorService service = Executors.newFixedThreadPool(10);
        registerToBinder();

        while (true){
            Socket socket =  serverSocket.accept();
            service.submit(new RequestHandler(socket));
        }

    }

    private static void registerToBinder() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try (Socket socket = new Socket("localhost",4000)){

                    PrintStream printStream = new PrintStream(socket.getOutputStream());

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("op","register");
                    List<String> functions = Arrays.asList("add","mul");
                    jsonObject.put("functions",functions);
                    jsonObject.put("serverIp",InetAddress.getLocalHost().getHostAddress());
                    jsonObject.put("serverPort",PORT);

                    printStream.println(jsonObject.toString());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, 1000, 10000);
    }
}
