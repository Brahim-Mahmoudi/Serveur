package http.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
/**
 * Cette classe permet la gestion d'un reponse dans son intégralitée
 *@author MAHMOUDI Brahim, DAKHIL El Yazid
 * @version 1.0
 */
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

    /**
     * Constructeur vide de la classe
     */
    public Response() {
    }

    /**
     * Méthode qui permet de gérer l'envoie d'une réponse après la réception d'un requête. Cette réponse contient le status, utile pour savoir l'état de la transaction( echec ou succes ).
     * De plus, cela permet la gestion des propriétés comme content lenght, server, content type....
     * @param out: la socket d'envoie
     * @throws IOException: exception en cas de soucis avec le printwriter
     */
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

    /**
     * Renvoie le status de la réponse ( échec ou succes ).
     * @param status: peut être différent en fonction de l'état: 200,201,400,404...
     */
    public void setStatus(String status) {
        this.status = status;
    }


    /**
     * Renvoie le type de la ressource à traiter
     * @param contentType: type de contenu
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Renvoie le server
     * @param server:le serveur
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * Permet l'ajout d'un body de type string dans la stucture de donnée qui gère le body de lo réponse
     * @param body: le body sous forme de string
     */
    public void addToBody(String body) {
        this.body = body.getBytes();
    }

    /**
     * Permet l'ajout d'un body de type autre que string, pour gérer par exemple les fichiers audio ou vidéo...
     * @param body: le tableau de byte à ajouter dans le body
     */
    public void setBody(byte[] body) {
        this.body = body;
    }

    /**
     * Permet de mettre en place la version HTTP dans la réponse, en générale HTTP/1.1
     * @param httpVersion: la verison à mettre
     */
    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    /**
     * Permet l'ajout de la taille du body si il y en a
     * @param contentLength: la taille du contenu
     */
    public void setContentLength(String contentLength) {
        this.contentLength = contentLength;
    }

}
