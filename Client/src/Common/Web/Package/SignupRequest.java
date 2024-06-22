package Common.Web.Package;

public class SignupRequest extends WebPackage{
    public String id;
    public String password;

    public SignupRequest() {}

    public SignupRequest(String id, String password){
        this.id = id;
        this.password = password;
    }
}
