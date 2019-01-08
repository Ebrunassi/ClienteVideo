package video.conexao;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;

public class Canal extends Thread {
	private LinkedList<InetSocketAddress> clientes;
	int max;
	private int tamanhoBuffer = 1024;

	public Canal(int max) {
		clientes = new LinkedList<InetSocketAddress>();
		this.max = max;
	}
	
	public void transmitir() {
		for(InetSocketAddress cliente : clientes) {
			try (Socket socket = new Socket(cliente.getAddress(), 9091);
					BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
					FileInputStream file = new FileInputStream("filme.mp4");) {
				
				int leu;
				byte[] buffer = new byte[tamanhoBuffer];
				while((leu = file.read(buffer)) > 0) {
					out.write(buffer, 0, leu);
					out.flush();
				}
				System.out.println("Video enviado para " + cliente.getHostName());
			} catch (IOException e) {
				remover(cliente);
			}
		}
	}

	public String numerar() {
		return Integer.toString(clientes.size());
	}

	public boolean remover(InetSocketAddress cliente) {
		return clientes.remove(cliente);
	}

	public String listar() {
		String msg = "[";
		for (int j = 0; j < clientes.size(); j++) {
			InetSocketAddress cliente = clientes.get(j);
			msg = msg + "'" + cliente.getHostName() + "'";
			if (j + 1 < clientes.size()) {
				msg = msg + ",";
			}
		}
		msg = msg + "]";
		return msg;
	}

	public boolean conectar(InetSocketAddress cliente) {
		if (clientes.size() < max && !clientes.contains(cliente)) {
			clientes.add(cliente);
			return true;
		}
		return false;
	}

}
