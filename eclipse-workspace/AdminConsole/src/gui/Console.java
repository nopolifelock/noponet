package gui;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import javax.swing.JOptionPane;

import org.eclipse.jgit.api.Git;

public class Console implements Runnable{
	private Socket socket;
	private BufferedWriter writer;
	private BufferedReader reader;
	private Frame frame;
	private GitStuff git;
	public static void main(String[] args) {
		String password = JOptionPane.showInputDialog(null, "Enter the git key");
		Console c = new Console(password);
		new Thread(c).start();
	}
	public Console(String key) {
		Frame frame = new Frame(this);
		frame.setVisible(true);
		this.frame = frame;
		this.git = new GitStuff(key);
		try {
			this.socket = new Socket("localhost",13371);
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (UnknownHostException e) {
			frame.updateLabel("error connecting to server");
			e.printStackTrace();
		} catch (IOException e) {
			frame.updateLabel("error connecting to server");
			e.printStackTrace();
		}
		
	}
	
	public void addToGit(List<String> elements, String repo) {
		git.appendWebsites(elements, repo);
	}
	public void addToGit(String element, String repo) {
		git.appendWebsite(element, repo);
	}
	
	
	//public void addTo
	@Override
	public void run() {
		while(true) {
			try {
				System.out.println(reader.readLine());
				this.frame.addToList(reader.readLine());
				frame.updateLabel("");
			} catch (IOException e) {
				frame.updateLabel("error connecting to server");
				e.printStackTrace();
			}
		}
	}
	
	public void send(String request) {
		try {
			writer.write(request);
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			frame.updateLabel("error connecting to server");
		}
	}
	

}
