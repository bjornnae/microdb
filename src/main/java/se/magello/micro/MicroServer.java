package se.magello.micro;
import java.net.*;
import java.io.*;
import java.util.*;

//import com.fasterxml.jackson.databind.ser.std.MapProperty;

/**
 * The HTTP Server, which is also the executable.
 * Provides an HTTP REST Bridge to the database API.
 */
public class MicroServer {
    private static final String MICROVERSION = "MICRODB.2022-10-07.1";
    /**
     * The filepath to the properties file.
     */
    private static final String PROPERTIESFILE = "micro.properties";

    /**
     * The default server port to fall back on if port instructions are missing elsewhere (in properties file).
     */
    private static final String DEFAULTPORT = "8090";

    private static final String newLine = "\r\n";


    private static Properties mProperties = new Properties();
    static String port = DEFAULTPORT;
    {
        try {
            FileInputStream propstream = new FileInputStream(PROPERTIESFILE);
            mProperties.load(propstream);
            propstream.close();
            String port = mProperties.getProperty("micro.http.port");
        } catch (java.lang.Exception e) {
            System.out.println("Missing a configuration file. Expecting it to be named: " + PROPERTIESFILE + "Using default port value of: "  + DEFAULTPORT );
        }
    }


    static MicroDB db = new MicroDB(new FilePersistenceImpl());

    public static String getPath(String request) {
        String[] parts = request.split("\\s+");
        return parts[1];
    }

    public static String getId(String request){
        String[] parts = getPath(request).split("/");
        //System.out.println("getId: " + parts[1]);
        return parts[parts.length - 2];
    }

    public static Integer getRevision(String request){
        String[] parts = getPath(request).split("/");
        //System.out.println("getRevision: " + parts[2]);
        return Integer.parseInt(parts[parts.length - 1]);
    }

    // Handlers -->
    public static String handleOptions(String request, PrintStream pout) {
        String path = getPath(request);
        System.out.println("Path is: '" + path + "'");
        String response = "{}";
        pout.print(
            "HTTP/1.1 204 No Content" + newLine +
            "Access-Control-Allow-Methods: OPTIONS, GET, POST, PUT, DELETE" + newLine +
            "Access-Control-Allow-Origin: *" + newLine +
            "Access-Control-Allow-Headers: Content-Type" + newLine +
            "Access-Control-Max-Age=604800" + newLine +
            "Date: " + new Date()  + newLine +
            "Server: " + MICROVERSION + newLine +
            newLine + ""
        );
        return response;
        }


    public static String handleGet(String request, PrintStream pout) {
        String path = getPath(request);
        System.out.println("Path is: '" + path + "'");
        String response = "{}";
        if (path.equals("/") ) {
            // case '/' : Let's get all 
            System.out.println("In root path.");
            response = db.getJSONAll();
        } else {
            if (path.length() > 1 && path.endsWith("/")){
                // case '/collectionName/' : Let's get a collection.
                String collectionName = path.substring(1,path.length()-1);
                response = db.getJSONCollection(collectionName);
            } else {
                // case '/an-object-id' ; Let's get a single object
                String objectId = path.substring(1);
                System.out.println("In resource path: getting " + objectId);
                response = db.getJSONObjById(objectId);
            }
        }
        if (response.contains("__NULLOBJECT")){
            pout.print(
                "HTTP/1.0 404 Not Found" + newLine +
                "Access-Control-Allow-Origin: *" + newLine +
                "Content-Type: text/plain" + newLine +
                "Date: " + new Date()  + newLine +
                "Server: " + MICROVERSION + newLine +
                "Content-length: 0" + newLine + newLine +
                "");
    
        } else {
            pout.print(
                "HTTP/1.0 200 OK" + newLine +
                "Access-Control-Allow-Origin: *" + newLine +
                "Content-Type: text/plain" + newLine +
                "Date: " + new Date()  + newLine +
                "Server: " + MICROVERSION + newLine +
                "Content-length: " + response.length() + newLine + newLine +
                response);
        }
        return response;
    }

    public static String handlePost(String request, String body, PrintStream pout) {
        String response;
        String path = getPath(request);
        if (path.length() > 1 && path.endsWith("/")){
            // Capture a collection
            String collectionName = path.substring(1,path.length()-1);
            System.out.println("Handling HTTP Post with collection:" + collectionName);
            response = db.create(collectionName, body);
        } else  { response = db.create(body); }
        
        pout.print(
            "HTTP/1.0 201 OK" + newLine +
            "Access-Control-Allow-Origin: *" + newLine +
            "Content-Type: text/plain" + newLine +
            "Date: " + new Date() + newLine +
            "Server: " + MICROVERSION + newLine +
            "Content-length: " + response.length() + newLine + newLine +
            response);
        return "Response from POST: " + response;
    }

