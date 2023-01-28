package feed;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import proxy.ProxyServer;

public class FeedServer implements Runnable{
	private ServerSocket serverSocket;
	private ArrayList<FeedConnection> connections = new ArrayList<FeedConnection>();
	private ProxyServer proxyServer;
	
	public FeedServer(ProxyServer proxyServer) {
		this.proxyServer = proxyServer;
		try {
			this.serverSocket = new ServerSocket(13371);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		
		while(true) {
			try {
				FeedConnection con = new FeedConnection(serverSocket.accept(), this);
				new Thread(con).start();
				connections.add(con);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public ProxyServer getProxyServer() {
		return proxyServer;
	}
	public void updateFeeds(String url) {
		for(FeedConnection con: connections) {
			con.send(url);
		}
	}
	public void removeConnection(FeedConnection con) {
		connections.remove(con);
	}
}
