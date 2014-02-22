import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
public class accounts {
    public static String CONFIG_PATH = Paths.get(accounts.class.getResource(".").getPath()).getParent().toString() +"/config.cfg";
    String  random_key = "";
    static boolean googleAuthentication = false ; 
	static boolean	dropboxAuthentication = false ; 
	
public static class GoogleAccounts {
		 String userid ,emailid ; 

         public	GoogleAccounts(String userid, String emailid){
        	 
        	 this.userid = userid ; 
		     this.emailid = emailid ; 
         }
				        
		
		
	}
	
public static class DropboxAccounts {
	String userid ,access_token ; 

    public	DropboxAccounts(String userid, String emailid){
   	 
   	 this.userid = userid ; 
	 this.access_token = emailid ; 
    }
		
		
	}
public static class Accounts {
	static GoogleAccounts gaccount = new GoogleAccounts(null, null) ; 
	static DropboxAccounts   dbaccount = new DropboxAccounts(null, null) ; 

		    

		  public String getGoogleUserID(){
			  

		        return this.gaccount.userid ; 
		  }
	}
//decode data after retrieving  
public String decode(String s) {
    return StringUtils.newStringUtf8(Base64.decodeBase64(s));
	}
//encode data before saving 
public String encode(String s) {
    return Base64.encodeBase64String(StringUtils.getBytesUtf8(s));
	}
public static JsonNode readAccounts() throws JSONException, IOException{
	
	ObjectMapper mapper = new ObjectMapper();
	 
	try {

		BufferedReader fileReader = new BufferedReader(
			new FileReader(CONFIG_PATH));
		JsonNode rootNode = mapper.readTree(fileReader);

		
		return rootNode ;

		

}catch(Exception e ){e.printStackTrace();}
	return null;  
    
	}
public static void writeAccounts (Accounts obj ) throws IOException,FileNotFoundException{
	
	FileWriter file = new FileWriter(CONFIG_PATH);
	file.write(obj.toString());
	file.flush();
	file.close();
	//write to config file 
	
	}
public void dropboxAuthenticate() throws JSONException, IOException{
	HttpResponse response = null ;
	String db_userid ; 
	if (googleAuthentication){
		db_userid = account_obj.dbaccount.userid ; 
		HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://cloudcv.org/cloudcv/auth/dropbox");
		if ( db_userid != null  ){
			try {

              List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
              
              nameValuePairs.add(new BasicNameValuePair("type", "api"));
              nameValuePairs.add(new BasicNameValuePair("state", random_key));
              nameValuePairs.add(new BasicNameValuePair("userid", account_obj.gaccount.userid ));
              nameValuePairs.add(new BasicNameValuePair("dbuser",db_userid ));
            
              post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
               response = client.execute(post);
              
              
              }catch(IOException e){e.printStackTrace(); }
			
			
			
		}
		else {
			try {

	              List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	              
	              nameValuePairs.add(new BasicNameValuePair("type", "api"));
	              nameValuePairs.add(new BasicNameValuePair("state", random_key));
	              nameValuePairs.add(new BasicNameValuePair("userid", account_obj.gaccount.userid ));
	              nameValuePairs.add(new BasicNameValuePair("dbuser",db_userid ));
	            
	              post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	              response = client.execute(post);
	              
	                 
              }catch(IOException e){e.printStackTrace(); }
		}
		
		//read the response 
        JSONObject mobj = new JSONObject(response.toString());
		Iterator<String> itr = mobj.keys();       // check iterator type 

		while(itr.hasNext())
		{
			String key=itr.next();

			//System.out.println(key);

			if(key.equals("redirect") && mobj.getString("redirect")== "True")
			{
				String url = mobj.getString("url");

		        if(Desktop.isDesktopSupported()){
		            Desktop desktop = Desktop.getDesktop();
		            try {
		                desktop.browse(new URI(url));
		            } catch (IOException | URISyntaxException e) {
		                
		                e.printStackTrace();
		            }
		        }else{
		            Runtime runtime = Runtime.getRuntime();
		            try {
		                runtime.exec("xdg-open " + url);
		            } catch (IOException e) {
		                
		                e.printStackTrace();
		            }
		        } 
			}
			if(key.equals("isValid") && mobj.getString("isValid")== "True")
			{
				//Dropbox authentication successful
				dropboxAuthentication = true ; 
			}
			else{
				authenticate() ;  
	            dropboxAuthenticate() ; 
			}
	}
    
}
	}
public void authenticate() throws JSONException, IOException {
	
	 random_key = UUID.randomUUID().toString();
	 System.out.println(random_key);
	 File f = new File(CONFIG_PATH);
	 HttpClient client = new DefaultHttpClient();
     HttpPost post = new HttpPost("http://cloudcv.org/cloudcv/auth/google");
     HttpResponse response = null;
	 if(f.exists() && !f.isDirectory()){
		 System.out.println("Authentication begins !");
		 JsonNode account_details = readAccounts();
		 String userid = account_details.path("userid").getTextValue();
		 
		 try {

             List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
             
             nameValuePairs.add(new BasicNameValuePair("type", "api"));
             nameValuePairs.add(new BasicNameValuePair("state", random_key));
             nameValuePairs.add(new BasicNameValuePair("userid", userid));
           
             post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
             response = client.execute(post);
        	 System.out.println("response is "+ response.toString());
             //read the response 
            
         
     }catch(IOException e){e.printStackTrace(); }
		 
	 }
	 else {
		 
		 
		 try {
             List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
             
             nameValuePairs.add(new BasicNameValuePair("type", "api"));
             nameValuePairs.add(new BasicNameValuePair("state", random_key));
             post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
              response = client.execute(post);
         	 System.out.println("Account Does not exits !");
        	 System.out.println("response is "+ response);
		 }catch(IOException e){e.printStackTrace(); }
		 
		 
		 
	 }
	 JSONObject mobj = new JSONObject(response);
		Iterator<String> itr = mobj.keys();       // check iterator type 

		while(itr.hasNext())
		{
			String key=itr.next();
			if(key.equals("redirect") && mobj.getString("redirect")== "True")
			{
				//open web browser with url in response  
				String url = mobj.getString("url");

		        if(Desktop.isDesktopSupported()){
		            Desktop desktop = Desktop.getDesktop();
		            try {
		                desktop.browse(new URI(url));
		            } catch (IOException | URISyntaxException e) {
		                
		                e.printStackTrace();
		            }
		        }else{
		            Runtime runtime = Runtime.getRuntime();
		            try {
		                runtime.exec("xdg-open " + url);
		            } catch (IOException e) {
		                
		                e.printStackTrace();
		            }
		        }
			}
			if(key.equals("isValid") && mobj.getString("isValid")== "True")
			{
				//Dropbox authentication successful
				googleAuthentication = true ; 
			}
			
	}
	
		
}
static Accounts account_obj = new Accounts() ; 
public static void main (String... args ) throws JSONException, IOException{
	 accounts test = new accounts() ; 
	 test.authenticate(); 
	
	
}

}

