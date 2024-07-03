package WebHandler;

import java.io.IOException;

import Common.User;
import Common.Web.WebConnection;
import Common.Web.Package.*;
import View.*;

public class LobbyWebHandlerThread extends Thread {
    WebConnection connection;
    MainFrm mainFrm;
    User user;

    public LobbyWebHandlerThread(MainFrm mainFrm, WebConnection webConnection, User user) {
        this.mainFrm = mainFrm;
        connection = webConnection;
        this.user = user;
    }

    public void run() {
        while (true) {
            WebPackage webPackage = null;
            try {
                webPackage = connection.receive();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            if (webPackage instanceof EnterChatRoomResult) {
                mainFrm.dispose();
                EnterChatRoomResult result = (EnterChatRoomResult)webPackage;
                ChatRoomFrm room = new ChatRoomFrm(connection, user, result.info);
                room.setVisible(true);
                return;
            }

            if (webPackage instanceof ChatRoomInfoUpdate) {
                ChatRoomInfoUpdate update = (ChatRoomInfoUpdate)webPackage;
                
                switch (update.type) {
                    case ChatRoomInfoUpdate.UpdateType.ADD:
                        mainFrm.InformNewChatroom(update.info);
                        break;
                
                    case ChatRoomInfoUpdate.UpdateType.MIN:
                        mainFrm.InformDelChatroom(update.info.name);
                        break;
                }
            }
        }
    }

    public synchronized void CreateChatRoom(String name) {
        try {
            connection.send(new NewChatRoomRequest(name));
            EnterChatRoom(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void EnterChatRoom(String name) {
        try {
            connection.send(new EnterChatRoomRequest(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
