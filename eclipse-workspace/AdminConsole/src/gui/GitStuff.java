package gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class GitStuff {
	private Git git;
	private Repository repository;
	private FileRepositoryBuilder builder;
	private File repoDir;
	private String key;
	private String repoLink;
	public GitStuff(String key) {
		this.key = key;
		//list.getSelectedValuesList();
		
		File configFile = new File("C:\\Program Files (x86)\\noponet\\config");
		if(configFile.exists()) {

			try {
				BufferedReader reader = new BufferedReader(new FileReader(configFile));
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
		}
		try {
			cloneRepo();
		} catch (GitAPIException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void appendWebsites(List<String> websites, String repo) {
		
        
       
        
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(repoDir+"/" + repo, true));
			ArrayList<String> WHITELIST = loadList(repo);
			for(String toAdd: websites) {
		
				if(!WHITELIST.contains(toAdd)) {
					if(toAdd.length()>1)
						pw.append("\n" + toAdd);
				}
			}
			pw.close();
			 git.add().addFilepattern(repo).call();
		        git.commit().setMessage("Added new " + repo).call();

		        // Push the changes to the remote repository
		            git.push()
		                .setCredentialsProvider(new UsernamePasswordCredentialsProvider("nopolifelock", key))
		                .call();
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoFilepatternException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
	public void appendWebsite(String website, String repo) {
		List<String> list = new ArrayList<String>();
		list.add(website);
		appendWebsites(list, repo);
	}
    public ArrayList<String> loadList(String repo) throws IOException {
    	
    	
    	URL url = new URL(this.repoLink + "/blob/main/"+ repo);
    	System.out.println(this.repoLink + "/blob/main/"+ repo);
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
    	 return  list;
    }
	private void cloneRepo() throws InvalidRemoteException, TransportException, GitAPIException, IOException { 
		String[] splits = repoLink.split("/");
		String repoName = splits[splits.length-1];
		repoDir = new File(currentPath() + "\\" + repoName);
        if(!repoDir.exists()) {
            // Clone the repository to your local machine
            git = Git.cloneRepository()
                    .setURI(this.repoLink + ".git")
                    .setDirectory(repoDir)
                    .call();
        }else {
            builder = new FileRepositoryBuilder();
            repository = builder.setGitDir(new File(repoDir + "/.git"))
                    .readEnvironment() // scan environment GIT_* variables
                    .findGitDir() // scan up the file system tree
                    .build();
            git = new Git(repository);
        }
        handleBug("whitelist.txt");
        handleBug("keywords.txt");

	}
	
	private void handleBug(String doc) {
		File file = new File(repoDir +"/" +doc);
		ArrayList<String> lines = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			//PrintWriter writer = new PrintWriter(file);
			String line;
			while((line = reader.readLine()) != null) {
				if(!line.equals(""))
					lines.add(line);
			}
			file.createNewFile();
			PrintWriter writer = new PrintWriter(file);
			for(int i = 0 ; i<lines.size(); i++) {
				if(i<lines.size()-1)
					writer.print(lines.get(i) + "\n");
				else
					writer.print(lines.get(i));
			}
			writer.close();
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static String currentPath() {
		try {
			String pathRaw =  new File(GitStuff.class.getProtectionDomain().getCodeSource().getLocation()
				    .toURI()).getPath();
			
			return new File(pathRaw).getParentFile().getPath();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
