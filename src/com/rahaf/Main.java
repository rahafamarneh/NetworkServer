package com.rahaf;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
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
        System.out.println("connect");
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintStream writer = new PrintStream(socket.getOutputStream());
        String line;
        while ( (line=reader.readLine()) != null && !line.equals("stop")){
            System.out.println("receive from client " + line);

            if(line.startsWith("fact")){
                System.out.println("fact");
                int number = Integer.parseInt(line.split(" ")[1]);
                writer.println("fact of " + number);
            }

//            else if(line.equals("add")){
//                double n1 = Integer.parseInt(reader.readLine());
//                double n2 = Integer.parseInt(reader.readLine());
//                writer.println("add of "+n1 + " " + n2);
//            }

            writer.flush();


        }

        return null;
    }
}
public class Main {
    static ServerSocket serverSocket;
    public static void main(String[] args) throws Exception{
	    serverSocket = new ServerSocket(3000);
        System.out.println("start");
        ExecutorService service = Executors.newFixedThreadPool(10);
        while (true){
            Socket socket =  serverSocket.accept();
            service.submit(new RequestHandler(socket));
        }

    }
}
