/**
 * 
 */
package video.conexao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

/**
 * @author ahborba
 *
 */
public class Servidor extends Thread {
	private ServerSocket servidor;
	private LinkedList<Canal> lista;
	private int qtdCanal;
	private Cliente threadCliente;
	
	public Servidor(int porta,int qtdCanal,int max,Cliente cliente) {
		try {
			this.lista =  new LinkedList<Canal>();
			this.servidor = new ServerSocket(porta);
			this.qtdCanal = qtdCanal;
			this.threadCliente = cliente;
			this.threadCliente.setServer(this);
			criarListas(max);
			
			
		} catch (IOException e) {
			System.out.println("Nao foi possivel abrir o servidor na porta: "+porta+".Porta ja esta sendo utilizada por outro processo");
		}
	
	}



	public void run(){
    	String msgRecebida;
    	String msgResposta;
    	Canal canal;
    	InetSocketAddress cliente;
    	try {
    		
    		
	    	while(true) {
	    	
	    			msgRecebida="";
					Socket socket = servidor.accept();
					
					InputStreamReader isr = new InputStreamReader(socket.getInputStream());
					BufferedReader bfr = new BufferedReader(isr);// cria o buffer de entrada de mensagens
					
					while(!bfr.ready()) {}// espera receber a mensagem
					msgRecebida = bfr.readLine();
					System.out.println(msgRecebida);
					
					int cod = Integer.parseInt(msgRecebida.subSequence(0,2).toString());
			    	int canalCliente = Integer.parseInt(msgRecebida.substring(2).toString());

			    	System.out.println("  \t recebido:"+msgRecebida);
			    	System.out.println("endereco:"+socket.getInetAddress().getHostAddress()+"\nporta: 6060");

			    	if(canalCliente < qtdCanal) {
			    		canal = lista.get(canalCliente);
			    		cliente = new InetSocketAddress(socket.getInetAddress().getHostAddress(),socket.getPort());
			    		//CONECTAR EM UM CANAL
			    		if(cod==10) {
			    			verificarConexao(cliente);
			    			if(canal.conectar(cliente)) {
			    				enviar(cliente,"10");
			    			}else {
			    				enviar(cliente,"00");
			    			}
			    		}
			    		//LISTAR CANAL
			    		else if( cod == 11 ) {
			    			msgResposta = canal.listar();
			    			enviar(cliente,msgResposta);
			    			System.out.println("Enviando lista de clientes do canal "+canalCliente+" para o endereco "+socket.getInetAddress().getHostAddress());

			    		}
			    		//REMOVER DO CANAL
			    		else if(cod == 12 ) {
			    			canal.remover(cliente);
			    		}
			    		//NUMERAR CONEXOES
			    		else if( cod == 13){
			    			msgResposta = canal.numerar();
			    			enviar(cliente,msgResposta);
			    		}
			    	}
			    	
			    	socket.close();
						
	    	}
	    	

    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
	
	
	private void verificarConexao(InetSocketAddress cliente) {
		// TODO Auto-generated method stub
		for(int i = 0; i < lista.size() ; i++) {
			Canal canal = lista.get(i);
			if(canal.remover(cliente)) {
				return;
			}
		}
		
	}


	public boolean enviar(InetSocketAddress endereco,String msg){
		try {
			
	    	Socket sk_enviar = new Socket();// cria um socket
			sk_enviar.connect(endereco);//define o endereço do socket
			 	
			OutputStream os = sk_enviar.getOutputStream();
			PrintWriter saida = new PrintWriter(os);// cria o buffer de saída de mensagens
				
	        saida.println(msg);// escreve a mensagem
			saida.flush();//envia (esvazia o buffer)				
			sk_enviar.close();
			saida.close();// fecha o socket e os buffers
			System.out.println("enviando:"+msg); 
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    		return false;
	    	}
	        return true;
	    }



	
	//cria uma lista de clientes para cada canal conforme a quantidade de canais informada
	private void criarListas(int max) {
		for(int i = 0 ; i < qtdCanal ; i++) {
			Canal canal = new Canal(max);
			System.out.println("Nova Thread iniciada para o canal"+i+" .");
			canal.start();
			lista.add(canal);
		}
	}



	public void broadcast() {
		for(int i = 0 ; i < lista.size() ; i++) {
			Canal canal = lista.get(i);
			canal.broadcast();
		}
	}



	public static void DeletarFilme() {
		// TODO Auto-generated method stub
		
	}
	


}








//
//
//
//
//
//
////conectar em um canal			    	
//if(cod == 10 ){ 
//	if(canalCliente < qtdCanal) {
//		verificarCanal(thCliente); //verifica se já esta em algum outro canal
//		adicionarCanal(thCliente,canalCliente);
//		thCliente.start();
//		//criar thread de transmissao;
//	}
//}
////listar canal    	
//else if( cod == 11 ){
//	if( canalCliente < qtdCanal ) {
//		msgResposta = listarCanal(canalCliente);
//		enviar(c,msgResposta);
//		System.out.println("Enviando lista de clientes do canal "+canalCliente+" para o endereco "+socket.getInetAddress().getHostAddress()+" .");
//	}
//
//}
////remover do canal
//else if( cod == 12){
//	if(canalCliente < qtdCanal) {
//		ThreadTransmitir th = obterCliente(thCliente,canalCliente);
//		if( th != null) {
////			encerrarThread(th);
//		}
//		remover(thCliente,canalCliente);
//		
//	}
//}
////numerar canal
//else if ( cod == 13){
//	if( canalCliente < qtdCanal ) {			    			
//		msgResposta =numerarCanal(canalCliente);
//		this.enviar(thCliente.getDestino(),msgResposta);
//		System.out.println("Enviando lista de clientes do canal "+canalCliente+" para o endereco "+socket.getInetAddress().getHostAddress()+" .");
//	}
//}


//	//lista todos os clientes conectados em um determinado canal
//	private String listarCanal(int canalCliente) {
//		LinkedList<ThreadTransmitir> canal = lista.get(canalCliente);
//		String msg ="[";
//		
//		for(int j = 0 ; j < canal.size() ; j++) {
//			ThreadTransmitir cliente = canal.get(j);
//			msg = msg + "'" + cliente.getHostName() +"'";
//			if( j + 1 < canal.size()) {
//				msg = msg + ",";
//			}
//		}
//		msg = msg + "]";
//		return msg;
//	}
//
