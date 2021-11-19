package http.server;

public class Request {

    private String method;
    private String url;
    private String httpVersion;

    public Request(String message){
        String[] split = message.split(" ");
        method = split [0];
        url = split[1];
        httpVersion = split[2];
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
}
