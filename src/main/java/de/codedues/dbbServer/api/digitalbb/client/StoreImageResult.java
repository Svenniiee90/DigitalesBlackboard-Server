package de.codedues.dbbServer.api.digitalbb.client;

import java.util.List;

public class StoreImageResult {
	
	private String state;
	private String msg;
	private List<String> exceptions;
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public List<String> getExceptions() {
		return exceptions;
	}
	public void setExceptions(List<String> exceptions) {
		this.exceptions = exceptions;
	}
	
	
	

}
