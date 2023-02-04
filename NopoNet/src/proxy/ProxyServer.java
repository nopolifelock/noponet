package proxy;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

import java.util.ArrayList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import feed.FeedServer;

public class ProxyServer extends Thread {
    private  String[] BLACKLISTED = {"porn, xxx, fuck, boobs, ass, tits, dick, cock, pussy, ludes, girls, milf, sex, booty"};
    private  String[] WHITELIST = {"www.github.org"};
    private  String[] KEYWORDS = {"github"};
    private static boolean CHECK = true;
    private String repoLink;
    private FeedServer feedServer;
    public static void main(String[] args) throws IOException {
        
        ProxyServer server = new ProxyServer();
    new Thread(server).start();
        boolean whiteListLoaded = false;
        boolean keyWordsLoaded = false;
        do {
        whiteListLoaded = server.loadWhiteList();
        keyWordsLoaded = server.loadKeyWords();
        }while(!whiteListLoaded || !keyWordsLoaded);
        

    }
    
    private void updateFeedServer(String url) {
        feedServer.updateFeeds(url + "\n");
    }
    
    public boolean loadKeyWords() throws IOException {
        URL url = new URL(repoLink + "/blob/main/keywords.txt");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String line;
        ArrayList<String> list = new ArrayList<String>();
        while((line = reader.readLine()) != null) {
            if(line.contains("class=\"blob-code blob-code-inner js-file-line\">"))
                if(line.contains("class=\"blob-code blob-code-inner js-file-line\">")) {
                    if(line.contains("</td>")){
                        list.add(line.substring(line.indexOf("file-line\">") + 11, line.indexOf("</td>")));
                    }else
                        list.add(line.substring(line.indexOf("file-line\">") + 11));
                }
        }
        KEYWORDS = list.toArray(new String[list.size()]);
        if(list.size()>1) {

            System.out.println("Whitelist refreshed");
        return true;
        }
        
        
        return false;
    }
    
    public boolean  loadWhiteList() throws IOException {
        
        
        URL url = new URL(repoLink + "/blob/main/whitelist.txt");
        
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String line;
        ArrayList<String> list = new ArrayList<String>();
        while((line = reader.readLine()) != null) {
            if(line.contains("class=\"blob-code blob-code-inner js-file-line\">"))
                if(line.contains("class=\"blob-code blob-code-inner js-file-line\">")) {
                    if(line.contains("</td>")){
                        list.add(line.substring(line.indexOf("file-line\">") + 11, line.indexOf("</td>")));
                    }else
                        list.add(line.substring(line.indexOf("file-line\">") + 11));
                }
        }
        WHITELIST = list.toArray(new String[list.size()]);
        if(list.size()>1) {

            System.out.println("Whitelist refreshed");
        return true;
        }
        
        
        return false;
        
    }
    
