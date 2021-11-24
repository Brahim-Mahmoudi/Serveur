package http.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Response {
    public static final int OK = 200;
    public static final int CREATED = 201;
    public static final int BAD_REQUEST = 400;
    public static final int FILE_NOT_FOUND = 404;
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int CONFLICT = 409;



    private String status;
    private String httpVersion;
    private String contentType;
    private String server;
    private String contentLength;
    private byte[] body = null;

    public Response() {
    }

    public void send(OutputStream out) throws IOException {
        //Envoie du header
        PrintWriter socketOutStream = new PrintWriter(out);
        switch(status){
            case "200":
                status += " OK";
                break;
            case "404":
                status += " Not Found";
                break;
            case "500":
                status += " Internal server error";
                break;
            case "400":
                status += " Bad Request";
            break;
            case "201":
                status += " Created";
                break;
            case "409":
                status += " Conflict";
                break;

            default:
                break;

        }
        socketOutStream.println(httpVersion + " "+ status + " ");
        socketOutStream.println((contentType));
        socketOutStream.println((server));
        if(contentLength !=null){
            socketOutStream.println(contentLength);
        }

        // Le blanc signifie que le header est termine
        socketOutStream.println("");
        socketOutStream.flush();

        //Envoie du contenue du fichier
        if(body != null) {
            out.write(body, 0, body.length);
            out.flush();
        }


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

    public void addToBody(String body) {
        this.body = body.getBytes();
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public void setContentLength(String contentLength) {
        this.contentLength = contentLength;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getContentLength() {
        return contentLength;
    }
}
