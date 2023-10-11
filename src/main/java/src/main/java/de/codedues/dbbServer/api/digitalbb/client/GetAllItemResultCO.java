package src.main.java.de.codedues.dbbServer.api.digitalbb.client;

import java.util.List;

public class GetAllItemResultCO {

	private String state;
	
	private List<BBItemCO> resultList;
	
	private String exception;

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public List<BBItemCO> getResultList() {
		return resultList;
	}

	public void setResultList(List<BBItemCO> resultList) {
		this.resultList = resultList;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}
	
	
	
	

}
