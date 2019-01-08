
package video.conexao;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class Servidor extends Thread {
	ServerSocket servidor = null;
	Canal canal = null;
	
	public Servidor() {
		try {
			this.servidor = new ServerSocket(6060);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.canal = new Canal(1);
		this.start();
		
		System.out.println("Servidor iniciado");
	}
	
	public void encerrar() {
		try {
			servidor.close();
			this.join();
			System.out.println("Servidor encerrado...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while(true) {
			try (Socket socket = servidor.accept();
					PrintStream out = new PrintStream(socket.getOutputStream());
					Scanner scan = new Scanner(socket.getInputStream());) {
				String message = scan.nextLine().trim();
				InetSocketAddress cliente = new InetSocketAddress(socket.getInetAddress(), 9091);
				
				switch (message.substring(0, 2)) {
				case "10":
					conectar(cliente);
					break;
				case "11":
					out.println(canal.listar());
					out.flush();
					break;
				case "12":
					canal.remover(cliente);
					break;
				case "13":
					out.println(canal.numerar());
					out.flush();
					break;
				default:
					break;
				}
			} catch (SocketException e) {
				break;
			}
			catch (Exception e) {
			}
		}
	}

	private void conectar(InetSocketAddress cliente) throws IOException {
		try (Socket socket = new Socket(cliente.getAddress(), 9091);
				PrintStream out = new PrintStream(socket.getOutputStream());) {
			if(canal.conectar(cliente)) {
				out.println("10");
			} else {
				out.println("11");
			}
			out.flush();
		}
	}
	
	public void transmitir() {
		canal.transmitir();
	}
}