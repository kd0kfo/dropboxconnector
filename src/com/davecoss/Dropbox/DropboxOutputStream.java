package com.davecoss.Dropbox;

import java.io.OutputStream;
import java.io.IOException;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;

public class DropboxOutputStream extends OutputStream {

    private DbxClient.Uploader uploader = null;

    public DropboxOutputStream(DbxClient.Uploader uploader) {
	this.uploader = uploader;
    }

    @Override
    public void write(int b) throws IOException {
	if(uploader == null || uploader.getBody() == null)
	    throw new IOException("Missing uploader for Dropbox Client");
	uploader.getBody().write(b);
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws IOException {
	if(uploader == null)
	    throw new IOException("Missing uploader for Dropbox Client");
	try {
	    uploader.finish();
	} catch(DbxException de) {
	    throw new IOException("Error closing Upload Connection", de);
	} finally {
	    uploader.close();
	}
    }

    public void abort() {
	if(uploader == null)
	    return;
	uploader.abort();
    }
 
}