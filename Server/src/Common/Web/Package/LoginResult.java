package Common.Web.Package;

import Common.User;

public class LoginResult extends WebPackage{
    public boolean success;
    public User user;
    public ERROR_TYPE error_info;

    public enum ERROR_TYPE{
        NO_USER,
        WRONG_PASSWORD,
    }
}
