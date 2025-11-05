

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        Socket cs = null;
        BufferedReader bfr = null;
        PrintWriter pw = null;
        Scanner sc = new Scanner(System.in);

        try {
            cs = new Socket("localhost", 5000);
            System.out.println("Conectat la server...");
            bfr = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            pw = new PrintWriter(cs.getOutputStream());
            String linieInit;
            while ((linieInit = bfr.readLine()) != null) {
                if (linieInit.equals("<END>")) break;
                System.out.println(linieInit);
            }

            for (;;) {
                System.out.println("\nIntrodu textul (sau scrie STOP pentru a ieși):");
                String text = sc.nextLine();

                if (text.equalsIgnoreCase("STOP")) {
                    pw.println("");
                    pw.flush();
                    break;
                }

                pw.println(text);
                pw.flush();

                String raspuns = bfr.readLine();
                if (raspuns == null) break;

                System.out.println("\n--- Rezultat ---");

                if (!raspuns.equals("<END>"))
                    System.out.println(raspuns);

                String linie;
                while ((linie = bfr.readLine()) != null) {
                    if (linie.equals("<END>")) break;
                    System.out.println(linie);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Client deconectat.");
    }
}
