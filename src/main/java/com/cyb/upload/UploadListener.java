package com.cyb.upload;

import org.apache.commons.fileupload.ProgressListener;

public class UploadListener implements ProgressListener {
	
	private UploadStatus status;  
	
	public UploadListener(UploadStatus status) {
		super();
		this.status = status;
	}

	public void update(long bytesRead, long contentLength, int items) {
		status.setBytesRead(bytesRead);
		status.setContentLength(contentLength);
		status.setItems(items);
	}

}
