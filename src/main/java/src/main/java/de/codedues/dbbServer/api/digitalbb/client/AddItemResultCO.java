package src.main.java.de.codedues.dbbServer.api.digitalbb.client;

public class AddItemResultCO {
	
	private String state; 
	private String error;
	private BBItemCO item;
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public BBItemCO getItem() {
		return item;
	}
	public void setItem(BBItemCO item) {
		this.item = item;
	}
	
	

}
