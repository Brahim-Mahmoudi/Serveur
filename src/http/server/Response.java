package http.server;

import java.io.OutputStream;
import java.io.PrintStream;

public class Response {

    private String status;
    private String contentType;
    private String server;
    private String body;

    public Response() {
    }

    public void send(PrintStream socketOutStream){

        //Envoie du header
        socketOutStream.println((status));
        socketOutStream.println((contentType));
        socketOutStream.println((server));

        // Le blanc signifie que le header est termine
        socketOutStream.println("");

        //Envoie du contenue du fichier
        socketOutStream.println(body);




    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
