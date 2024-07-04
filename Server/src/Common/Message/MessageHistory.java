package Common.Message;

import java.util.ArrayList;

import Common.Web.Package.WebPackage;

public class MessageHistory extends WebPackage {
    public ArrayList<Message> messages;
    
    public MessageHistory(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
