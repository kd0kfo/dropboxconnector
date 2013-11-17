package com.davecoss.Dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWriteMode;

public class FileUtils {

	
	public static DbxEntry.File upload_stream(InputStream in, long size, String dest, DbxClient client) throws DbxException, IOException {
		DbxEntry.File outfile = null;
		DbxClient.Uploader uploader = client.startUploadFile(dest, DbxWriteMode.force(), size);
        try {
        	byte[] buffer = new byte[4096];
        	int bytes_read = -1;
        	int total_read = 0;
        	int amount_to_write = 0;
        	while((bytes_read = in.read(buffer, 0, 4096)) != -1)
        	{
        		amount_to_write = bytes_read;
        		if(bytes_read + total_read > size)
        			amount_to_write = total_read - bytes_read;
        		uploader.getBody().write(buffer, 0, amount_to_write);
        		total_read += bytes_read;
        		if(total_read > size)
        			break;
        	}
    		outfile = uploader.finish();
        } finally {
            uploader.close();
        }
        return outfile;
	}
	public static DbxEntry.File upload_file(File src, String dest, DbxClient client) throws DbxException, IOException {
		FileInputStream inputStream = new FileInputStream(src);
        DbxEntry.File outfile = null;
		try {
        	outfile = upload_stream(inputStream, src.length(), dest, client);
        } finally {
        	inputStream.close();
        }
		return outfile;
	}
	
	public static DbxEntry[] ls(String query, DbxClient client) throws DbxException {
		DbxEntry.WithChildren listing = client.getMetadataWithChildren(query);
        if(listing == null) {
        	return new DbxEntry[]{};
        }
		if(listing.entry.isFile()) {
        	return new DbxEntry[]{listing.entry};
        }
		
		DbxEntry[] dirents = new DbxEntry[listing.children.size()];
		int idx = 0;
		for (DbxEntry child : listing.children) {
            dirents[idx++] = child;
        }
		return dirents;
	}
}
