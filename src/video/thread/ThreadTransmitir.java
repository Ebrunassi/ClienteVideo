package video.thread;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ThreadTransmitir extends Thread {
	private InetSocketAddress destino;
	private int transmitidos;
	private String path ="/home/ahborba/Documentos/Uel/so/servidor/ClienteVideo/filme/01/";
	private int tamanhoBuffer = 1024;

	public  ThreadTransmitir(String ip, int porta) {
		this.destino = new InetSocketAddress(ip,6060);
		transmitidos = 0;

	}
	
	
	
	private boolean lerArquivo() {
		// TODO Auto-generated method stub
		try {
			int flag = -1;
			File video = new File(path+"filme-"+transmitidos+".mp4");
			if(!video.exists()) {
				return false;
			}
			FileInputStream fis = new FileInputStream(video);				
			byte buffer[] = new byte[tamanhoBuffer];
		    while ((flag = fis.read(buffer, 0, tamanhoBuffer)) != -1) {  
		        if(!enviar(buffer))
		        	return false;
		    }
		    fis.close();
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
		
		return true;
	}

	private boolean verificaArquivo() {
		if( ThreadReceber.getRecebido() >= transmitidos) {
			return true;
		}
		return false;
	}

	public boolean enviar(byte []bytes){
		try {
			
	    	Socket sk_enviar = new Socket();// cria um socket
			sk_enviar.connect(destino);//define o endereço do socket
			 	
			OutputStream os = sk_enviar.getOutputStream();
			BufferedOutputStream bos = new BufferedOutputStream(os);
			
			
			
			
			bos.write(bytes);//escreve os bytes no buffer
			bos.flush();//envia (esvazia o buffer)				
			bos.close();// fecha o socket e os buffers
			sk_enviar.close();
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    		return false;
	    	}
	        return true;
	    }

	public InetSocketAddress getDestino() {
				return this.destino;
	}

	public String getHostName() {
		// TODO Auto-generated method stub
		return destino.getHostName();
	}

	
}