    public static String handlePut(String request, String body, PrintStream pout) {
        String id = getId(request);
        Integer revision = getRevision(request);
        Integer response = db.update(id, revision, body);
        String rStr = Integer.toString(response);
        if (response == -2){
            pout.print(
            "HTTP/1.0 404 Not Found" + newLine +
            "Access-Control-Allow-Methods: GET,POST,PUT,DELETE,OPTIONS" + newLine +
            "Access-Control-Allow-Origin: *" + newLine +
            "Content-Type: text/plain" + newLine +
            "Date: " + new Date() + newLine +
            "Server: " + MICROVERSION + newLine +
            "Content-length: " + rStr.length() + newLine + newLine +
            response);
        } else if (response == -1){
                    String responseString = "Cannot update an old revision when newer version of resource exist.";
                    pout.print(
                    "HTTP/1.0 409 Conflict" + newLine +
                    "Access-Control-Allow-Methods: GET,POST,PUT,DELETE,OPTIONS" + newLine +
                    "Access-Control-Allow-Origin: *" + newLine +
                    "Content-Type: text/plain" + newLine +
                    "Date: " + new Date() + newLine +
                    "Server: " + MICROVERSION + newLine +
                    "Content-length: " + responseString.length() + newLine + newLine +
                    responseString);
        } else {
            pout.print(
                "HTTP/1.0 200 OK" + newLine +
                "Access-Control-Allow-Methods: GET,POST,PUT,DELETE,OPTIONS" + newLine +
                "Access-Control-Allow-Origin: *" + newLine +
                "Content-Type: text/plain" + newLine +
                "Date: " + new Date() + newLine +
                "Server: " + MICROVERSION + newLine +
                "Content-length: " + rStr.length() + newLine + newLine +
                response);
        }
        return "Response from PUT: " + response;
    }

    public static String handleDelete(String request, PrintStream pout) {
        String id = getId(request);
        int revision = getRevision(request);
        Integer response = db.delete(id, revision);
        String rStr = Integer.toString(response);
        pout.print(
            "HTTP/1.0 200 OK" + newLine +
            "Access-Control-Allow-Methods: GET,POST,PUT,DELETE,OPTIONS" + newLine +
            "Access-Control-Allow-Origin: *" + newLine +
            "Content-Type: text/plain" + newLine +
            "Date: " + new Date() + newLine +
            "Server: " + MICROVERSION + newLine +
            "Content-length: " + rStr.length() + newLine + newLine +
                    response);
        return "Response from DELETE: " + response;
    }

    public static void main(String[] args) {
        try {
            ServerSocket socket = new ServerSocket(Integer.parseInt(port));
            System.out.println("Started up new MicroDB Server listening on port " + port);
            while (true) {
                Socket connection = socket.accept();

                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    OutputStream out = new BufferedOutputStream(connection.getOutputStream());
                    PrintStream pout = new PrintStream(out);

                    // read first line of request
                    String request = in.readLine();
                    if (request == null)
                        continue;

                    // the Headers:
                    int contentLength = 0;
                    while (true) {
                        String header = in.readLine();
                        System.out.println("Req Header: " + header);
                        if (header.startsWith("Content-Length")){
                            contentLength = (int)Integer.parseInt(header.substring(15).trim());
                        }
                        if (header == null || header.length() == 0)
                            break;
                    }

                    System.out.println("Going on to capture the BODY...");
                    String body = "";
                    int count = 0;
                    while(count < contentLength){
                        char ch = (char)in.read();
                        body += ch;
                        count++;
                    }
                    System.out.println("BODY:" + body);
                    
                    if (!(request.endsWith(" HTTP/1.0") || request.endsWith(" HTTP/1.1"))) {
                        // bad request
                        pout.print("HTTP/1.0 400 Bad Request" + newLine + newLine);
                    } else {
                        String response = "Unhandled request due to unknown http-verb. Known verbs: PUT, GET, POST, DELETE";
                        boolean requestHasBeenHandled = false;
                        if (request.startsWith("OPTIONS")) {
                            response = handleOptions(request, pout);
                            requestHasBeenHandled = true;
                        }
                        if (request.startsWith("GET")) {
                            response = handleGet(request, pout);
                            requestHasBeenHandled = true;
                        }

                        if (request.startsWith("PUT")) {
                            response = handlePut(request, body, pout);
                            requestHasBeenHandled = true;
                        }

                        if (request.startsWith("POST")) {
                            response = handlePost(request, body, pout);
                            requestHasBeenHandled = true;
                        }

                        if (request.startsWith("DELETE")) {
                            response = handleDelete(request, pout);
                            requestHasBeenHandled = true;
                        }

                        if (!requestHasBeenHandled){
                            System.out.println("WARNING: No matching handler has been run completely.");
                        }

                        System.out.println("Response:" + response);

                        
                    }
                    pout.close();
                } catch (Throwable tri) {
                    System.err.println("Error handling request: " + tri);
                }
            }
        } catch (Throwable tr) {
            System.err.println("Could not start server: " + tr);
        }
    }

}
