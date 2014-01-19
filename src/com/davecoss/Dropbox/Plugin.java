package com.davecoss.Dropbox;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JDialog;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWriteMode;

import com.davecoss.java.GUIUtils;
import com.davecoss.java.plugin.PluginException;
import com.davecoss.java.plugin.PluginInitException;

public class Plugin implements com.davecoss.java.plugin.StoragePlugin {

	private DbxClient client = null;
	private File jarfile = null;
	
	public Plugin() {
		client = null;
	}

	@Override
	public void init(Console console) throws PluginInitException {
		throw new UnsupportedOperationException("Console has not been implemented for Dropbox Plugin. Use PrintStream/InputStream init.");
	}

        @Override
        public void init(PrintStream output, InputStream input) throws PluginInitException {
		try {
			File keystorefile = new File("appkey.properties");
			if(!keystorefile.exists())
			{
				output.print("API Key File: ");
				BufferedReader reader = new BufferedReader(new InputStreamReader(input));
				keystorefile = new File(reader.readLine());
			}
			APIKeyStore keystore = new APIKeyStore(keystorefile);
		    client = Connector.connect(keystore, output, input);
		} catch (Exception e) {
			throw new PluginInitException("Error creating Dropbox Client", e);
		}
	}

        @Override
        public void init(JDialog parent) throws PluginInitException {
		try {
			File keystorefile = new File("appkey.properties");
			if(!keystorefile.exists())
			{
				keystorefile = GUIUtils.select_file(parent);
			}
			client = Connector.connect(new APIKeyStore("appkey.properties"), parent);
		} catch (Exception e) {
			throw new PluginInitException("Error creating Dropbox Client", e);
		}
	}


	@Override
	public void destroy() throws PluginException  {
		if(client != null)
			client = null;
	}
	
	@Override
    public URI mkdir(String path) {
	    try {
	    	DbxEntry.Folder retval = FileUtils.mkdir(clean_path(path), client);
			return new URI("dbx:" + retval.path);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }

	@Override
	public File get_jarfile() {
		return jarfile;
	}
	
	@Override
	public File set_jarfile(File jarfile) {
		return (this.jarfile = jarfile);
	}

	@Override
	public String get_protocol() {
		return "dbx";
	}

	@Override
	public boolean isFile(URI uri) {
	    try {
		return client.getMetadata(clean_path(uri)).isFile();
	    } catch(DbxException de) {
		return false;
	    }
	}

	@Override
	public boolean exists(URI uri) {
	    try {
	    	return client.getMetadata(clean_path(uri)) != null;
	    } catch(DbxException de) {
		return false;
	    }
	}

	@Override
	public URI[] listFiles(URI uri) {
	    DbxEntry.WithChildren dirents = null;
	    try {
		dirents = client.getMetadataWithChildren(clean_path(uri));
	    } catch(DbxException de) {
		return null;
	    }

	    if(dirents == null || dirents.children.size() == 0)
		return new URI[]{};
	    
	    URI[] retval = new URI[dirents.children.size()];
	    Iterator<DbxEntry> dirent = dirents.children.iterator();
	    int idx = 0;
	    while(dirent.hasNext()) {
		DbxEntry entry = dirent.next();
		try {
		    retval[idx++] = new URI("dbx:" + entry.path);
		} catch(URISyntaxException urise) {
		    return null;
		}
	    }
	    return retval;
	}

	@Override
	public URI saveStream(InputStream input, int amount_to_write,
			URI destination) throws PluginException {
		try {
			DbxEntry.File retval = FileUtils.upload_stream(input, amount_to_write, clean_path(destination.getPath()), client);
			return new URI("dbx:" + retval.path);
    	} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public InputStream getInputStream(URI uri) throws PluginException {
		try {
			DbxClient.Downloader downloader = client.startGetFile(
					clean_path(uri), null);
			return new DropboxInputStream(downloader);
		} catch (DbxException de) {
			throw new PluginException("Error starting download.", de);
		}
	}

	@Override
	public OutputStream getOutputStream(URI uri) throws PluginException {
	    DbxClient.Uploader uploader = client.startUploadFileChunked(uri.getPath(), DbxWriteMode.force(), -1);
	    return new DropboxOutputStream(uploader);
	}
	
	public String clean_path(URI uri) {
		return clean_path(uri.getPath());
	}
	
	public String clean_path(String path) {
		if(path.charAt(path.length()-1) == '/')
    		path = path.substring(0,path.length()-1);
		return path;
	}

	
}