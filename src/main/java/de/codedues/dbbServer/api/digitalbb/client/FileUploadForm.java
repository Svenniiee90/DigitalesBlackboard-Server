package de.codedues.dbbServer.api.digitalbb.client;

import javax.ws.rs.FormParam;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class FileUploadForm {
	
	private byte[] fileData;
	private String fileName;
	
	public byte[] getFileData() {
		return fileData;
	}
	@FormParam("selectedFile")
	@PartType("application/octet-stream")
	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}
	public String getFileName() {
		return fileName;
	}
	@FormParam("fileName")
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	

}
