package com.app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * @author Kunlong Wang
 * @create 2023-03-02 1:44 PM
 */
public class Client implements Runnable {

    // my client ip (client ip = server ip) and my port of listening server
    private String clientIp;
    private int serverPort;
    // receiver ip and listening port of receiver server
    private String ip;
    private int port;
    private ArrayList<String[]> list;
    private boolean trigger = true;

    public Client(int serverPort, ArrayList<String[]> list) {

        this.serverPort = serverPort;
        this.list = list;
        try {
            this.clientIp = InetAddress.getLocalHost().getHostAddress();
            System.out.println(this.clientIp);
            System.out.println(this.serverPort);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg() {
        System.out.println("---------------Client Start---------------------");
        try {
            Scanner scanner = new Scanner(System.in);

            while (trigger) {
                String msg = scanner.nextLine();
                if (!msg.isEmpty()) {
                    if (msg.startsWith("help")) {
                        System.out.println("1. help Display information about the available user interface options or command manual.");
                        System.out.println("2. myip Display the IP address of this process.");
                        System.out.println("3. myport Display the port on which this process is listening for incoming connections.");
                        System.out.println("4. connect  <destination>  <port  no>  :  This  command establishes  a  new TCP  connection to  the  specified \n" +
                                "<destination> at the specified < port no>. The <destination> is the IP address of the computer. Any attempt \n" +
                                "to  connect  to  an  invalid  IP  should  be  rejected and  suitable error message  should  be  displayed.  Success  or \n" +
                                "failure  in  connections  between  two  peers  should  be  indicated  by  both  the  peers  using  suitable  messages. \n" +
                                "Self-connections and duplicate connections should be flagged with suitable error messages.");
                        System.out.println("5. list Display a numbered list of all the connections this process is part of. This numbered list will include \n" +
                                "connections  initiated  by  this  process  and  connections  initiated  by  other  processes.  The  output  should \n" +
                                "display the IP address and the listening port of all the peers the process is connected to. ");
                        System.out.println("6.  terminate  <connection  id.>  This  command  will  terminate  the  connection  listed  under  the  specified \n" +
                                "number  when  LIST  is  used  to  display  all  connections.");
                        System.out.println("7. send  <connection id.>  <message> This will \n" +
                                "send the message to the host on the connection that is designated by the number 3 when command “list” is \n" +
                                "used. ");
                        System.out.println("8. exit Close all connections and terminate this process. The other peers should also update their connection \n" +
                                "list by removing the peer that exits.");
                    } else if (msg.startsWith("send")) {
                        StringBuilder s = new StringBuilder();
                        int i = 0;
                        for(i=5; i<msg.length(); i++){
                            char character = msg.charAt(i);
                            if(' ' == character){
                                break;
                            }
                            s.append(msg.charAt(i));
                        }
                        String indexStr = s.toString();
                        String receiverIp = null;
                        String receiverPort = null;

                        for(String[] information:list){
                            if(indexStr.equals(information[0])){
                                receiverIp = information[1];
                                receiverPort = information[2];
                            }
                        }


                        String word = msg.substring(i);
                        String message = "send " + receiverIp + " " + receiverPort + " " + this.serverPort + word;
                        Tool.launch(receiverIp, Integer.parseInt(receiverPort), message);

                        System.out.println("Message sent to peer [" + indexStr + "]");
                    } else if (msg.startsWith("connect")) {
                        String[] strs = msg.split(" ");
                        ip = strs[1];
                        port = Integer.parseInt(strs[2]);
                        msg += " " + this.serverPort + " " + "sender";
                        Tool.launch(ip, port, msg);
                    } else if (msg.startsWith("myip")) {
                        System.out.println("My ip is: " + this.clientIp);
                    } else if (msg.startsWith("myport")) {
                        System.out.println("My server listening  port  is: " + this.serverPort);
                    } else if (msg.startsWith("exit")) {
                        String receiverIp = null;
                        String receiverPort = null;

                        for(int i=0; i<list.size(); i++){
                            String[] information = list.get(i);
                            receiverIp = information[1];
                            receiverPort = information[2];
                            String message = "exit " + receiverIp + " " + receiverPort + " " + this.serverPort + " sender";
                            Tool.launch(receiverIp, Integer.parseInt(receiverPort), message);

                        }
                        list = new ArrayList<>();
                        System.out.println("Exit Message sent to peers!");
                        System.out.println("Your server has exited!");
                        System.out.println("---------------Client End---------------------");
                    } else if (msg.startsWith("list")) {
                        for(String[] information:list){
                            System.out.println(information[0] + " " + information[1] + " " + information[2]);
                        }
                    } else if (msg.startsWith("terminate")) {
                        String indexStr = msg.substring(10, msg.length());
                        String receiverIp = null;
                        String receiverPort = null;

                        for(String[] information:list){
                            if(indexStr.equals(information[0])){
                                receiverIp = information[1];
                                receiverPort = information[2];
                            }
                        }
                        String message = "terminate " + receiverIp + " " + receiverPort + " " + this.serverPort + " sender";
                        Tool.launch(receiverIp, Integer.parseInt(receiverPort), message);

                        System.out.println("Terminate Message sent to peer [" + indexStr + "]");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void subStringToIps(String str, String strStart, String strEnd, Map<String, String> result) {
        while (str.indexOf(str) != -1) {
            int strStartIndex = str.indexOf(strStart);
            int strEndIndex = str.indexOf(strEnd);

            if (strStartIndex == -1 || strEndIndex == -1) break;
            String s = str.substring(strStartIndex, strEndIndex).substring(strStart.length());
            String[] infos = s.split(" ");
            result.put(infos[0], infos[2].substring(0, infos[2].length()));
            str = str.substring(strEndIndex + 1);
        }
    }

    public static String cutString(String str, String start, String end) {
        if ("".equals(str)) {
            return str;
        }
        String reg = start + "(.*)" + end;
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            str = matcher.group(1);
        }
        return str;
    }

    @Override
    public void run() {
        sendMsg();
    }
}
