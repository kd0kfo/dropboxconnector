package com.davecoss.Dropbox;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;


public class CommandLine {
	
	public CommandLine () {
		
	}
	
	public static void sanity_check(String[] args) {
		if(args.length == 0) {
			print_usage();
			System.exit(1);
		}
		String command = args[0].toLowerCase();
		switch(command) {
		case "upload":
			if(args.length < 3) {
				System.err.println("Missing source and destination files.");
				System.exit(1);
			}
			File inputFile = new File(args[1]);
			if(!inputFile.exists()) {
	        	System.err.println("No such file: " + inputFile.getPath());
	        	System.exit(1);
	        }
	        if(!inputFile.isFile()) {
	        	System.err.println(inputFile.getPath() + " is not a file.");
	        	System.exit(1);
	        }
	        break;
		case "mkdir":
			if(args.length < 2) {
				System.err.println("Missing directory name.");
				System.exit(1);
			}
		}
	}
	
	public static void print_usage() {
		System.out.println("Options:");
		System.out.println("ls [path]");
		System.out.println("mkdir [path]");
		System.out.println("upload <source file> <destination file>");
	}
	
    public static void main(String[] args) throws IOException, DbxException, URISyntaxException {
	       
    		if(args.length == 0 || args[0].equals("help")) {
    			print_usage();
    			System.exit(0);
    		}
    		String command = args[0].toLowerCase();
    		
    		// Sanity check before connecting.
    		sanity_check(args);
    		
	        // Connect
    		DbxClient client = Connector.connect(new APIKeyStore("appkey.properties"));

	        // Report with whom we're working
	        System.out.println("Linked account: " + client.getAccountInfo().displayName);

	        switch(command.toLowerCase()) {
	        case "ls":
	        	String query = (args.length > 1) ? args[1] : "/";
	        	DbxEntry[] dirents = FileUtils.ls(query, client);
		        for(DbxEntry dirent : dirents) {
		        	if(dirent.isFile())
		        	{
		        		DbxEntry.File asfile = dirent.asFile();
		        		System.out.println(asfile.lastModified + "\t"
		        				+ asfile.numBytes + "\t"
		        				+ asfile.path);
		        	}
		        	else
		        	{
		        		DbxEntry.Folder asfolder = dirent.asFolder();
		        		System.out.println(asfolder.path);
		        	}
		        }
	        	break;
	        case "mkdir":
	        	FileUtils.mkdir(args[1], client);
	        	break;
	        case "upload":
	        	File inputFile = new File(args[1]);
	        	String dest = args[2];
	        	if(dest.charAt(0) != '/')
	        		dest = "/" + dest;
	        	DbxEntry.File outfile = FileUtils.upload_file(inputFile, dest, client);
	        	System.out.println("Uploaded " + outfile.numBytes + " bytes to " + outfile.name);
	        	break;
	        }
	        
	        

	        /*
	        FileOutputStream outputStream = new FileOutputStream("magnum-opus.txt");
	        try {
	            DbxEntry.File downloadedFile = client.getFile("/magnum-opus.txt", null,
								  outputStream);
	            System.out.println("Metadata: " + downloadedFile.toString());
	        } finally {
	            outputStream.close();
	        }
	        /**/
	    }
    
    
    // Plugin stuff
    public String plugin_protocol() {
    	return "dbx";
    }
    
    public boolean saveuri(InputStream input, int amount_to_write, URI uri) {
    	DbxClient client;
		try {
			client = Connector.connect(new APIKeyStore("appkey.properties"));
			FileUtils.upload_stream(input, amount_to_write, uri.getPath(), client);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    	return true;
    }
    
    public DbxEntry.Folder mkdir(URI newdir) {
    	DbxClient client;
    	try {
			client = Connector.connect(new APIKeyStore("appkey.properties"));
			return FileUtils.mkdir(newdir.getPath(), client);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
}