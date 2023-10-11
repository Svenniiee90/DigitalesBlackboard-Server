package de.codedues.dbbServer.api.digitalbb.boundary;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartOutput;
import org.jboss.resteasy.plugins.providers.multipart.OutputPart;

import de.codedues.dbbServer.api.digitalbb.client.FileUploadForm;
import de.codedues.dbbServer.api.digitalbb.client.GetFileRequest;
import de.codedues.dbbServer.api.digitalbb.client.StoreImageResult;
import de.codedues.dbbServer.core.AppManager;
import src.main.java.de.codedues.dbbServer.api.digitalbb.client.AddItemRequestCO;
import src.main.java.de.codedues.dbbServer.api.digitalbb.client.AddItemResultCO;
import src.main.java.de.codedues.dbbServer.api.digitalbb.client.BBItemCO;
import src.main.java.de.codedues.dbbServer.api.digitalbb.client.GetAllItemRequestCO;
import src.main.java.de.codedues.dbbServer.api.digitalbb.client.GetAllItemResultCO;

@Path("files")
@Stateless

public class DigitalbbREST {
	
	@Resource(mappedName = AppManager.DBB_DS)
	    private DataSource dbb_DS;
	
	@POST
	@Path("addItem")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
		public AddItemResultCO addItem(AddItemRequestCO requestCO) {
		AddItemResultCO resultCO = new AddItemResultCO();
		String sql = "insert into `item` (id, cdate, start, end, title, msg, author, img) values (?, CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, ?)";
		String sql2 = "select * from `item` where id = ?";
		int rows = 0;
		int id = 0; 
		BBItemCO createItem = requestCO.getCreateItem();
		try (Connection con = dbb_DS.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			id = getNewItemID();
			int index = 1; 
			ps.setInt(index++, id);
			ps.setTimestamp(index++, Timestamp.valueOf(createItem.getStart()));
			ps.setTimestamp(index++, Timestamp.valueOf(createItem.getEnd()));
			ps.setString(index++, createItem.getTitle());
			ps.setString(index++, createItem.getMsg());
			ps.setString(index++, createItem.getAutor());
			ps.setBoolean(index++, createItem.isImagePost());
			rows = ps.executeUpdate();			
		} catch (SQLException e) {
			resultCO.setState("Error");
			resultCO.setError(e.getMessage());
		}
		if (rows > 0) {
			try (Connection con = dbb_DS.getConnection(); PreparedStatement ps = con.prepareStatement(sql2)) {
				ps.setInt(1, id);
				ResultSet rs = ps.executeQuery();
				rs.next();
				BBItemCO item = new BBItemCO();
				item.setId(rs.getInt("id"));
				item.setCdate(rs.getTimestamp("cdate").toLocalDateTime());
				item.setTitle(rs.getString("title"));
				item.setImagePost(rs.getBoolean("img"));
				item.setMsg(rs.getString("msg"));
				item.setAutor("autor");
				item.setStart(rs.getTimestamp("start").toLocalDateTime());
				item.setEnd(rs.getTimestamp("end").toLocalDateTime());				
				resultCO.setState("Success");
				resultCO.setItem(item);
				return resultCO;
			} catch (SQLException e) {
				resultCO.setState("Error");
				resultCO.setError(e.getMessage());
			} 
		}
		return null;
	}
	
	
	@POST
	@Path("getAllItems")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
		public GetAllItemResultCO addItem(GetAllItemRequestCO request) {
		GetAllItemResultCO resultCO = new GetAllItemResultCO();
		List<BBItemCO> resultList = new ArrayList<>();
;		String sql = "SELECT * FROM `item`";
		try (Connection con = dbb_DS.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				BBItemCO item = new BBItemCO();
				item.setId(rs.getInt("id"));
				item.setCdate(rs.getTimestamp("cdate").toLocalDateTime());
				item.setTitle(rs.getString("title"));
				item.setImagePost(rs.getBoolean("img"));
				item.setMsg(rs.getString("msg"));
				item.setAutor(rs.getString("author"));
				item.setStart(rs.getTimestamp("start").toLocalDateTime());
				item.setEnd(rs.getTimestamp("end").toLocalDateTime());
				resultList.add(item);				
			}
			resultCO.setState("Success");
			resultCO.setResultList(resultList);
			return resultCO;
		} catch (SQLException e) {
			resultCO.setState("Error");
			resultCO.setException(e.getMessage());
		}
		return null;
	}
	
	
	@POST
	@Path("upload")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	  public StoreImageResult uploadFile(@MultipartForm FileUploadForm incomingFile) throws IOException {
		StoreImageResult result = new StoreImageResult();
		String name = incomingFile.getFileName().split("\\.")[0];
		String endung = incomingFile.getFileName().split("\\.")[1];
		String completeFilePath = incomingFile.getFileName();
		
		File file = new File(completeFilePath);
		if(!file.exists()) {
			file.createNewFile();
		}
		
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(incomingFile.getFileData());
		fos.flush();
		fos.close();
		
		InputStream uploaded = null;
		
		uploaded = new FileInputStream(file);
		
	    if(uploaded != null) {
	    	String sql = "INSERT INTO `pictures`(name, endung, itemid, data) VALUES(?,?,?,?)";
		    try (Connection con = dbb_DS.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
		  
		    	ps.setString(1, name);
		    	ps.setString(2, endung);
		    	ps.setInt(3, 22323);
		    	ps.setBlob(4, uploaded);
		    	ps.execute();
		    	
		    	
		    } catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	    
	    }
	    result.setState("Successful");
	    file.delete();
	    return result;
	    
	    
	  }
	//
	  @GET
	  @Path("image/{fileName}")
	  @Produces("image/jpeg")
	  public Response getFileInJPEGFormat(@PathParam("fileName") String fileName) 
	  {
		  StringBuilder sql = new StringBuilder("SELECT data, name, endung FROM pictures");
		  	sql.append(" WHERE name = '");
		  	sql.append(fileName+"'");
			InputStream os = null;
			String name = "";
			String end = "";
			try (Connection con = dbb_DS.getConnection(); PreparedStatement ps = con.prepareStatement(sql.toString())) {
				
				ResultSet rs = ps.executeQuery();
				while(rs.next()) {
					os = rs.getBinaryStream("data");
					name = rs.getString("name");
					end = rs.getString("endung");
					
				}
				
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			String filename = name+"."+end;
			File targetFile = new File(filename);
			try(OutputStream outputStream = new FileOutputStream(targetFile)){
			    IOUtils.copy(os, outputStream);
			} catch (FileNotFoundException e) {
			    // handle exception here
			} catch (IOException e) {
			    // handle exception here
			}
			
		    
			
			ResponseBuilder response = Response.ok((Object) targetFile);
			response.header("Content-Disposition", "attachment; filename="+filename);
			
	    return response.build();
	  }
	  
	  private int getNewItemID() throws SQLException {
		  String sql = "SELECT NEXTVAL(itemID)";
		  try (Connection con = dbb_DS.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			  ResultSet rs = ps.executeQuery();
			  rs.next();
			  return rs.getInt(1);
		  }
	  }

}
