package WebHandler;

import java.io.IOException;

import Common.Message.Message;
import Common.Message.MessageHistory;
import Common.Web.WebConnection;
import Common.Web.Package.*;
import View.ChatRoomFrm;

public class ChatRoomWebHandlerThread extends Thread {
    WebConnection connection;
    ChatRoomFrm chatRoomFrm;

    public ChatRoomWebHandlerThread(ChatRoomFrm f, WebConnection connection) {
        this.connection = connection;
        this.chatRoomFrm = f;
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

            if (webPackage instanceof MessageHistory) {
                MessageHistory m = (MessageHistory)webPackage;
                chatRoomFrm.receiveMessageHistory(m);
            }
            else if (webPackage instanceof Message) {
                Message m = (Message)webPackage;
                chatRoomFrm.receiveMessage(m);
            }
        }
    }

    public void syncWithServer() {
        try {
            connection.send(new MessageHistoryRequest());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message m) {
        try {
            connection.send(m);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}   
