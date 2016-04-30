package client;

import java.awt.EventQueue;

import gui.MainMenu;

public class Main {
	
	  public static void main(String[] args) {
	        
		   final Client clnt = new Client(); 
	        EventQueue.invokeLater(new Runnable() {
	            @Override
	            public void run() {
	                
	                MainMenu ex = new MainMenu(clnt);
	                ex.setVisible(true);
	            }
	        });
	    }
}
