import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Scanner;
import java.nio.file.*;

public class Setup {
	public static void main(String[] args) {
		ArrayList<String> users = getUsers();
		Scanner input = new Scanner(System.in);
		
		System.out.println("Select the account to install to:");
		for(String user: users) {
			System.out.println(user);
		}
		
		
		String user;
		do {
			user = input.nextLine();
			if(!users.contains(user))
				System.out.println("user invalid, try again");
			
		}while(!users.contains(user));
		
		input.close();
		
		try {
			disableProxySettings(user);
			//disableTaskmanager(user);
			installStartup(user);
		} catch (InstallException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			System.out.println("Keep the change you filthy animal");
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	public static String currentPath() {
		try {
			String pathRaw =  new File(Setup.class.getProtectionDomain().getCodeSource().getLocation()
				    .toURI()).getPath();
			
			return new File(pathRaw).getParentFile().getPath();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static void installStartup(String user) throws IOException {
		
		String taskScheduler = "REG ADD \"HKEY_USERS\\" + user +"\\Software\\Microsoft\\Windows\\CurrentVersion\\Run\" /v \"nopolauncher\" /t REG_SZ /d \"C:\\PROGRA~2\\noponet\\nopolauncher.exe\"";
		File nopoNetFolder = new File("C://Program Files (x86)//noponet");
		Runtime rt = Runtime.getRuntime();
		Process pr = rt.exec(taskScheduler);
		String response = getResponse(pr);
		pr.destroy();
		File accountsFile = new File("C:\\PROGRA~2\\noponet\\accounts");
		if(!accountsFile.exists())
			accountsFile.createNewFile();
		PrintWriter writer = new PrintWriter(accountsFile);
		writer.append(user);
		writer.close();
		nopoNetFolder.mkdirs();
		download("https://github.com/nopolifelock/lists/releases/download/release/noponet.jar", "C:\\Program Files (x86)\\noponet\\noponet.jar" );
		download("https://github.com/nopolifelock/lists/releases/download/release/nopolauncher.exe", "C:\\Program Files (x86)\\noponet\\noponlauncher.exe" );
		
		pr.getInputStream().read();
		pr.destroy();
	}
	public static void download(String url, String path) {
		try (BufferedInputStream inputStream = new BufferedInputStream(new URL(url).openStream());
				  FileOutputStream fileOS = new FileOutputStream(path)) {
				    byte data[] = new byte[1024];
				    int byteContent;
				    while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
				        fileOS.write(data, 0, byteContent);
				    }
				} catch (IOException e) {
				    // handles IO exceptions
				}
	}

	public static void disableTaskmanager(String user) {
		Runtime rt = Runtime.getRuntime();
		
		Process pr;
		
		try {
			pr = rt.exec("REG ADD \"HKEY_USERS\\" + user + "\\Software\\Microsoft\\Windows\\CurrentVersion\\Policies\\System\" /v DisableTaskMgr /t REG_DWORD /d 1 /f");
			pr.getInputStream().read();
			pr.destroy();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String getResponse(Process pr) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		
		String line;
		
		String response = "";
		try {
			while((line = reader.readLine()) != null) {
				response += line + "\n";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}
	public static void disableProxySettings(String user) throws InstallException {
		Runtime rt = Runtime.getRuntime();
		try {
			Process pr = rt.exec("reg load HKU\\" + user + " C:\\Users\\" + user +"\\ntuser.dat");
			String response = getResponse(pr);
			if(!response.contains("successfully"))
				throw new InstallException(response);
			
			
			//add the proxy and turn it on 
			pr = rt.exec("REG ADD \"HKEY_USERS\\" + user + "\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\" /v \"ProxyEnable\" /t REG_DWORD /d \"0\" /f");
			pr.getInputStream().read();
			pr.destroy();
			pr = rt.exec("REG ADD \"HKEY_USERS\\" + user + "\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\" /v \"ProxyServer\" /t REG_SZ /d \"127.0.0.1:80\" /f");
			pr.getInputStream().read();
			pr.destroy();//Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\" /v ProxyEnable /t REG_DWORD /d 0 /f
			pr = rt.exec("REG ADD \"HKEY_USERS\\" + user + "\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\" /v ProxyEnable /t REG_DWORD /d 1 /f");
			pr.getInputStream().read();
			pr.destroy();
			
			
			//disable proxy control
			pr = rt.exec("REG ADD \"HKEY_USERS\\" + user + "\\SOFTWARE\\Policies\\Microsoft\\Internet Explorer\"");
			pr.destroy();
			pr.getInputStream().read();
			pr = rt.exec("REG ADD \"HKEY_USERS\\" + user + "\\SOFTWARE\\Policies\\Microsoft\\Internet Explorer\\Control Panel\"");
			pr.destroy();
			pr.getInputStream().read();
			pr = rt.exec("REG ADD \"HKEY_USERS\\" + user + "\\SOFTWARE\\Policies\\Microsoft\\Internet Explorer\\Control Panel\" /v Proxy /t REG_DWORD /d 1");
			pr.getInputStream().read();
			pr.destroy();
			
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static ArrayList<String> getUsers() {
		ArrayList<String> users = new ArrayList<String>();
			
			for (File userDirectory : new File("C:/Users").listFiles())
			{
				String userName = userDirectory.getName();
				users.add(userName);
				
			}
		return users;
	}
	

}class InstallException extends Exception{
	public InstallException(String cmd) {
		super("You need to log out of the user.");
		System.out.println(cmd);
	}
}
