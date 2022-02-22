package at.hassmann.server;

import at.hassmann.server.objects.Request;
import at.hassmann.server.objects.Response;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Basic server logic
 */
public class Main {
    static final int port = 10001;
    private Socket socket;

    public static void main(String[] args) {
        if (!new DBFunctions().initial()) {
            System.err.println("DB init failed");
            System.exit(0);
        }
        System.out.println("\nStarte Server auf Port " + port + "...\n");
        new Main(port);
    }

    /**
     * opens Server Socket und accept it
     * @param port Port on which the server runs
     */
    public Main(int port){
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
            while (true){
                this.socket = serverSocket.accept();
                Thread t1 = new Thread(() -> requestResponding());
                t1.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Every request gets trough this method
     */
    public void requestResponding(){
        try {
            System.out.println("Socket wird gestartet...");
            Request request = new Request(this.socket);
            new Response(request.getUrl(), request.getCmd(), request.getOutStream(), request.getAuthUserString(), request.getInput());
            this.socket.close();
            System.out.println("Socket wird geschlossen!");
        } catch (IOException e){
            System.out.println("Socket error");
            e.printStackTrace();
        }
    }
}
