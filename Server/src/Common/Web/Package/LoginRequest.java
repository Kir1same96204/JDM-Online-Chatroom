package Common.Web.Package;

public class LoginRequest extends WebPackage{
    public String id;
    public String password;

    public LoginRequest() {}

    public LoginRequest(String id, String password){
        this.id = id;
        this.password = password;
    }
}
