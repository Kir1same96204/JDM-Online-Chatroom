package Common.Web.Package;

import Common.ChatroomInfo;

public class ChatRoomInfoUpdate extends WebPackage{
    public enum UpdateType{
        ADD, MIN
    }
    public UpdateType type;
    public ChatroomInfo info;
}
