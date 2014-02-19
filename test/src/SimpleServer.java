import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.JsonNode;
import org.json.JSONException;
import org.json.JSONObject;

public class SimpleServer extends NanoHTTPD {

        public SimpleServer() {

            super(7669);
        }

        @Override
        public Response serve(String uri, Method method, Map<String, String> headers, Map<String, String> parms,
                       Map<String, String> files) throws JSONException  {
        	String[] values = null ; 
        	int i=0 ; 
       	 System.out.println(uri);
      if(!uri.equals("/")){
        	String paramSet = uri.split("\\?")[1];
        	String[] params = paramSet.split("&");
        	for (String param : params)  
            {  
                
                 values[i] = param.split("=")[1];  //confirm that values[0] is code 
                 i++ ;
                
            }}  
        	if(uri.startsWith("/dropbox_callback")) {
            	
            	//Dropbox calling....
            	
            	//send 'POST' to CCV
            	HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost("http://cloudcv.org/cloudcv/callback/dropbox/");

                try {

                  List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                  JsonNode account_details = accounts.readAccounts();
         		 String userid = account_details.path("userid").getTextValue();
                  
                  nameValuePairs.add(new BasicNameValuePair("code", values[0]));
                  nameValuePairs.add(new BasicNameValuePair("state", values[1]));
                  nameValuePairs.add(new BasicNameValuePair("userid", userid )); // dunno what to put as value  
                  

                  post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                  HttpResponse response = client.execute(post);
                  JSONObject mobj = new JSONObject(response.toString());
                  accounts.account_obj = new accounts.Accounts() ;
                  
         
                  accounts.account_obj = new accounts.Accounts() ;
             		
        		 accounts.Accounts.dbaccount = new accounts.DropboxAccounts(mobj.getString("uid") , mobj.getString("token") )  ;             		
                  			
        		  accounts.writeAccounts(accounts.account_obj) ;
        	        accounts.dropboxAuthentication = true	;
                  				
                
                  }catch (IOException | JSONException e ) {
                      e.printStackTrace();
                  } 
            	
                
            } else {
                if (uri.startsWith("/callback")) {
                    //google calling.....
                	//send 'POST' to CCV 
                	HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost("http://cloudcv.org/cloudcv/callback/google/");

                    try {

                      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                      
                      
                      nameValuePairs.add(new BasicNameValuePair("code", ""));
                      nameValuePairs.add(new BasicNameValuePair("state",""));
                    
                      post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                      HttpResponse response = client.execute(post);
                      System.out.println("request sent to the server !");
                      //read the response 
                      JSONObject mobj = new JSONObject(response);
                      accounts.account_obj = new accounts.Accounts() ;
                   		
    		accounts.Accounts.dbaccount = new accounts.DropboxAccounts(mobj.getString("uid") , mobj.getString("token") )  ;             		
              			
    		  accounts.writeAccounts(accounts.account_obj) ;
    	        accounts.googleAuthentication = true	;
              				
    			return new NanoHTTPD.Response(response.toString());
	
              				
              			
                      
                  }catch(IOException e){e.printStackTrace(); }
                   
               }
                
               
                
            }
        	 
        	
			return new NanoHTTPD.Response("heoloo");
        }
        public static void main(String[] args) {
            ServerRunner.run(SimpleServer.class);
        }
        public static class ServerRunner {
            public static void run(Class serverClass) {
                try {
                    executeInstance((NanoHTTPD) serverClass.newInstance());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public static void executeInstance(NanoHTTPD server) {
                try {
                	
                    server.start();
                } catch (IOException ioe) {
                    System.err.println("Couldn't start server:\n" + ioe);
                    System.exit(-1);
                }

                System.out.println("Server started, Hit Enter to stop.\n");

                try {
                    System.in.read();
                } catch (Throwable ignored) {
                }

                server.stop();
                System.out.println("Server stopped.\n");
            }
        }
    }
        
    