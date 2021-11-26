package http.server;

/**
 * Cette classe permet la gestion d'un requête dans son intégralitée
 *@author MAHMOUDI Brahim, DAKHIL El Yazid
 * @version 1.0
 */
public class Request {

    private String method;
    private String url;
    private String httpVersion;
    private String body;


    /**
     * Objet requête que l'on recupère lors d'une requête http
     * @param message: requête envoyée sous forme de String( chaîne de caractères)
     */
    public Request(String message){
        System.out.println("message dans Request"+message);
        String[] split = message.split(" ");
        method = split [0];
        url = split[1];
        httpVersion = split[2];
        body = null;
    }


    /**
     * Renvoie la méthode http , GET/POST/HEAD/PUT...
     * @return: la méthode
     */
    public String getMethod() {
        return method;
    }

    /**
     * Renvoie l'url de la resource à modifier/ajouter/supprimer
     * @return: l'url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Renvoie la version, en générale HTTP/1.1
     * @return: la version
     */
    public String getHttpVersion() {
        return httpVersion;
    }


}
