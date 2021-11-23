///A Simple Web Server (WebServer.java)

package http.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * 
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 * 
 * @author Jeff Heaton
 * @version 1.0
 */
public class WebServer {

  /**
   * WebServer constructor.
   */

  HashMap<String, String> fullRequest;

  protected void start() {
    ServerSocket s;

    System.out.println("Webserver starting up on port 3000");
    System.out.println("(press ctrl-c to exit)");
    try {
      // create the main server socket
      s = new ServerSocket(3000);
    } catch (Exception e) {
      System.out.println("Error: " + e);
      return;
    }

    System.out.println("Waiting for connection");
    while (true) {
      try {
        // wait for a connection
        Socket remote = s.accept();
        // remote is now the connected socket
        System.out.println("Connection, sending data.");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                remote.getInputStream()));
        PrintWriter out = new PrintWriter(remote.getOutputStream());


        // read the data sent. We basically ignore it,
        // stop reading once a blank line is hit. This
        // blank line signals the end of the client HTTP
        // headers.
        fullRequest = new HashMap<>();
        String header = in.readLine();
        String socketLine = ".";

        if (header != null) {
          while (socketLine != null && !socketLine.equals("")) {
            //On cree une instance de l'objet requete qui permet de parse le messageUtilisateur
            socketLine = in.readLine();
            System.out.println(socketLine);
            if (!socketLine.equals("")) {
              String[] split = socketLine.split(": ");
              fullRequest.put(split[0], split[1]);
            }
          }


          if (fullRequest.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(fullRequest.get("Content-Length"));
            if (contentLength != 0) {
              char[] test = new char[contentLength];
              in.read(test, 0, contentLength);
              String toAdd = "";
              for (int i = 0; i < test.length; i++) {
                toAdd += test[i];
              }
              fullRequest.put("body", toAdd);

            }

          }
          for (Map.Entry<String, String> entry : fullRequest.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
          }


          Request request = new Request(header);
          Response response = new Response();
          switch (request.getMethod()) {
            case "GET":
              //On recupere l'URL de la ressource
              doGet(request, response);
              response.setServer("Server: Bot");
              response.send(remote.getOutputStream());
              break;

            case "POST":
              if (fullRequest.containsKey("body")) {
                request.setBody(fullRequest.get("body"));
                doPost(request, response);
                response.setServer("Server: Bot");
                response.send(remote.getOutputStream());
              }
              break;

            case "HEAD":
              doHead(request, response);
              response.setServer("Server: Bot");
              response.send(remote.getOutputStream());

            default:
              response.setStatus(String.valueOf(Response.BAD_REQUEST));
              response.setHttpVersion(request.getHttpVersion());
              response.setContentType("Content-Type: text/html");
              response.setServer("Server: Bot");
              response.addToBody("<html><body><h1>404 - Bad Request</h1></body></html>");
              response.send(remote.getOutputStream());

          }




        }
          /*// Send the response

          // Send the headers
          out.println("HTTP/1.0 200 OK");
          out.println("Content-Type: text/html");
          out.println("Server: Bot");
          // this blank line signals the end of the headers
          out.println("");
          // Send the HTML page
          out.println("<H1>Welcome to the Ultra Mini-WebServer</H2>");*/
        out.flush();
        remote.close();

      } catch (Exception e) {
        System.out.println("Error: " + e);
      }
    }
  }

  /**
   * Start the application.
   *
   * @param args Command line parameters are not used.
   */
  public static void main(String args[]) {
    WebServer ws = new WebServer();
    ws.start();

  }

  public void doGet(Request request, Response response) {
    try {
      response.setHttpVersion(request.getHttpVersion());
      String url = request.getUrl();
      File toRead = new File("./src/fichier/" + url);
      if(!toRead.exists()){
        throw new FileNotFoundException();
      }
      String fileType = Files.probeContentType(toRead.toPath());
      response.setContentType("Content-Type: " + fileType);
      if (fullRequest.containsKey("Content-Length")) {
        response.setContentLength("Content-Length: " + fullRequest.get("Content-Length"));
      }
      //read the file
      byte[] writer = Files.readAllBytes(toRead.toPath());
      response.setBody(writer);
      response.setStatus(String.valueOf(Response.OK));

    } catch (FileNotFoundException ex) {
      response.setStatus(String.valueOf(Response.FILE_NOT_FOUND));
      response.setContentType("Content-Type: text/html");
      response.addToBody("<html><body><h1>404 - File Not Found</h1>\n<h2>Error :</h2>\n" + ex.toString() + "</body></html>");

    } catch (IOException ioEx) {
      response.setStatus(String.valueOf(Response.INTERNAL_SERVER_ERROR));
      response.setContentType("Content-Type: text/html");
      response.addToBody("<html><body><h1>404 - Internal Server Error</h1>\n<h2>Error :</h2>\n" + ioEx.toString() + "</body></html>");

    }
    catch (Exception ex){
      response.setStatus(String.valueOf(Response.INTERNAL_SERVER_ERROR));
      response.setContentType("Content-Type: text/html");
      response.addToBody("<html><body><h1>404 - Internal Server Error</h1>\n<h2>Error :</h2>\n" + ex.toString() + "</body></html>");
    }
  }

  public void doPost(Request request, Response response) {
    try {
      String url = request.getUrl();
      String body = request.getBody();
      File toPost = new File("./src/fichier/" + url);
      if (!toPost.exists()) {
        toPost.createNewFile();
      }
      BufferedWriter buffer = new BufferedWriter(new FileWriter(toPost));
      buffer.write(body);
      buffer.flush();
      buffer.close();
      response.setHttpVersion(request.getHttpVersion());
      String fileType = Files.probeContentType(toPost.toPath());
      response.setContentType("Content-Type: " + fileType);
      response.addToBody("Content added");
    }

    catch (IOException ioEx) {
      response.setStatus(String.valueOf(Response.INTERNAL_SERVER_ERROR));
      response.setContentType("Content-Type: text/html");
      response.addToBody("<html><body><h1>404 - Internal Server Error</h1>\n<h2>Error :</h2>\n" + ioEx.toString() + "</body></html>");
    }

    catch (Exception ex){
      response.setStatus(String.valueOf(Response.INTERNAL_SERVER_ERROR));
      response.setContentType("Content-Type: text/html");
      response.addToBody("<html><body><h1>404 - Internal Server Error</h1>\n<h2>Error :</h2>\n" + ex.toString() + "</body></html>");
    }

  }

  public void doHead(Request request, Response response) {
    try {
      response.setHttpVersion(request.getHttpVersion());
      if (fullRequest.containsKey("Content-Length")) {
        response.setContentLength("Content-Length: " + fullRequest.get("Content-Length"));
      }
      String url = request.getUrl();
      File toRead = new File("./src/fichier/" + url);
      if(!toRead.exists()){
        throw new FileNotFoundException();
      }
      String fileType = Files.probeContentType(toRead.toPath());
      response.setContentType("Content-Type: " + fileType);
      response.setStatus(String.valueOf(Response.OK));
    } catch (FileNotFoundException ex) {
      response.setContentType("Content-Type: text/html");
      response.addToBody("<html><body><h1>404 - File Not Found</h1>\n<h2>Error :</h2>\n" + ex.toString() + "</body></html>");
      response.setStatus(String.valueOf(Response.FILE_NOT_FOUND));
    } catch (IOException ioEx) {
      response.setStatus(String.valueOf(Response.INTERNAL_SERVER_ERROR));
      response.setContentType("Content-Type: text/html");
      response.addToBody("<html><body><h1>404 - Internal Server Error</h1>\n<h2>Error :</h2>\n" + ioEx.toString() + "</body></html>");
    }
    catch (Exception ex){
      response.setStatus(String.valueOf(Response.INTERNAL_SERVER_ERROR));
      response.setContentType("Content-Type: text/html");
      response.addToBody("<html><body><h1>404 - Internal Server Error</h1>\n<h2>Error :</h2>\n" + ex.toString() + "</body></html>");
    }
  }
}
