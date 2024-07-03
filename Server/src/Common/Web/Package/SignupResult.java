package Common.Web.Package;

import java.util.HashSet;

import Common.ChatroomInfo;
import Common.User;


public class SignupResult extends WebPackage{
    public boolean success;
    public User user;
    public ERROR_TYPE error_info;
    public HashSet<ChatroomInfo> chatroom_info;

    public enum ERROR_TYPE{
        USER_EXIST,
    }
}
