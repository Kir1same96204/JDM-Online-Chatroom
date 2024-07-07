package Common.Message;

import Common.Web.Package.WebPackage;
import java.util.ArrayList;

public class MessageHistory extends WebPackage {
    public ArrayList<Message> messages;
    public MessageHistory(){
        messages=new ArrayList<Message>();
    }
    public MessageHistory(ArrayList<Message> messages) {
        this.messages = messages;
    }
    public void addmessage(Message msg){
        messages.add(msg);
    }
    
}
