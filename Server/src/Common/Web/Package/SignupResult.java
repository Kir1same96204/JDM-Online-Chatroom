package Common.Web.Package;

import Common.User;


public class SignupResult extends WebPackage{
    public boolean success;
    public User user;
    public ERROR_TYPE error_info;

    public enum ERROR_TYPE{
        USER_EXIST,
    }
}
