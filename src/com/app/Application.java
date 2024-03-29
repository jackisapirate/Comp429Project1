package com.app;

import java.util.ArrayList;
import java.util.Scanner;
/**
 * @author Kunlong Wang
 * @create 2023-03-02 1:44 PM
 */
public class Application {
    String port;
    private ArrayList<String[]> list;

    public void startChat() {
        System.out.println("Hello! Your terminal has launched!");
        System.out.println("Please input your Server Port that is listening for incoming connections:");
        Scanner scanner = new Scanner(System.in);
        String port = scanner.nextLine();
        while(!Tool.isNumeric(port)){
            System.out.println("This Server Port must be numeric! Input it again:");
            port = scanner.nextLine();
        }
        while(Tool.isLoclePortUsing(Integer.parseInt(port))){
            System.out.println("This port is occupied by another process! Please change another and input it again:");
            port = scanner.nextLine();
        }
        list = new ArrayList<>();
        Server server = new Server(Integer.parseInt(port), list);
        Client client = new Client(Integer.parseInt(port), list);
        new Thread(server).start();
        new Thread(client).start();
    }

    public static void main(String[] args) {
        Application chat = new Application();
        chat.startChat();
    }
}
