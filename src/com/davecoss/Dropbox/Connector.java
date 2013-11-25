package com.davecoss.Dropbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Locale;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Dimension;

import com.davecoss.java.BuildInfo;
import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;

public class Connector {
	
    public static String get_auth_prompt(String authorizeUrl) {
	return "1. Go to: " + authorizeUrl + "\n" 
	    + "2. Click \"Allow\" (you might have to log in first)\n"
	    + "3. Copy the authorization code.";
    }

    public static DbxClient connect(APIKeyStore apikey, JDialog parent) throws IOException, DbxException {

	BuildInfo info = new BuildInfo(Connector.class);
	
	DbxAppInfo appInfo = new DbxAppInfo(apikey.APP_KEY, apikey.APP_SECRET);
	
	DbxRequestConfig config = new DbxRequestConfig("DropboxConnector/" + info.get_version(),
						       Locale.getDefault().toString());
	return new DbxClient(config, get_authorization(parent, appInfo, config).accessToken);
    }

    public static DbxAuthFinish get_authorization(PrintStream consoleStream, InputStream inputStream, DbxAppInfo appInfo, DbxRequestConfig config) throws DbxException, IOException {
	    DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);
	
	    // Have the user sign in and authorize your app.
	    String authorizeUrl = webAuth.start();

	    consoleStream.println(get_auth_prompt(authorizeUrl));
	    String code = new BufferedReader(new InputStreamReader(inputStream)).readLine().trim();

	    // This will fail if the user enters an invalid authorization code.
	     return webAuth.finish(code);

    }

  public static DbxAuthFinish get_authorization(JDialog parent, DbxAppInfo appInfo, DbxRequestConfig config) throws DbxException {
	    DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);
	
	    // Have the user sign in and authorize your app.
	    String authorizeUrl = webAuth.start();

	    String auth_prompt = get_auth_prompt(authorizeUrl);
	    JTextArea text = new JTextArea(auth_prompt);
	    text.setLineWrap(true);
	    text.setWrapStyleWord(true);
	    JScrollPane scrollpane = new JScrollPane(text);
	    scrollpane.setPreferredSize(new Dimension(200, 200));
	    String code = JOptionPane.showInputDialog(scrollpane);

	    // This will fail if the user enters an invalid authorization code.
	    return webAuth.finish(code);

    }

    public static DbxClient connect(APIKeyStore apikey, PrintStream consoleStream, InputStream inputStream) throws IOException, DbxException {

	BuildInfo info = new BuildInfo(Connector.class);
	
	DbxAppInfo appInfo = new DbxAppInfo(apikey.APP_KEY, apikey.APP_SECRET);
	
	DbxRequestConfig config = new DbxRequestConfig("DropboxConnector/" + info.get_version(),
						       Locale.getDefault().toString());

	return new DbxClient(config, get_authorization(consoleStream, inputStream, appInfo, config).accessToken);
	
    }

}
