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
        ip=sc.nextLine();

        
        System.out.println("Servidor local:");
        System.out.print("CANAIS: ");
        qtdCanal= sc.nextInt();
        System.out.print("MAX CONEXOES: ");
        maxConecoes= sc.nextInt();
        sc.close();
        
        Cliente cliente = new Cliente(ip,porta,timeout);
        Servidor servidor = new Servidor(porta,qtdCanal,maxConecoes,cliente);
        
//        ThreadListar listar = new ThreadListar(cliente, 0);//cliente ligado no canal 0
        ThreadPrompt prompt = new ThreadPrompt(cliente);
//        ThreadReceber receber = new ThreadReceber(ip,porta_entrada,servidor);
        servidor.start();
        cliente.start();
//        listar.start();
        prompt.start();
//        receber.start();
        
         
    }
}