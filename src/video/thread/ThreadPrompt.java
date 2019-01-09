package video.thread;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

import video.conexao.Cliente;

public class ThreadPrompt extends Thread {
	Scanner sc;
	String ip;
	Cliente cliente = null;
	LinkedList<String> lista = new LinkedList<String>();

	public ThreadPrompt(String ip) {
		this.ip = ip;
		sc = new Scanner(System.in);
	}

	public void run() {
		String msg = "";
		System.out.println("Iniciando Thread do Prompt");

		while (sc.hasNextLine()) {
			msg = sc.nextLine().trim();
			

			
			
			switch (msg.substring(0, 2)) {
			case "10":

					cliente = new Cliente(ip, msg.substring(2, 3));
	
				break;
			case "12":
				if(cliente != null) {
					cliente.encerrar();
				}
				break;
			case "11":
			case "13":
				try (Socket socket = new Socket(this.ip, 6060);
						PrintStream out = new PrintStream(socket.getOutputStream());
						Scanner in = new Scanner(socket.getInputStream());){
					out.println(msg);
					out.flush();
					System.out.println(in.nextLine().trim());
				} catch (IOException e) {
					e.printStackTrace();
				}
			default:
				break;
			}

		}

		System.out.println("Finalizando Thread do Prompt");

	}


}
