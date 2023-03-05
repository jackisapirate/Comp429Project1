package com.app;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author Kunlong Wang
 * @create 2023-03-02 1:44 PM
 */
public class Tool {
    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
//            System.out.println(str.charAt(i));
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }


        /**
         * Check whether a certain port of the machine is occupied
         * @param port
         */
        public static boolean isLoclePortUsing(int port){
            boolean flag = true;
            try{
                flag = isPortUsing("127.0.0.1", port);
            }catch (Exception e){
                e.printStackTrace();
            }
            return flag;
        }

        public static boolean isPortUsing(String host, int port) throws UnknownHostException {
            boolean flag = false;
            InetAddress theAddress = InetAddress.getByName(host);
            try{
                Socket socket = new Socket(theAddress, port);
                flag = true;
            } catch (IOException e) {

            }
            return flag;
        }

}
