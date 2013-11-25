package com.davecoss.Dropbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Locale;

import com.davecoss.java.BuildInfo;
import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;

public class Connector {
	
    public static DbxClient connect(APIKeyStore apikey) throws IOException, DbxException {
	return connect(apikey, System.out, System.in);
    }

    public static DbxClient connect(APIKeyStore apikey, PrintStream consoleStream, InputStream inputStream) throws IOException, DbxException {
		final String APP_KEY = apikey.APP_KEY;
	    final String APP_SECRET = apikey.APP_SECRET;
	    
	    BuildInfo info = new BuildInfo(Connector.class);
	
	    DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
	
	    DbxRequestConfig config = new DbxRequestConfig("DropboxConnector/" + info.get_version(),
						       Locale.getDefault().toString());
	    DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);
	
	    // Have the user sign in and authorize your app.
	    String authorizeUrl = webAuth.start();
	    consoleStream.println("1. Go to: " + authorizeUrl);
	    consoleStream.println("2. Click \"Allow\" (you might have to log in first)");
	    consoleStream.println("3. Copy the authorization code.");
	    String code = new BufferedReader(new InputStreamReader(inputStream)).readLine().trim();
	
	    // This will fail if the user enters an invalid authorization code.
	    DbxAuthFinish authFinish = webAuth.finish(code);
	
	    return new DbxClient(config, authFinish.accessToken);

	}
}
