package at.hassmann.server.objects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * proceeds request
 */
public class Request {
    private final PrintStream outStream;
    private final String command;
    private final String url;
    private final String input;
    private final String authUserString;

    /*
     * gets infos out of request
     * @param socket: Socket from which the request gets
     */
    public Request(Socket socket) throws IOException {
        StringBuilder rqBuilder = new StringBuilder();
        this.outStream = new PrintStream(socket.getOutputStream());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String line = bufferedReader.readLine();
        while (!line.isBlank()) {
            rqBuilder.append(line).append("\r\n");
            line = bufferedReader.readLine();
        }
        String request = rqBuilder.toString();
        String[] requestsLines = request.split("\r\n");
        String[] requestLine = requestsLines[0].split(" ");
        //System.out.println(requestLine[0]);  //zb POST
        //System.out.println(requestLine[1]);   //zb /sessions
        //System.out.println(requestLine[2]);   //zb HTTP/1.1
        String method = requestLine[0];
        String path = requestLine[1];
        String version = requestLine[2];
        String host = requestsLines[1].split(" ")[1];

        //read the post input data
        StringBuilder input = new StringBuilder();
        while(bufferedReader.ready()){
            input.append((char) bufferedReader.read());
        }
        this.input = input.toString();
        this.url = path;
        this.command = method;

        List<String> headers = new ArrayList<>(Arrays.asList(requestsLines).subList(2, requestsLines.length));
        //System.out.println("accessLog:");
        //String accessLog = String.format("Client %s, method %s, path %s, version %s, host %s, headers %s", socket, method, path, version, host, headers);
        //System.out.println(accessLog);
        String auth = "Authorization: Basic";
        if(headers.toString().contains(auth)) {
            int authIndex = headers.toString().indexOf(auth);
            String authUserString = headers.toString().substring(authIndex + 21);   //extract username
            int authIndexEnd = authUserString.indexOf('-');
            if(authIndexEnd == -1){
                authIndexEnd = authUserString.indexOf(']');
            }
            authUserString = authUserString.substring(0,authIndexEnd);
            this.authUserString = authUserString;
        }else{
            this.authUserString = null;
        }
    }

    public String getAuthUserString() {
        return authUserString;
    }

    /**
     * @return out PrintStream : Output
     */
    public PrintStream getOutStream() {
        return this.outStream;
    }

    /**
     * Command as GET, PUT, POST, DEL
     * @return command as String
     */
    public String getCmd() {
        return this.command;
    }

    /**
     * Request url
     * @return url as String
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * Input des Request
     * @return Input as String
     */
    public String getInput() {
        return this.input;
    }
}
