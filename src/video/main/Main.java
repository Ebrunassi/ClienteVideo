package video.main;
import java.io.IOException;
import java.util.Scanner;

import video.thread.ThreadPrompt;

public class Main{
    public static void main(String args[]) throws IOException{
    	String ip;
        Scanner sc = new Scanner (System.in);
        System.out.println("Servico de transmissao de video iniciado...");
        
        System.out.println("Por favor, insira o ip do servidor :");
        System.out.print("IP: ");
        ip=sc.nextLine();
        
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