import java.io.IOException;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

import Common.User;
import Common.Web.*;
import Common.Web.Package.*;

public class App {
    static final int PORT = 6666;
    private static ConcurrentHashMap<String, ConnectionHandlerThread> connections = new ConcurrentHashMap<>();
    static ServerSocket serverSocket = null;
    static App server = null;
    
    private App() {
        // do static initialization here
        ConnectionHandlerThread.mainApp = this;
        if (App.serverSocket == null) {
            try {
                App.serverSocket = new ServerSocket(PORT);
            } catch (IOException e) {
                System.err.println("Server Socket build failed!");
                e.printStackTrace();
                return;
            }
        }
        
        try {
            mainLoop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void mainLoop() throws Exception {
        // main loop to monitor port, start new thread to handle new connection 
        
        while (true){
            Socket socket = serverSocket.accept();
            WebConnection webConnection = new WebConnection(socket);
            //------test-----
            // WebPackage webPackage = (WebPackage)webConnection.receive();
            // // if (webPackage instanceof SignupRequest) {
            // LoginResult result = new LoginResult();
            // result.success = true;
            // webConnection.send(result);

            //-----end of test------
            /*
             *TODO: 在本地根据用户信息库进行相应的操作（成功登陆、失败返回报错信息）
             * 将User传给ConnectionHandleThread
             */
            WebPackage webPackage = null;
            try {
                webPackage = (WebPackage)webConnection.receive();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            User user = null;
            if (webPackage instanceof LoginRequest) {
                //TODO
            }
            else if (webPackage instanceof SignupRequest) {
                //TODO
            }
            //----test------
            user = new User();
            user.name = "test";
            //-----end of test-------
            ConnectionHandlerThread connectionHandler = new ConnectionHandlerThread(webConnection, user);
            connections.put(user.name, connectionHandler);
            connectionHandler.start();
        }
    }

    public static void main(String[] args) {
        System.out.println("Server starting...");

        server = new App();
    }

    public void ThreadExit(String user_name) {
        connections.remove(user_name);
    }
}
