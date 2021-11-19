///A Simple Web Server (WebServer.java)

package http.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

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
  final public String ROOT_PATH = "../resources";
  /**
   * WebServer constructor.
   */
  protected void start() {
    ServerSocket s;

    System.out.println("Webserver starting up on port 80");
    System.out.println("(press ctrl-c to exit)");
    try {
      // create the main server socket
      s = new ServerSocket(3000);
    } catch (Exception e) {
      System.out.println("Error: " + e);
      return;
    }

    System.out.println("Waiting for connection");
    while(true) {
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
        String messageUtilisateur = ".";

        while (messageUtilisateur != null && !messageUtilisateur.equals(""))
          messageUtilisateur = in.readLine();
        System.out.println("messageUtilisateur : "+messageUtilisateur);

          //On cree une instance de l'objet requete qui permet de parse le messageUtilisateur
          Request request = new Request(messageUtilisateur);
          Response response = new Response();

          switch(request.getMethod()){
            case "GET":
              //On recupere l'URL de la ressource
              request.getUrl();
              doGet(request,response);

              break;

            default:

              response.setStatus("text/html");
              response.setContentType("Content-Type: text/html");
              response.setServer("Server: Bot");
              response.setBody("<html><body><h1>Method Unknown</h1></body></html>");
              break;

        }


        PrintStream outStream = new PrintStream(remote.getOutputStream());
        // Send response to the client
        response.send(outStream);

        // Then close connection
        remote.close();

        // Send the headers
        out.println("HTTP/1.0 200 OK");
        out.println("Content-Type: text/html");
        out.println("Server: Bot");
        // this blank line signals the end of the headers
        out.println("");
        // Send the HTML page
        out.println("<H1>Welcome to the Ultra Mini-WebServer</H2>");
        out.flush();
        remote.close();
      } catch (Exception e) {
        System.out.println("Error: " + e);
      }
    }
  }

  /** HTTP method GET
   * @param request requete du serveur
   * @param response reponse du serveur.
   */
  public void doGet(Request request, Response response) throws FileNotFoundException, IOException {

    // Get file
    File file = new File(ROOT_PATH, request.getUrl()); // We start from bin

    // Get and set file type
    String fileType = Files.probeContentType(file.toPath());
    response.setContentType("Content-Type"+fileType);

    //read the file
    String buf = Files.readString(file.toPath());

    // add to the body
    response.setBody(buf);

  }

  /**
   * Start the application.
   * 
   * @param args
   *            Command line parameters are not used.
   */
  public static void main(String args[]) {
    WebServer ws = new WebServer();
    ws.start();

  }


}
