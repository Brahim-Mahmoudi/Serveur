package http.server;

public class Request {

    private String method;
    private String url;
    private String httpVersion;
    private String body;



    public Request(String message){
        System.out.println("message dans Request"+message);
        String[] split = message.split(" ");
        method = split [0];
        url = split[1];
        httpVersion = split[2];
        body = null;
    }



    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
