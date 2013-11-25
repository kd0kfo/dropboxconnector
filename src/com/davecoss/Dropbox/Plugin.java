package com.davecoss.Dropbox;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;

import com.davecoss.java.plugin.PluginException;
import com.davecoss.java.plugin.PluginInitException;

public class Plugin implements com.davecoss.java.plugin.StoragePlugin {

	private DbxClient client = null;
	private final Collection<String> functionlist;
	private File jarfile = null;
	
	public Plugin() {
		client = null;
		functionlist = new ArrayList<String>();
		for(String function : new String[]{"protocol_protocol", "saveuri", "mkdir"}) {
			functionlist.add(function);
		}
	}
	
	@Override
	public void init() throws PluginInitException {
		try {
			client = Connector.connect(new APIKeyStore("appkey.properties"));
		} catch (Exception e) {
			throw new PluginInitException("Error creating Dropbox Client", e);
		}
	}

	@Override
	public Collection<String> list_functions() throws PluginException {
		return functionlist;
	}

	@Override
	public void destroy() throws PluginException  {
		if(client != null)
			client = null;
	}
	
	@Override
    public URI mkdir(String path) {
    	try {
			DbxEntry.Folder retval = FileUtils.mkdir(path, client);
			return new URI("dbx:" + retval.path);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }

	@Override
	public boolean has_function(String function_name) throws PluginException {
		return functionlist != null && functionlist.contains(function_name);
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean exists(URI uri) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public URI[] listFiles(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI saveStream(InputStream input, int amount_to_write,
			URI destination) throws PluginException {
		try {
			DbxEntry.File retval = FileUtils.upload_stream(input, amount_to_write, destination.getPath(), client);
			return new URI("dbx:" + retval.path);
    	} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public InputStream readStream(URI uri) throws PluginException {
		throw new PluginException("Not yet implemented: readStream");
	}

	@Override
	public OutputStream getOutputStream(URI uri) throws PluginException {
		throw new PluginException("Not yet implemented: getOutputStream");
	}

}
