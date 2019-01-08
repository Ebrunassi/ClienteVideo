package video.thread;

import java.util.LinkedList;
import java.util.Scanner;

import video.conexao.Cliente;
import video.conexao.Servidor;

public class ThreadPrompt extends Thread {
	Scanner sc;
	Cliente cliente;
	LinkedList<String> lista = new LinkedList<String>();

	public ThreadPrompt(Cliente cliente) {
		this.cliente = cliente;
		sc = new Scanner(System.in);

	}

	public void run() {
		String msg = "";
		
		System.out.println("iniciando threadrompt");

		while (sc.hasNextLine()) {

			msg = sc.nextLine().trim();

			if (msg.equals("sair")) {
				System.exit(0);
				break;
			}

			System.out.println("prompt:" + msg);
			if (!cliente.enviar(msg)) {
				if (!cliente.reconectar()) {
					System.out.println("Nao foi possivel achar um novo endereco disponivel, reinicie o processo.");
					Servidor.DeletarFilme();
					System.exit(0);
				}
			}

		}
		
		System.out.println("finalizando threadrompt");

	}
}