import Common.ClientState;
import Common.User;
import Common.Web.WebConnection;
import Common.Web.Package.*;

public class ConnectionHandlerThread extends Thread {
    WebConnection webConnection = null;
    private User user = null;
    static public App mainApp = null;
    private int clientState;
    
    public ConnectionHandlerThread(WebConnection webConnection, User user) {
        this.webConnection = webConnection;
        this.user = user;

        clientState = ClientState.IN_LOBBY;
    }
    /**
     * @description connection closed
     */
    private void Exit() {
        ConnectionHandlerThread.mainApp.ThreadExit(user.name);
    }

    public void run() {
        while (true) {
            WebPackage webPackage;
            try {
                webPackage = (WebPackage)webConnection.receive();
            } catch (Exception e) { 
                e.printStackTrace();
                return;
            }
            /**
             *  TODO: respond to the webPackege depending on the curent client state
             * Valid webPackage types are different for different current client state
             */
            switch (clientState) {
                case ClientState.IN_LOBBY: {
                    if (webPackage instanceof NewChatRoomRequest) {
                        
                    }
                }

                case ClientState.IN_ROOM: {

                }
            }
        }
    }
}
