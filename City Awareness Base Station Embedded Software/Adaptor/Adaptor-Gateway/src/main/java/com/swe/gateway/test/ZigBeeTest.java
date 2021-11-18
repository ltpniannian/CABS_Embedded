
package com.swe.gateway.test;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.*;
public class ZigBeeTest {
        @Test
        public static void main(String[] args) throws Exception {
            // TODO 自动生成的方法存根
            String readline = null;
            int port =502;
            byte ipAddressTemp[] = {127, 0, 0, 1};
            InetAddress ipAddress = InetAddress.getByAddress(ipAddressTemp);
            //首先直接创建socket,端口号1~1023为系统保存，一般设在1023之外
            Socket socket = new Socket(ipAddress, port);
            //创建三个流，系统输入流BufferedReader systemIn，socket输入流BufferedReader socketIn，socket输出流PrintWriter socketOut;
            BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter socketOut = new PrintWriter(socket.getOutputStream());
            System.out.print("请输入: \t");
            String str = new BufferedReader(new InputStreamReader(System.in)).readLine();
            while(str != "exit"){
                DataInputStream input = new DataInputStream(socket.getInputStream());
                //向服务器端发送数据
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(str);
                socketOut.println(readline);
                socketOut.flush();

                //outTemp = readline;
                String ret = input.readUTF();
                System.out.println("服务器端返回过来的是: " + ret);
            }
            systemIn.close();
            socketIn.close();
            socketOut.close();
            socket.close();
        }
    }
