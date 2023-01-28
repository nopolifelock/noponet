package proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Counter implements Runnable{
	
	private ProxyServer server;
	
	public Counter(ProxyServer server) {
		this.server = server;
	}
	@Override
	public void run() {

		Runtime rt = Runtime.getRuntime();
		Process pr;
		try {
			pr = rt.exec("REG ADD \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\" /v ProxyEnable /t REG_DWORD /d 0 /f");
			pr.getInputStream().read();
			pr.destroy();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		while(true) {
					
			
			
			if(netIsAvailable()) {
				if(!proxyIsOn()) {
					System.out.println("Proxy is not on");
				try {
					pr = rt.exec("REG ADD \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\" /v ProxyEnable /t REG_DWORD /d 1 /f");
					pr.getInputStream().read();
					pr.destroy();
					System.out.println("proxy on");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			}else {
				if(proxyIsOn())
				try {
					pr = rt.exec("REG ADD \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\" /v ProxyEnable /t REG_DWORD /d 0 /f");
					pr.getInputStream().read();
					pr.destroy();
					System.out.println("proxy off");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	private static boolean proxyIsOn() {
		try {
			Runtime rt = Runtime.getRuntime();
			BufferedReader reader;
			
			Process pr = rt.exec("REG QUERY \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings\" /v ProxyEnable");
			reader = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line;
			
			while((line = reader.readLine()) !=null) {
				
				if(line.contains("REG_DWORD")) {
					String response = line.substring(line.indexOf("REG_DWORD    ") + 15);
					if(Integer.parseInt(response) > 0)
						return true;
					else
						return false;
				}
			}
			pr.destroy();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return false;
	}
	private boolean netIsAvailable() {
		
	    try {
	        final URL url = new URL("https://raw.githubusercontent.com/nopolifelock/lists/main/internet_test");
	        final URLConnection conn = url.openConnection();
	        conn.connect();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        
	        String line = reader.readLine();
	        if(line.equals("The engines are on.")) {
	        	return true;
	        }
	        reader.close();
	        
	    } catch (MalformedURLException e) {
	        throw new RuntimeException(e);
	    } catch (IOException e) {
	        return false;
	    }
	    return false;
	}

}
