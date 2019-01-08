package video.conexao;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;

import video.thread.*;


public class Cliente extends Thread {
	private InetSocketAddress endereco;

	private int timeout;
    private ThreadListar teste;
    public  boolean transmissao;
    private InetSocketAddress raiz;
    private int canal;
    private boolean conectado;
    private ServerSocket servidor;
    private int tamanhoBuffer;
    private Servidor server;
    private LinkedList<InetSocketAddress> listaConexoes;
    //construtor que inicializa o ip e a porta do servidor aonde esta hospedado o video
    public Cliente(String ip,int porta, int timeout) throws IOException{
        this.endereco = new InetSocketAddress(ip,porta);
        this.raiz = endereco;
        this.timeout = timeout;
        this.transmissao = true;
        this.conectado = false;
        this.tamanhoBuffer = 1024;
        this.servidor = new ServerSocket(9091);
    }

   
    
    
    
    
    
    public void run() {
		try {
			
			
			String retorno;
			Socket cliente = servidor.accept();
			
			while(true) {
				
	
				while(!conectado) {
					
					InputStreamReader isr = new InputStreamReader(cliente.getInputStream());
					BufferedReader bfr = new BufferedReader(isr);// cria o buffer de entrada de mensagens
					
					while(!bfr.ready()) {}
					
					
					retorno = bfr.readLine();
					
					
					cliente.close();
					if(retorno.equals("00")) {
						if(!reconectar()) {
							System.out.println("Nao foi possivel achar um novo endereco com conexoes disponiveis, reinicie o processo.");
							Servidor.DeletarFilme();
							System.exit(0);reconectar();							
						}
					}else {
						conectado = true;
						break;
					}
				}
				
				
				cliente = servidor.accept();
				
				BufferedInputStream bis = new BufferedInputStream(cliente.getInputStream());
				
				
				File arq = new File("filme.mp4" );
				FileOutputStream out = new FileOutputStream(arq);
				
			    byte[] buffer = new byte[tamanhoBuffer];  
			    int flag = -1;
			    

			  

			    while ((flag = bis.read(buffer, 0, tamanhoBuffer)) != -1) {  
			        out.write(buffer, 0, flag);  
			    }
			    server.broadcast();
			    
			    
			    out.flush();  
			    out.close();
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
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    //Metodo que envia comunica as mensagens ao servidor.
    public boolean enviar(String msg){
    	try {
    		String lista;
			int cod = Integer.parseInt(msg.subSequence(0,2).toString());
    		int canalCliente = Integer.parseInt(msg.substring(2).toString()); // descobre qual é o canal que o cliente esta se conectando
    		canal = canalCliente;
    		
    		
    		teste.setCanal(canalCliente);// seta esse canal para a mensagem automatica que obtem a lista de conectados do canal
    		Socket sk_enviar = new Socket();// cria um socket
		 	sk_enviar.connect(endereco, timeout);//define o endereço do socket
		 	
        	OutputStream os = sk_enviar.getOutputStream();
			PrintWriter saida = new PrintWriter(os);// cria o buffer de saída de mensagens
			
			InputStreamReader isr = new InputStreamReader(sk_enviar.getInputStream());
			BufferedReader bfr = new BufferedReader(isr);// cria o buffer de entrada de mensagens
			
			
			System.out.println("enviando:"+msg);
        	saida.println(msg);// escreve a mensagem
			saida.flush();//envia (esvazia o buffer)
			
			if(cod == 11) {
				while(!bfr.ready()) {}// espera receber a mensagem
				lista = bfr.readLine();
				System.out.println(lista);// recebe e escreve a mensagem
			}
			
			saida.close();// fecha o socket e os buffers
			bfr.close();
			sk_enviar.close();

			return true;
    	} catch (Exception e) {
    		// se ele vier aqui, é pq desconectou.... 
//    		e.printStackTrace();
    		return false;
    	}
    }   
    
    public boolean reconectar() {
    	this.conectado= false;
    	String clientesRaiz = requerirLista(raiz);
		if(clientesRaiz.equals("erro")) {
			System.out.println("Servidor raiz desconectado, fim de execucao.");
    		Servidor.DeletarFilme();
			System.exit(0);
		}else if(clientesRaiz.equals("[]")) {
			System.out.println("Ninguem conectado no servidor raiz, nao e possivel fazer reconexao.\n Fim de execucao.");
    		Servidor.DeletarFilme();
			System.exit(0);
		}else {
			listaConexoes = criarLista(clientesRaiz);
			for(int i = 0; i < listaConexoes.size() ; i++) {
				if(testarConexao(listaConexoes.get(i))) {
						this.endereco = listaConexoes.get(i);
						return true;
				}else {
					adicionarConectados(listaConexoes.get(i));
					listaConexoes.remove(i);
				}
			}
		}
	  

	  return false;
  }

    private void adicionarConectados(InetSocketAddress end) {
	// TODO Auto-generated method stub
    	String clientes = requerirLista(end);
    	if(listaConexoes == null) {
    		return;
    	}
		LinkedList<InetSocketAddress> lista = criarLista(clientes);
		
		for(int i = 0 ; i < lista.size() ; i++ ) {
			listaConexoes.add(lista.get(i));

		}
		
		
}

	private LinkedList<InetSocketAddress> criarLista(String clientesRaiz) {
	// TODO Auto-generated method stub
		LinkedList<String> lista = new LinkedList<String>(Arrays.asList(clientesRaiz.split("'")));
		lista.remove(0);
		lista.remove(lista.size()-1);
		for(int i=0;i<lista.size();i++) {
			if(lista.get(i).equals(",")) {
				lista.remove(i);
			}
		}
		LinkedList<InetSocketAddress> enderecos = new LinkedList<InetSocketAddress>();
		for(int i=0;i<lista.size();i++) {
			enderecos.add(new InetSocketAddress(lista.get(i),6060));
		}
		
		if(lista.size() == 0 ) {
			return null;
		}
		
		return  enderecos;
}
	
	private String requerirLista(InetSocketAddress end) {
    	try {
    		String lista;
    		
    		Socket sk_enviar = new Socket();// cria um socket
		 	sk_enviar.connect(end, timeout);//define o endereço do socket
		 	
        	OutputStream os = sk_enviar.getOutputStream();
			PrintWriter saida = new PrintWriter(os);// cria o buffer de saída de mensagens
			
			InputStreamReader isr = new InputStreamReader(sk_enviar.getInputStream());
			BufferedReader bfr = new BufferedReader(isr);// cria o buffer de entrada de mensagens
			
        	saida.println("11"+canal);// escreve a mensagem
			saida.flush();//envia (esvazia o buffer)
			
			while(!bfr.ready()) {}// espera receber a mensagem
			lista = bfr.readLine();
			
			saida.close();// fecha o socket e os buffers
			bfr.close();
			sk_enviar.close();

			return lista;
    	} catch (Exception e) {
    		// se ele vier aqui, é pq desconectou.... 
    		return "erro";
    	}
   
        
}

	public boolean testarConexao(InetSocketAddress endSk) {
		try {
			Socket sk = new Socket();
	    	InetSocketAddress novoEndereco = new InetSocketAddress("127.0.0.1",2019);
	    	System.out.println("Conectando-se a: "+novoEndereco);
				sk.connect(novoEndereco);
			if(sk.isConnected()) {
				sk.close();
				return true;
			}
			sk.close();
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	public boolean existe() {
		try {
			Socket sk_enviar = new Socket();// cria um socket
			sk_enviar.connect(endereco, timeout);//define o endereço do socket
	 	
		 	if(sk_enviar.isConnected()) {
		 		sk_enviar.close();
		 		return true;
		 	}
		 	sk_enviar.close();
		 	return false;
		} catch (IOException e) {
			return false;
		}
	}







	public void setServer(Servidor servidor2) {
		// TODO Auto-generated method stub
		this.server = servidor2;
		
	}
	

    
}
