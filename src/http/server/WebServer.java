///A Simple Web Server (WebServer.java)

package http.server;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;


/**
 * Example program from Chapter 1 Programming Spiders, Bots and Aggregators in
 * Java Copyright 2001 by Jeff Heaton
 * 
 * WebServer is a very simple web-server. Any request is responded with a very
 * simple web-page.
 * 
 * @author MAHMOUDI Brahim, DAKHIL El Yazid
 * @version 1.0
 */
public class WebServer {

  /**
   * constructeur du serveur web
   */

  HashMap<String, String> fullRequest;

  /**
   * Permet de démarrer le serveur au lancement de celui ci. Il faut crée une socket  et l'attribuer un port, dans notre cas le port 3000.
   * Cette socket attend les requetes et renvoie des reponses.
   */
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

            case "PUT":
              if (fullRequest.containsKey("body")) {
                doPut(request, response);
                response.setServer("Server: Bot");
                response.send(remote.getOutputStream());
              }
              break;

            case "POST":
              if (fullRequest.containsKey("body")) {
                doPost(request, response);
                response.setServer("Server: Bot");
                response.send(remote.getOutputStream());
              }
              break;


            case "HEAD":
              doHead(request, response);
              response.setServer("Server: Bot");
              response.send(remote.getOutputStream());
              break;

            case "DELETE":
              if (fullRequest.containsKey("body")) {
                response.setStatus(String.valueOf(Response.BAD_REQUEST));
                response.setHttpVersion(request.getHttpVersion());
                response.setContentType("Content-Type: text/html");
                response.setServer("Server: Bot");
                response.addToBody("<html><body><h1>404 - Bad Request</h1></body></html>");
                response.send(remote.getOutputStream());
              }else{
                doDelete(request,response);
                response.setServer("Server: Bot");
                response.send(remote.getOutputStream());
              }

              break;

