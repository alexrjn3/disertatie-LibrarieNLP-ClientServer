

import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {
        System.out.println("Server pornit...");

        try {
            System.load("D:\\Alex\\alex_proiect\\disertatie\\OrganizeText\\libs\\udpipe_java.dll");
            ServerSocket ss = new ServerSocket(5000);
            for (;;) {
                Socket cs = ss.accept();
                FirClient firPtClient = new FirClient(cs);
                firPtClient.start();
                System.out.println("Client conectat!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
