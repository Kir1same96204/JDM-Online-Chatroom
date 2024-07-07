package Common.Web;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

import Common.Web.Package.WebPackage;

/**
 * @Description
 * A basic class to handle Object transmission between Server and Client. 
 * All communication between Server and Client should go through this class.
 */
public class WebConnection {
    private String IP = "127.0.0.1";
    private int port = 6666;
    private Socket socket;
    ObjectOutputStream oos;
    ObjectInputStream ois;


    public WebConnection() throws IOException{
        socket = new Socket(InetAddress.getByName(IP), port);
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            
        }
    }

    protected void finalize() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public WebConnection(Socket socket) {
        this.socket = socket;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            
        }
    }

    public WebConnection(Socket socket) {
        this.socket = socket;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            
        }
    }

    public Socket getSocket(){
        return socket;
    }

    public void close(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Object obj) throws IOException{
        oos.writeObject(obj);
    }

    public WebPackage receive() throws IOException, ClassNotFoundException{
        return (WebPackage)ois.readObject();
    }
}