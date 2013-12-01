package com.davecoss.Dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class APIKeyStore {

	public String APP_KEY = null;
	public String APP_SECRET = null;
	
	APIKeyStore(String keyfile) throws IOException {
		Properties configfile = new Properties();
		InputStream keystream = new FileInputStream(keyfile);
		configfile.load(keystream);
		
		APP_KEY = configfile.getProperty("key");
		APP_SECRET = configfile.getProperty("secret");
	}
	
	APIKeyStore(File keyfile) throws IOException {
		Properties configfile = new Properties();
		InputStream keystream = new FileInputStream(keyfile);
		configfile.load(keystream);
		
		APP_KEY = configfile.getProperty("key");
		APP_SECRET = configfile.getProperty("secret");
	}
}
