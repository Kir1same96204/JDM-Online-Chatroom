package Common.Message;

import java.time.LocalDateTime;

import Common.Web.Package.WebPackage;

public class Message extends WebPackage{
    public LocalDateTime send_time;
    public String sender;

    public Message() {
        send_time = LocalDateTime.now();
    }

    public Message(String sender) {
        this.sender = sender;
    }
}
 