import java.io.IOException;
import java.util.Map;

/**
 * An example of subclassing NanoHTTPD to make a custom HTTP server.
 */
public class server extends NanoHTTPD {
    public server() {
        super(8789);
    }

    @Override public Response serve(IHTTPSession session) {
        Method method = session.getMethod();
        String uri = session.getUri();
        System.out.println(method + " '" + uri + "' ");

        String msg = "<html><body><h1>Hello server</h1>\n";
        Map<String, String> parms = session.getParms();
        if (parms.get("username") == null)
            msg +=
                    "<form action='?' method='get'>\n" +
                            "  <p>Your name: <input type='text' name='username'></p>\n" +
                            "</form>\n";
        else
            msg += "<p>Hello, " + parms.get("username") + "!</p>";

        msg += "</body></html>\n";
if(uri.equals("/shubham")){
        	
        	

            return new NanoHTTPD.Response("hello shubham");
        }

        return new NanoHTTPD.Response("hello");
        
    }


    public static void main(String[] args) {
        ServerRunner.run(server.class);
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