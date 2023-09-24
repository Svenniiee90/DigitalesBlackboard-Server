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

@Path("files")
@Stateless

public class DigitalbbREST {
	
	@Resource(mappedName = AppManager.DBB_DS)
	    private DataSource dbb_DS;
	
	@POST
	@Path("upload")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	  public StoreImageResult uploadFile(@MultipartForm FileUploadForm incomingFile) throws IOException {
		StoreImageResult result = new StoreImageResult();
		String fileName = incomingFile.getFileName();
		
		String completeFilePath = fileName;
		
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
	    	String sql = "INSERT INTO `pictures`(name, itemid, data) VALUES(?,?,?)";
		    try (Connection con = dbb_DS.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
		    	
		    	ps.setString(1, fileName);
		    	ps.setInt(2, 22323);
		    	ps.setBlob(3, uploaded);
		    	ps.execute();
		    	
		    	
		    } catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	    
	    }
	    result.setState("Successful");
	    return result;
	    
	    
	  }
	
	  @GET
	  @Path("image/{fileName}")
	  @Produces(MediaType.APPLICATION_OCTET_STREAM)
	  public Response getFileInJPEGFormat(@PathParam("fileName") String fileName) 
	  {
		  StringBuilder sql = new StringBuilder("SELECT data FROM pictures");
		  	sql.append(" WHERE name = '");
		  	sql.append(fileName+"'");
			InputStream os = null;
			try (Connection con = dbb_DS.getConnection(); Statement ps = con.prepareStatement(sql.toString())) {
				
				ResultSet rs = ps.executeQuery(sql.toString());
				while(rs.next()) {
					os = rs.getBinaryStream("data");
				}
				
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			File targetFile = new File("targetFile.png");
			try(OutputStream outputStream = new FileOutputStream(targetFile)){
			    IOUtils.copy(os, outputStream);
			} catch (FileNotFoundException e) {
			    // handle exception here
			} catch (IOException e) {
			    // handle exception here
			}
			
		    
			
			ResponseBuilder response = Response.ok((Object) targetFile);
			response.header("Content-Disposition", "attachment; filename="+fileName+".jpg");
			
	    return response.build();
	  }

}
