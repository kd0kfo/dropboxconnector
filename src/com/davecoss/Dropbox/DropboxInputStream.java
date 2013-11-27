package com.davecoss.Dropbox;

import java.io.InputStream;
import java.io.IOException;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;

public class DropboxInputStream extends InputStream {

    private DbxClient.Downloader downloader = null;

    public DropboxInputStream(DbxClient.Downloader downloader) {
	this.downloader = downloader;
    }

    @Override
    public void close() {
	if(downloader == null)
	    return;
	downloader.close();
    }

    @Override
    public int read() throws IOException {
	if(downloader == null || downloader.body == null)
	    throw new IOException("Missing downloader for Dropbox Client");
	
	return downloader.body.read();
    }

    public DbxEntry.File getMetadata() throws IOException {
	if(downloader == null || downloader.metadata == null)
	    throw new IOException("Missing downloader for Dropbox Client");
	return downloader.metadata;
    }

}