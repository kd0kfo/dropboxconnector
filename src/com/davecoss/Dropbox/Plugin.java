package com.davecoss.Dropbox;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;

import com.davecoss.java.plugin.PluginException;
import com.davecoss.java.plugin.PluginInitException;

public class Plugin implements com.davecoss.java.plugin.Plugin {

	private DbxClient client = null;
	private final Collection<String> functionlist;
	
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
	
	// Plugin stuff
    public String plugin_protocol() {
    	return "dbx";
    }
    
    public boolean saveuri(InputStream input, int amount_to_write, URI uri) {
    	try {
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

	@Override
	public boolean has_function(String function_name) throws PluginException {
		return functionlist != null && functionlist.contains(function_name);
	}

}
