package Common.Web.Package;

import java.util.HashSet;

import Common.*;

public class LoginResult extends WebPackage{
    public boolean success;
    public User user;
    public ERROR_TYPE error_info;
    public HashSet<ChatroomInfo> chatroom_info;

    public enum ERROR_TYPE{
        NO_USER,
        WRONG_PASSWORD,
    }
}