            default:
              response.setStatus(String.valueOf(Response.BAD_REQUEST));
              response.setHttpVersion(request.getHttpVersion());
              response.setContentType("Content-Type: text/html");
              response.setServer("Server: Bot");
              response.addToBody("<html><body><h1>404 - Bad Request</h1></body></html>");
              response.send(remote.getOutputStream());

          }




        }
        out.flush();
        remote.close();

      } catch (Exception e) {
        System.out.println("Error: " + e);
      }
    }
  }

  /**
   * Démarre l'application
   *
   * @param args ligne de commande au lancement de la méthode principale, pas utilisé ici
   */
  public static void main(String args[]) {
    WebServer ws = new WebServer();
    ws.start();

  }

  /**
   * Cette méthode petmet de gérer la méthode HTTP GET. Cette méthode renvoie dans le body de la réponse, la ressource demandé dans la requête, cette ressource peut être au format souhaité ( vidéo, texte, audio... ).
   * @param request: la requête recue
   * @param response: la réponse à remplir
   */
  public void doGet(Request request, Response response) {
    try {
      response.setHttpVersion(request.getHttpVersion());
      String url = request.getUrl();
      String body = fullRequest.get("body");
      if (body != null){
        throw new IllegalArgumentException();
      }
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
      }
    catch (IllegalArgumentException ex) {
        response.setStatus(String.valueOf(Response.CONFLICT));
        response.setContentType("Content-Type: text/html");
        response.addToBody("<html><body><h1>409 - Conflict </h1>\n<h2>Error :</h2>\n" + ex.toString() + "</body></html>");


    } catch (IOException ioEx) {
      response.setStatus(String.valueOf(Response.INTERNAL_SERVER_ERROR));
      response.setContentType("Content-Type: text/html");
      response.addToBody("<html><body><h1>500 - Internal Server Error</h1>\n<h2>Error :</h2>\n" + ioEx.toString() + "</body></html>");

    }
    catch (Exception ex){
      response.setStatus(String.valueOf(Response.INTERNAL_SERVER_ERROR));
      response.setContentType("Content-Type: text/html");
      response.addToBody("<html><body><h1>500 - Internal Server Error</h1>\n<h2>Error :</h2>\n" + ex.toString() + "</body></html>");
    }
  }
  /**
   * Cette méthode petmet de gérer la méthode HTTP PUT. Cette méthode permet de placer dans un fichier existant ou non le contenu du body de la requête. Cette méthode écrase donc l'ancien contenu si le fichier est existant.
   * @param request: la requête recue
   * @param response: la réponse à remplir
   */
  public void doPut(Request request, Response response) {
    try {
      response.setHttpVersion(request.getHttpVersion());
      String url = request.getUrl();
      String body = fullRequest.get("body");
      File toPost = new File("./src/fichier" + url);
      if (!toPost.exists()) {
        toPost.createNewFile();
      }
      BufferedWriter buffer = new BufferedWriter(new FileWriter(toPost));
      buffer.write("<html><body><h1>PUT </h1>\n<h2>PUT :</h2>\n" + "    ");
      if(body.contains("<") && body.contains(">")) {
        buffer.write(body + "\n</body>\n</html>");
      }else{

        String [] param = null;
        if(body.contains("&")){

          param = body.split("&");
          for (int i = 0; i< param.length; i++){
            String[] val = param[i].split("=");
            String propriete = val[0];
            String valeur = val[1];
            propriete+= ": "+valeur;

            buffer.write("    <p> "+propriete+ "</p>\n");

          }
          buffer.write("</body>\n</html>");

        }else{
          String[] val = body.split("=");
          String propriete = val[0];
          String valeur = val[1];
          propriete+= ": "+valeur;
          buffer.write("<p>"+propriete + "</p> \n</body>\n</html>");
        }
      }
      buffer.close();
      String fileType = Files.probeContentType(toPost.toPath());
      response.setContentType("Content-Type: " + fileType);
      response.addToBody("Content added");
      response.setStatus(String.valueOf(Response.CREATED));
    }

    catch (IOException ioEx) {
      response.setStatus(String.valueOf(Response.INTERNAL_SERVER_ERROR));
      response.setContentType("Content-Type: text/html");
      response.addToBody("<html><body><h1>500 - Internal Server Error</h1>\n<h2>Error :</h2>\n" + ioEx.toString() + "</body></html>");
    }

    catch (Exception ex){
      response.setStatus(String.valueOf(Response.INTERNAL_SERVER_ERROR));
      response.setContentType("Content-Type: text/html");
      response.addToBody("<html><body><h1>500 - Internal Server Error</h1>\n<h2>Error :</h2>\n" + ex.toString() + "</body></html>");
    }

  }
  /**
   * Cette méthode petmet de gérer la méthode HTTP POST. Cette méthode permet de placé dans un fichier existant ou non, le contenu du body de la reqête dans la fin du fichier.
   * @param request: la requête recue
   * @param response: la réponse à remplir
   */
  public void doPost(Request request, Response response) {
    try {
      response.setHttpVersion(request.getHttpVersion());
      String url = request.getUrl();
      String body = fullRequest.get("body");

      File toPost = new File("./src/fichier" + url);
      if (!toPost.exists()) {
        toPost.createNewFile();
      }


      BufferedReader lecteurAvecBuffer = null;
      String ligne;
      String contenuFichier = "";
      lecteurAvecBuffer = new BufferedReader(new FileReader("./src/fichier" + url));
      while ((ligne = lecteurAvecBuffer.readLine()) != null)
      {

        contenuFichier += ligne+"\n";
      }
      lecteurAvecBuffer.close();
      String[] contenuSplit = contenuFichier.split("</body>");


      BufferedWriter buffer = new BufferedWriter(new FileWriter(toPost));


      if(body.contains("<") && body.contains(">")) {

        buffer.write(contenuSplit[0] + "    "+body + "\n</body>"+ contenuSplit[1]);
      }else{

        String [] param = null;
        if(body.contains("&")){
          buffer.write(contenuSplit[0]);
          param = body.split("&");
          for (int i = 0; i< param.length; i++){
            String[] val = param[i].split("=");
            String propriete = val[0];
            String valeur = val[1];
            propriete+= ": "+valeur;

            buffer.write("    <p> "+propriete+ "</p>\n");

          }
          buffer.write("</body>"+ contenuSplit[1]);

        }else{
          String[] val = body.split("=");
          String propriete = val[0];
          String valeur = val[1];
          propriete+= ": "+valeur;
          buffer.write(contenuSplit[0] + "    <p>"+propriete + "</p> \n</body>\n"+ contenuSplit[1]);
        }
      }

      buffer.close();
      String fileType = Files.probeContentType(toPost.toPath());
      response.setContentType("Content-Type: " + fileType);
      response.addToBody("Content added");
      response.setStatus(String.valueOf(Response.OK));
    }

    catch (IOException ioEx) {
      response.setStatus(String.valueOf(Response.INTERNAL_SERVER_ERROR));
      response.setContentType("Content-Type: text/html");
      response.addToBody("<html><body><h1>500 - Internal Server Error</h1>\n<h2>Error :</h2>\n" + ioEx.toString() + "</body></html>");
    }

    catch (Exception ex){
      response.setStatus(String.valueOf(Response.INTERNAL_SERVER_ERROR));
      response.setContentType("Content-Type: text/html");
      response.addToBody("<html><body><h1>500 - Internal Server Error</h1>\n<h2>Error :</h2>\n" + ex.toString() + "</body></html>");
    }

  }
  /**
   * Cette méthode petmet de gérer la méthode HTTP HEAD. Cette méthode demande les en-têtes qui seraient retournés si la ressource spécifiée était demandée avec une méthode HTTP GET
   * @param request: la requête recue
   * @param response: la réponse à remplir
   */
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
      response.addToBody("<html><body><h1>500 - Internal Server Error</h1>\n<h2>Error :</h2>\n" + ioEx.toString() + "</body></html>");
    }
    catch (Exception ex){
      response.setStatus(String.valueOf(Response.INTERNAL_SERVER_ERROR));
      response.setContentType("Content-Type: text/html");
      response.addToBody("<html><body><h1>500 - Internal Server Error</h1>\n<h2>Error :</h2>\n" + ex.toString() + "</body></html>");
    }
  }

  /**
   * Cette méthode petmet de gérer la méthode HTTP DELETE. Cette méthode supprime la ressource indiquée
   * @param request: la requête recue
   * @param response: la réponse à remplir
   */
  public void doDelete(Request request, Response response) {
    try {
      response.setHttpVersion(request.getHttpVersion());
      String url = request.getUrl();
      File toRead = new File("./src/fichier/" + url);
      if(!toRead.exists()){
        throw new FileNotFoundException();
      }else{
        if(toRead.delete()){
          response.setStatus(String.valueOf(Response.OK));
          response.setContentType("Content-Type: text/html");
          response.addToBody("<html><body><h1>File deleted.</h1></body></html>");

        }else{
          throw new Exception();

        }
      }



    } catch (FileNotFoundException ex) {
      response.setContentType("Content-Type: text/html");
      response.addToBody("<html><body><h1>404 - File Not Found</h1>\n<h2>Error :</h2>\n" + ex.toString() + "</body></html>");
      response.setStatus(String.valueOf(Response.FILE_NOT_FOUND));
    }
    catch (Exception ex){
      response.setStatus(String.valueOf(Response.INTERNAL_SERVER_ERROR));
      response.setContentType("Content-Type: text/html");
      response.addToBody("<html><body><h1>500 - Internal Server Error</h1>\n<h2>Error :</h2>\n" + ex.toString() + "</body></html>");
    }
  }
}
