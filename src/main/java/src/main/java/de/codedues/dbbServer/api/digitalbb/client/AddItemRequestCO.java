package src.main.java.de.codedues.dbbServer.api.digitalbb.client;

public class AddItemRequestCO {
	
	private String user;
	private BBItemCO createItem;
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public BBItemCO getCreateItem() {
		return createItem;
	}
	public void setCreateItem(BBItemCO createItem) {
		this.createItem = createItem;
	}

}
