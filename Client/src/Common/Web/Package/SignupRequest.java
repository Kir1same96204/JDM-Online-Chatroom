package Common.Web.Package;

public class SignupRequest {
    public String id;
    public String password;

    public SignupRequest() {}

    public SignupRequest(String id, String password){
        this.id = id;
        this.password = password;
    }
}
