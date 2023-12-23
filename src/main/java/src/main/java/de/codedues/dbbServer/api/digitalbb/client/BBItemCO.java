package src.main.java.de.codedues.dbbServer.api.digitalbb.client;

import java.time.LocalDateTime;

public class BBItemCO {
	
	private int id;
	private String title;
	private String msg;
	private String autor;
	private byte[] img;
	private boolean imagePost;
	private LocalDateTime cdate;
	private LocalDateTime start;
	private LocalDateTime end;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getAutor() {
		return autor;
	}
	public void setAutor(String autor) {
		this.autor = autor;
	}
	
	public byte[] getImg() {
		return img;
	}
	public void setImg(byte[] img) {
		this.img = img;
	}
	public boolean isImagePost() {
		return imagePost;
	}
	public void setImagePost(boolean imagePost) {
		this.imagePost = imagePost;
	}
	public LocalDateTime getCdate() {
		return cdate;
	}
	public void setCdate(LocalDateTime cdate) {
		this.cdate = cdate;
	}
	public LocalDateTime getStart() {
		return start;
	}
	public void setStart(LocalDateTime start) {
		this.start = start;
	}
	public LocalDateTime getEnd() {
		return end;
	}
	public void setEnd(LocalDateTime end) {
		this.end = end;
	}
	
	
	
 
}
