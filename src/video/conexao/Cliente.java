package video.conexao;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Scanner;

public class Cliente implements Runnable {
	InetSocketAddress raiz = null;
	InetSocketAddress atual = null;
	String canal = null;
	ServerSocket server = null;
	int timeout = 3000;
	Thread thread = null;
	
	public Cliente(String ip, String canal) {
		this.raiz = new InetSocketAddress(ip, 6060);
		this.canal = canal;
		
		try {
			server = new ServerSocket(9091);
			server.setSoTimeout(timeout);
			
			conectar();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void conectar() {
		ArrayList<InetSocketAddress> fila = new ArrayList<InetSocketAddress>();
		fila.add(raiz);
		int i = 0;
		
		while(true) {
			atual = fila.get(i);
			
			if(!requisitarConexao()) {
				fila.remove(i);
				continue;
			}
			
			try (Socket socket = server.accept();
					Scanner scan = new Scanner(socket.getInputStream());) {
				String message = scan.nextLine().trim();
				
				switch (message.substring(0, 2)) {
				case "00":
					if(obterClientes(fila, i)) {
						i++;
						if(i > fila.size()) {
							i = 0;
						}
					}
					break;
				case "10":
					thread = new Thread(this);
					thread.start();
					System.out.println("Iniciando recebimento de videos.");
					return;
				default:
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean requisitarConexao() {
		try (Socket socket = new Socket(atual.getAddress(), 6060);
				PrintStream out = new PrintStream(socket.getOutputStream());) {
			out.println("10" + canal);
			out.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean obterClientes(ArrayList<InetSocketAddress> fila, int i) throws IOException {
		try (Socket socket = new Socket(atual.getAddress(), atual.getPort());
				PrintStream out = new PrintStream(socket.getOutputStream());
				Scanner scan = new Scanner(socket.getInputStream());) {
			out.println("11" + canal);
			out.flush();
			
			for(String ip : scan.nextLine().trim().replaceAll("[\\[\\] ']", "").split(",")) {
				fila.add(new InetSocketAddress(ip, 6060));
			}
			return true;
		} catch (SocketTimeoutException e) {
			fila.remove(i);
			return false;
		}
	}
	
	public void encerrar() {
		try (Socket socket = new Socket(atual.getAddress(), 6060);
				PrintStream out = new PrintStream(socket.getOutputStream());
				Scanner in = new Scanner(socket.getInputStream());){
			out.println("12" + canal);
			out.flush();
			
			server.close();
			thread.join();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		Servidor servidor = new Servidor();
		
		while(true) {
			try (Socket socket = server.accept();
					FileOutputStream file = new FileOutputStream("filme.mp4");
					BufferedInputStream in = new BufferedInputStream(socket.getInputStream());) {
				
				int leu;
				byte[] buffer = new byte[1024];
				while((leu = in.read(buffer)) > 0) {
					file.write(buffer, 0, leu);
					file.flush();
				}
			} catch (SocketTimeoutException e) {
				servidor.encerrar();
				conectar();
				break;
			} catch (SocketException e) {
				servidor.encerrar();
				System.out.println("Encerrando recebimento de videos.");
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Filme recebido");
			
			//TODO abrir video
			
			try {
				ProcessBuilder builder = new ProcessBuilder("C:\\Program Files\\VideoLAN\\VLC\\vlc.exe","--one-instance","filme.mp4");
				builder.start();
			} catch (IOException e) {
				System.out.println("Para reproduzir o video instale o vlc.");
			}
			
			
			servidor.transmitir();
		}
	}
}