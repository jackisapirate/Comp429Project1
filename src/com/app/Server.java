package com.app;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
/**
 * @author Kunlong Wang
 * @create 2023-03-02 1:44 PM
 */
public class Server implements Runnable {
    ArrayList<String[]> list = new ArrayList<>();
    private String ip;
    private int port;


    public Server(int port) {
        this.port = port;
    }

    public void startServer() {
        try {
            System.out.println("My Server Start and My Ip: " + InetAddress.getLocalHost().getHostAddress() + ", Port: " + port);
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.print("ServerSocket.accept()ing");
                new ServerThread(socket, list).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        startServer();
    }
}
