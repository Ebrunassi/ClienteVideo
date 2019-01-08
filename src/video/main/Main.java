package video.main;
import java.io.IOException;
import java.util.Scanner;

import video.thread.ThreadPrompt;

public class Main{
    public static void main(String args[]) throws IOException{
        Scanner sc = new Scanner (System.in);
        System.out.println("Servico de transmissao de video iniciado...");
        System.out.println("Por favor, insira alguns dados para realizar a configuracao.");
        
        System.out.println("Conexao ao servidor:");
        System.out.print("IP: ");
//        ip=sc.nextLine();
        String ip = "192.168.0.32";
        
        ThreadPrompt prompt = new ThreadPrompt(ip);
        prompt.start();
        try {
			prompt.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        sc.close();
    }
}