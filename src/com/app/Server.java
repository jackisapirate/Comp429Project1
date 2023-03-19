package com.app;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kunlong Wang
 * @create 2023-03-02 1:44 PM
 */
public class Server implements Runnable {
    private String ip;
    private int port;
    private Map<Integer, Integer> contacts = new HashMap<>();
    private ArrayList<String[]> list;
    private boolean flag = true;


    public Server(int port, ArrayList<String[]> list) {
        this.port = port;
        this.list = list;
        try {
            this.ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void startServer() {
        try {
            System.out.println("My Server Start and My Ip: " + InetAddress.getLocalHost().getHostAddress() + ", Port: " + port);
            ServerSocket serverSocket = new ServerSocket(port);
            while (this.flag) {
                Socket socket = serverSocket.accept();
                new ServerThread(socket, this).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        startServer();
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public Map<Integer, Integer> getContacts() {
        return contacts;
    }

    public ArrayList<String[]> getList() {
        return list;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setContacts(Map<Integer, Integer> contacts) {
        this.contacts = contacts;
    }

    public void setList(ArrayList<String[]> list) {
        this.list = list;
    }

    public boolean getFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
