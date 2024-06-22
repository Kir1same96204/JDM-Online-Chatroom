package Common.Web;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

/**
 * @Description
 * A basic class to handle Object transmission between Server and Client. 
 * All communication between Server and Client should go through this class.
 */
public class WebConnection {
    private String IP = "127.0.0.1";
    private int port = 6666;
    private Socket socket;

    public WebConnection() throws IOException{
        socket = new Socket(InetAddress.getByName(IP), port);
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
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(obj);
    }

    public Object receive() throws IOException, ClassNotFoundException{
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        return ois.readObject();
    }
}