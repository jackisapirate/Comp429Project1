package com.app;

import java.io.*;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kunlong Wang
 * @create 2023-03-02 1:44 PM
 */
public class ServerThread extends Thread {
    private Socket socket;
    private PrintWriter out;
    private List<String[]> list;
    private Server server;

    private int port = 0;

    public ServerThread(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        this.list = server.getList();
    }

    @Override
    public void run() {
        try {
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String msg;
            String remoteMsgArray[] = socket.getRemoteSocketAddress().toString().split(":");
            String remoteIp = remoteMsgArray[0].substring(1, remoteMsgArray[0].length());

            while ((msg = br.readLine()) != null) {
                System.out.println("---------NEW-ACTION-COMING----------------------------------");
                if (msg.startsWith("send")) {
                    int index = 0;
                    int i = 4;
                    while(--i>=0){
                        index = msg.indexOf(" ", index + 1);
                    }
                    String[] information = msg.substring(0, index).split(" ");
                    String message = msg.substring(index + 1, msg.length());

//                    Message received from 192.168.21.20
//                    Sender’s Port: <The port no. of the sender>
//                    Message: “<received message>”
                    System.out.println("----------------------NEW-MESSAGE----------------------");
                    System.out.println("Message received from [" + remoteIp + "]");
                    System.out.println("Sender’s Port: [" + information[3] + "]");
                    System.out.println("Message: [" + message + "]");
                } else if (msg.startsWith("connect")) {
                    String userId;
                    if (list.isEmpty()) {
                        userId = "1";
                    } else {
                        userId = Integer.parseInt(list.get(list.size() - 1)[0]) + 1 + "";
                    }
                    String[] msgArray = msg.split(" ");
                    String port = msgArray[3];
                    String[] strs = {userId, remoteIp, port};

                    if("sender".equals(msgArray[4])){
                        String message = "connect " + remoteIp + " " + port + " " + this.server.getPort() + " receiver";
                        Tool.launch(remoteIp, Integer.parseInt(port), message);
                    }
                    list.add(strs);
                    System.out.println("Your connection with a friend  [" + remoteIp + ":" + port + "] has been established!");
                    for(String str[]:list){
                        System.out.println(Arrays.toString(str));
                    }
                } else if (msg.startsWith("exit")) {
                    // exit 127.0.0.1 8081 8080 sender
                    String[] msgArray = msg.split(" ");
                    String port = msgArray[3];

                    if("sender".equals(msgArray[4])){
                        for(int i=0; i<list.size(); i++){
                            String[] information = list.get(i);
                            if(information[1].equals(remoteIp) && information[2].equals(port)){
                                list.remove(i);
                                break;
                            }
                        }
                        System.out.println("Your connection with a friend  [" + remoteIp + ":" + port + "] has been disconnected!");
                    }

                    for(String str[]:list){
                        System.out.println(Arrays.toString(str));
                    }
                } else if (msg.startsWith("list")) {
                    msg.replaceFirst("list", "online list: \n");
                    for(String str[]:list){
                        msg+= "\t" + Arrays.toString(str) + "\n";
                    }
                    out = new PrintWriter(socket.getOutputStream());
                    out.println(msg);
                    out.flush();
                } else if (msg.startsWith("terminate")) {
                    // terminate 127.0.0.1 8081 8080 sender/receiver
                    String[] msgArray = msg.split(" ");
                    String port = msgArray[3];

                    if("sender".equals(msgArray[4])){
                        String message = "terminate " + remoteIp + " " + port + " " + this.server.getPort() + " receiver";
                        Tool.launch(remoteIp, Integer.parseInt(port), message);
                    }
                    for(int i=0; i<list.size(); i++){
                        String[] information = list.get(i);
                        if(information[1].equals(remoteIp) && information[2].equals(port)){
                            list.remove(i);
                            break;
                        }
                    }
                    System.out.println("Your connection with a friend  [" + remoteIp + ":" + port + "] has been disconnected!");
                    for(String str[]:list){
                        System.out.println(Arrays.toString(str));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(socket.getRemoteSocketAddress() + " exit");
        }
    }

    public static String cutString(String str, String start, String end) {
        if ("".equals(str)) {
            return str;
        }
        int strStartIndex = str.indexOf(start);
        int strEndIndex = str.indexOf(end);
        String s = str.substring(strStartIndex, strEndIndex).substring(start.length());
        return s;
    }
}
