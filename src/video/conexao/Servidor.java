
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
	
	public Servidor(int max) {
		try {
			this.servidor = new ServerSocket(6060);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.canal = new Canal(max);
		this.start();
		
		System.out.println("Servidor local iniciado");
	}
	
	public void encerrar() {
		try {
			servidor.close();// cria a excecao que mata a thread
			this.join();//espera a thread chegar aqui
			System.out.println("Servidor local encerrado.");
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
				String msg = scan.nextLine().trim();
				InetSocketAddress cliente = new InetSocketAddress(socket.getInetAddress(), 9091);
				
				switch (msg.substring(0, 2)) {
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
				out.println("00");
			}
			out.flush();
		}
	}
	
	public void transmitir() {
		canal.transmitir();
	}
}