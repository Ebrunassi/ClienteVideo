package video.main;
import java.io.IOException;
import java.util.Scanner;

import video.conexao.Cliente;
import video.conexao.Servidor;
import video.thread.ThreadPrompt;

public class Main{
    public static void main(String args[]) throws IOException{
        String ip= "localhost";
        int porta = 6060;
        int timeout = 10000;
        int qtdCanal = 2;
        int maxConecoes = 1;
        
        Scanner sc = new Scanner (System.in);
        System.out.println("Servico de transmissao de video iniciado...");
        System.out.println("Por favor, insira alguns dados para realizar a configuracao.");
        
        System.out.println("Conexao ao servidor:");
        System.out.print("IP: ");
//        ip=sc.nextLine();
        ip = "192.168.0.28";

        
        System.out.println("Servidor local:");
        System.out.print("CANAIS: ");
//        qtdCanal= sc.nextInt();
        qtdCanal = 1;
        System.out.print("MAX CONEXOES: ");
//        maxConecoes= sc.nextInt();
        maxConecoes = 1;
        
        Cliente cliente = new Cliente(ip,porta,timeout);
        Servidor servidor = new Servidor(porta,qtdCanal,maxConecoes,cliente);
        
        ThreadPrompt prompt = new ThreadPrompt(cliente);
        servidor.start();
        cliente.start();
        prompt.start();
        try {
			prompt.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        sc.close();
        
         
    }
}