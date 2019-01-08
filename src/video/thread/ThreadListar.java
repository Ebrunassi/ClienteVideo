package video.thread;

import java.util.Arrays;
import java.util.LinkedList;

import video.conexao.Cliente;

public class ThreadListar extends Thread{

    LinkedList<String> listaConectados = new LinkedList<String>();
    long timeout = 2000;
    Cliente cliente;
    int canal;
    
 

    public ThreadListar(Cliente cliente,int canal) {
    	this.canal = canal;
    	this.cliente = cliente;
    	
    }

    public void run(){
    	try {
			int i = 1;
			
	        while(true){
	        	Thread.sleep(timeout);
	        	System.out.println("recebido:\t"+ThreadReceber.getRecebido());
	        	System.out.print(i+":\t");
	        	long t1 = System.currentTimeMillis();
	            if(!cliente.enviar("11"+canal)) {// mensagem é enviada
	            	if(cliente.existe()) {// então é o servidor...
	            		continue;
	            	}else {	            		
	            		System.out.println("Conectando ao proximo servidor.");
	            		
	            		if(!cliente.reconectar(listaConectados)) {
	            			System.out.println("Nao foi possivel encontrar um servidor disponivel.\nEncerrando processos...");
	            			System.exit(0);
	            		}
	            		System.out.println("Tempo:"+(System.currentTimeMillis()-t1));
	            		listaConectados = new LinkedList<String>();
	            	}
	            }	            
	            i++;
	        }

    	} catch (InterruptedException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}// tempo de espera da thread
    	
    }
    

    public LinkedList<String> getLista(){
        return this.listaConectados;

    }

    public void setCanal(int cn){
        if(cn != canal){
            listaConectados = new LinkedList<String>();
            this.canal=cn;
        }
    }

	public void construirLista(String lista) {
		if(lista.equals("[]")) {
			this.listaConectados = new LinkedList<String>();
			return;
		}
		LinkedList<String> novaLista = new LinkedList<String>(Arrays.asList(lista.split("'")));
		novaLista.remove(0);
		novaLista.remove(novaLista.size()-1);
		for(int i=0;i<novaLista.size();i++) {
			if(novaLista.get(i).equals(",")) {
				novaLista.remove(i);
			}
//			System.out.println("Valor:\t"+novaLista.get(i));
		}
		this.listaConectados = novaLista;
		
	}
	public void novaLista() {
		this.listaConectados = new LinkedList<String>();
	}
}