    public boolean containsBlacklisted(String site) {
        for(String badword: BLACKLISTED) {
            if(site.contains(badword)) {
                return true;
            }
        }
        return false;
    }
    public boolean IS_SAFE(String site) {
        for(String whiteListedSite: WHITELIST) {
            if(site.equals(whiteListedSite))
                return true;
        }

            for(String word: KEYWORDS) {
                if(site.contains(word)) {
                    
                    return true;
                }
            }
            
        
        return false;
    }
    public ProxyServer() {
        super("Server Thread");
        File configFile = new File("C:\\Program Files (x86)\\noponet\\config");
        if(configFile.exists()) {
	        BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(configFile));
				this.repoLink = reader.readLine();
				this.repoLink = repoLink.substring(0,repoLink.indexOf(".git"));
				reader.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        }else {
        	repoLink = "https://github.com/nopolifelock/lists";
        }
            feedServer = new FeedServer(this);
            new Thread(feedServer).start();
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(80)) {
            Socket socket;
            
            try {
                while ((socket = serverSocket.accept()) != null) {
                    (new Handler(socket)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();  // TODO: implement catch
            }
        } catch (IOException e) {
            e.printStackTrace();  // TODO: implement catch
            return;
        }
    }

    public class Handler extends Thread {
        public final Pattern CONNECT_PATTERN = Pattern.compile("CONNECT (.+):(.+) HTTP/(1\\.[01])",
                                                                      Pattern.CASE_INSENSITIVE);
        private final Socket clientSocket;
        private boolean previousWasR = false;

        public Handler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                String request = readLine(clientSocket);
                String host = request.substring(8,request.indexOf(":"));
                
                if(!IS_SAFE(host)) {
                    updateFeedServer(host);
                    if(CHECK)
                            return;
                    
                }else {
                    
                }
                Matcher matcher = CONNECT_PATTERN.matcher(request);
                if (matcher.matches()) {
                    String header;
                    do {
                        header = readLine(clientSocket);
                    } while (!"".equals(header));
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(clientSocket.getOutputStream(),
                                                                                   "ISO-8859-1");

                    final Socket forwardSocket;
                    try {
                        forwardSocket = new Socket(matcher.group(1), Integer.parseInt(matcher.group(2)));
                        
                    } catch (IOException | NumberFormatException e) {
                        e.printStackTrace();  // TODO: implement catch
                        outputStreamWriter.write("HTTP/" + matcher.group(3) + " 502 Bad Gateway\r\n");
                        outputStreamWriter.write("Proxy-agent: Simple/0.1\r\n");
                        outputStreamWriter.write("\r\n");
                        outputStreamWriter.flush();
                        return;
                    }
                    try {
                        outputStreamWriter.write("HTTP/" + matcher.group(3) + " 200 Connection established\r\n");
                        outputStreamWriter.write("Proxy-agent: Simple/0.1\r\n");
                        outputStreamWriter.write("\r\n");
                        outputStreamWriter.flush();

                        Thread remoteToClient = new Thread() {
                            @Override
                            public void run() {
                                forwardData(forwardSocket, clientSocket);
                            }
                        };
                        remoteToClient.start();
                        try {
                            if (previousWasR) {
                                int read = clientSocket.getInputStream().read();
                                if (read != -1) {
                                    if (read != '\n') {
                                        forwardSocket.getOutputStream().write(read);
                                    }
                                    forwardData(clientSocket, forwardSocket);
                                } else {
                                    if (!forwardSocket.isOutputShutdown()) {
                                        forwardSocket.shutdownOutput();
                                    }
                                    if (!clientSocket.isInputShutdown()) {
                                        clientSocket.shutdownInput();
                                    }
                                }
                            } else {
                                forwardData(clientSocket, forwardSocket);
                            }
                        } finally {
                            try {
                                remoteToClient.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();  // TODO: implement catch
                            }
                        }
                    } finally {
                        forwardSocket.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();  // TODO: implement catch
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();  // TODO: implement catch
                }
            }
        }

        private  void forwardData(Socket inputSocket, Socket outputSocket) {
            try {
                InputStream inputStream = inputSocket.getInputStream();
                try {
                    OutputStream outputStream = outputSocket.getOutputStream();
                    try {
                        byte[] buffer = new byte[4096];
                        int read;
                        do {
                            read = inputStream.read(buffer);
                            if (read > 0) {
                                outputStream.write(buffer, 0, read);
                                if (inputStream.available() < 1) {
                                    outputStream.flush();
                                }
                            }
                        } while (read >= 0);
                    } finally {
                        if (!outputSocket.isOutputShutdown()) {
                            outputSocket.shutdownOutput();
                        }
                    }
                } finally {
                    if (!inputSocket.isInputShutdown()) {
                        inputSocket.shutdownInput();
                    }
                }
            } catch (IOException e) {
                //e.printStackTrace();  // TODO: implement catch
            }
        }

        private String readLine(Socket socket) throws IOException {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int next;
            readerLoop:
            while ((next = socket.getInputStream().read()) != -1) {
                if (previousWasR && next == '\n') {
                    previousWasR = false;
                    continue;
                }
                previousWasR = false;
                switch (next) {
                    case '\r':
                        previousWasR = true;
                        break readerLoop;
                    case '\n':
                        break readerLoop;
                    default:
                        byteArrayOutputStream.write(next);
                        break;
                }
            }
            return byteArrayOutputStream.toString("ISO-8859-1");
        }
    }
}