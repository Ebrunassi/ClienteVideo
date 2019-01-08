package video.thread;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import video.conexao.Servidor;

public class ThreadReceber extends Thread{
	private String path ="/home/ahborba/Documentos/Uel/so/servidor/ClienteVideo/filme/01/";
	public static int recebido = 0;
	private ServerSocket servidor;
	private Socket cliente;
	private int tamanhoBuffer = 1024;
	private boolean conectado;
	private Servidor server;
	
	
	
	
	public ThreadReceber(String ip, int porta,Servidor server) throws IOException {
		servidor = new ServerSocket(porta);
		conectado = false;
		this.server = server;

	}
	
	
	
	
	public void run() {
		try {
	
			
			String retorno;
			cliente = servidor.accept();
			while(true) {
				while(!conectado) {
					InputStreamReader isr = new InputStreamReader(cliente.getInputStream());
					BufferedReader bfr = new BufferedReader(isr);// cria o buffer de entrada de mensagens
					
					while(!bfr.ready()) {}
					retorno = bfr.readLine();
					if(retorno.equals("00")) {
						//procurar canal e  trocar automaticamente, tentar conexão novamente
					}else {
						conectado = true;
						break;
					}
					cliente.close();
				}
				cliente = servidor.accept();
				
				BufferedInputStream bis = new BufferedInputStream(cliente.getInputStream());
				
				
				File arq = new File("filme.mp4" );
				FileOutputStream out = new FileOutputStream(arq);
				
			    byte[] buffer = new byte[tamanhoBuffer];  
			    int flag = -1;
			    

			    Thread.sleep(2000);
			  

			    while ((flag = bis.read(buffer, 0, tamanhoBuffer)) != -1) {  
			        out.write(buffer, 0, flag);  
			    }
			    server.broadcast();
			    
			    
			    out.flush();  
			    out.close();
			    recebido++;
			    cliente.close();
			    try {
			    	ProcessBuilder builder = new ProcessBuilder("mpv","filme.mp4");
//			    	ProcessBuilder builder = new ProcessBuilder("vlc","--no-play-and-stop filme.mp4");
			    	builder.start();
			    }catch(IOException e) {
			    	e.printStackTrace();
			    }
			    
			}
			
		}catch( Exception e) {
			e.printStackTrace();
		}
	}
	public boolean isConectado() {
		return conectado;
	}




	public void setConectado(boolean conectado) {
		this.conectado = conectado;
	}




	public String getEndereco() {
		return null;
	}
	
	
	public static int getRecebido() {
		return recebido;
	}








	
}


