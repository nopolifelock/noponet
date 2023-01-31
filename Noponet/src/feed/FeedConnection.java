package feed;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class FeedConnection implements Runnable{
	private Socket sock;
	private BufferedWriter writer;
	private BufferedReader reader;
	private FeedServer server;
	private boolean running = true;
	public FeedConnection(Socket sock, FeedServer server) {
		this.server = server;
		this.sock = sock;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		while(running) {
			try {
				String request = reader.readLine();
				handle(request);
			} catch (IOException e) {
				e.printStackTrace();
				this.close();
				running = false;
				server.removeConnection(this);
			}
			
		}
		
	}
	private void handle(String request) {
	
		switch(request) {
		case "refresh":
			try {
				server.getProxyServer().loadKeyWords();
				server.getProxyServer().loadWhiteList();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
	}
	public void send(String message) {
		try {
			//System.out.println(message);
			writer.write(message);
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void close() {
		
		try {	running = false;
			reader.close();
			writer.close();
			sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
