package Common.Message;

import java.time.LocalDateTime;

import Common.Web.Package.WebPackage;

public class Message extends WebPackage{
    public LocalDateTime send_time;
    public String sender;

    public Message() {}

    public Message(String sender) {
        send_time = LocalDateTime.now();
        this.sender = sender;
    }
}
