package Common.Web.Package;

import java.io.Serializable;

enum PackageType{
    LOGIN_REQUEST,
    SIGNUP_REQUEST,
    LOGIN_RESULT,
    SIGNUP_RESULT,
}

/**
 * Basic class for packages between clients and server
 */
public class WebPackage implements Serializable{
    public PackageType packageType;
    public String sender;
    public String receiver;
}
