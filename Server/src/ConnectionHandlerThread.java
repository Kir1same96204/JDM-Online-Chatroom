import Common.ClientState;
import Common.Message.Message;
import Common.User;
import Common.Web.Package.*;
import static Common.Web.Package.ChatRoomInfoUpdate.UpdateType.ADD;
import Common.Web.WebConnection;

public class ConnectionHandlerThread extends Thread { 
    WebConnection webConnection = null;
    private User user = null;
    static public App mainApp = null;
    private int clientState;
    public String current_room="";
    public ConnectionHandlerThread(WebConnection webConnection, User user) {
        this.webConnection = webConnection;
        this.user = user;

        clientState = ClientState.IN_LOBBY;
    }
    /**
     * @description connection closed
     */
    private void Exit1() {
        ConnectionHandlerThread.mainApp.ThreadExit(user.name);
    }
    public void send(WebPackage w){
        try {
            webConnection.send(w);
        } catch (Exception e) {
        }
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
            if (clientState == ClientState.IN_LOBBY) {
                if (webPackage instanceof NewChatRoomRequest) {
                    NewChatRoomRequest ncr=(NewChatRoomRequest)webPackage;
                    mainApp.rmm.addroom(ncr.name);
                    ChatRoomInfoUpdate criu=new ChatRoomInfoUpdate();
                    criu.type=ADD;
                    criu.info=mainApp.rmm.getroominfo(ncr.name);
                    mainApp.msgall(criu,null); 
                }
                else if(webPackage instanceof EnterChatRoomRequest){
                    EnterChatRoomRequest ecr=(EnterChatRoomRequest)webPackage;
                    boolean exist=mainApp.rmm.exists(ecr.name);
                    EnterChatRoomResult ecrr=new EnterChatRoomResult();
                    if(!exist){
                        ecrr.success=false;
                    }
                    else{
                        current_room=ecr.name;
                        ecrr.success=true;
                        ecrr.info=mainApp.rmm.getroominfo(ecr.name);
                        mainApp.rmm.addroom(ecr.name);
                        ChatRoomInfoUpdate criu=new ChatRoomInfoUpdate();
                        criu.type=ADD;
                        criu.info=ecrr.info;
                        mainApp.msgall(criu, ecr.name);
                        clientState=ClientState.IN_ROOM;
                        mainApp.rmm.changenum(ecr.name, 1);
                    }
                    try {
                        webConnection.send(ecrr);
                    } catch (Exception e) {
                    }
                }
            }
            else if (clientState == ClientState.IN_ROOM) { 
                if (webPackage instanceof Message) {
                    Message mes=(Message)webPackage;
                    String str=user.name;
                    mainApp.rmm.addmsg(current_room, mes);
                    try {
                        mainApp.msgall(webPackage, current_room);
                        mainApp.savedata();
                    } catch (Exception e) {
                    }
                }
/*                     else if(webPackage instanceof EnterChatRoomRequest){//quit
                    EnterChatRoomRequest ecr=(EnterChatRoomRequest)webPackage;
                    int pos=mainApp.rmm.findroom(current_room);
                    mainApp.rmm.addusernum(current_room,-1); 
                    ChatRoomInfoUpdate criu=new ChatRoomInfoUpdate();
                    criu.info=mainApp.rmm.cri[pos];
                    current_room="";
                    
                    mainApp.msgall(criu, ecr.name);
                    
                } */
                else{//history
                    try {
                        webConnection.send(mainApp.rmm.getmessagehistory(current_room));
                    } catch (Exception e) {

                    }
                }
            }
        }
    }
}
