package com.rahaf;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
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

    public static void main(String[] args) throws Exception{
        ServerSocket serverSocket = new ServerSocket(3000);
        System.out.println("server start");
        ExecutorService service = Executors.newFixedThreadPool(10);
        while (true){
            Socket socket =  serverSocket.accept();
            service.submit(new RequestHandler(socket));
        }

    }
}
