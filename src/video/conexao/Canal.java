package video.conexao;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;

public class Canal extends Thread {
	private LinkedList<InetSocketAddress> clientes ;
	int max;
	private int tamanhoBuffer = 1024;

	
	public Canal(int max) {
		clientes = new LinkedList<InetSocketAddress>();
		this.max = max;
	}

	
	
	
	public void broadcast() {
		try {
			
			File video = new File("filme.mp4");
			FileInputStream fis= new FileInputStream(video);
			
			byte buffer[] = new byte[tamanhoBuffer ];
		    
			while (fis.read(buffer, 0, tamanhoBuffer) != -1) {  
		    	for(int i = 0; i < clientes.size(); i++) {
		    		enviar(clientes.get(i),buffer);
		    	}
		    }
		    
			fis.close();
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
			
		
	}

	public boolean enviar(InetSocketAddress destino,byte []bytes){
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
	public String numerar() {
		return Integer.toString(clientes.size());
	}

	public boolean remover(InetSocketAddress cliente) {
		// TODO Auto-generated method stub
		for(int i = 0 ; i < clientes.size() ; i++ ) {
			if(clientes.get(i).equals(cliente)) {
				clientes.remove(i);
				return true;
			}
		}
		return false;
	}

	public String listar() {
		// TODO Auto-generated method stub
		String msg ="[";
		for(int j = 0 ; j < clientes.size() ; j++) {
			InetSocketAddress cliente = clientes.get(j);
			msg = msg + "'" + cliente.getHostName() +"'";
			if( j + 1 < clientes.size()) {
				msg = msg + ",";
			}
		}
		msg = msg + "]";
		return msg;
	}

	public boolean conectar(InetSocketAddress cliente) {
		if(clientes.size() + 1 <= max) {
			clientes.add(cliente);
			return true;
		}
		return false;
	}
	




}
