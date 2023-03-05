package com.app;

import java.io.*;
import java.net.Socket;
import java.util.List;
/**
 * @author Kunlong Wang
 * @create 2023-03-02 1:44 PM
 */
public class ServerThread extends Thread {
    private Socket socket;
    private PrintWriter out;
    private List<String[]> list;

    private int port = 0;

    public ServerThread(Socket socket, List list) {
        this.socket = socket;
        this.list = list;
    }

    @Override
    public void run() {
        try {
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String msg;
            while ((msg = br.readLine()) != null) {
                if (msg.startsWith("send")) {
                    msg = msg.substring(13, msg.length());
                    System.out.println(socket.getRemoteSocketAddress() + " say: " + msg.replaceFirst("send", ""));
                } else if (msg.startsWith("connect")) {
                    String userId;
                    if (list.isEmpty()) {
                        userId = "1";
                    } else {
                        userId = Integer.parseInt(list.get(list.size() - 1)[0]) + 1 + "";
                    }
                    String remoteIpAndPort[] = socket.getRemoteSocketAddress().toString().split(":");
                    String remoteIp = remoteIpAndPort[0].substring(1, remoteIpAndPort[0].length());
                    String port = cutString(msg, "--myport:{", "}");
                    String[] strs = {userId, remoteIp + ":" + port};
                    list.add(strs);
                    System.out.println(socket.getRemoteSocketAddress() + " online");
                } else if (msg.startsWith("exit")) {
                    socket.close();
//                  System.out.println(socket.getRemoteSocketAddress() + " exit");
                    break;
                } else if (msg.startsWith("list")) {
                    msg.replaceFirst("list", "online list: \n");
                    for (String[] s : list) {
                        msg += " id: " + s[0] + " ip: " + s[1] + " is online ";
                    }
                    out = new PrintWriter(socket.getOutputStream());
                    out.println(msg);
                    out.flush();
                } else if (msg.startsWith("terminate")) {
                    String[] s = msg.split(" ");
                    String id = s[1];
                    for (int i = 0; i < list.size(); i++) {
                        String[] current = list.get(i);
                        if (current[0].equals(id)) {
                            list.remove(i);
                            System.out.println("user " + i + 1 + " has been kicked off");
                        }
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
