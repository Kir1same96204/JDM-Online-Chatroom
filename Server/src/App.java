import Common.User;
import Common.Web.*;
import Common.Web.Package.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException; 
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class App {
    static final int PORT = 6666;
    private static ConcurrentHashMap<String, ConnectionHandlerThread> connections = new ConcurrentHashMap<String, ConnectionHandlerThread>();
    static ServerSocket serverSocket = null;
    static App server = null;
    pwdmanager pwm=new pwdmanager();
    public roommanager rmm=new roommanager();
    private App() {
        // do static initialization here

        ConnectionHandlerThread.mainApp = this;
        if (App.serverSocket == null) {
            try {
                App.serverSocket = new ServerSocket(PORT);
            } catch (IOException e) {
                System.err.println("Server Socket build failed!");
                e.printStackTrace();
                return;
            }
        }
        
        try {
            mainLoop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void msgall(WebPackage w,String room){
        for (Map.Entry<String, ConnectionHandlerThread> entry : connections.entrySet()) {
            if(room==null){
                entry.getValue().send(w);
            }
            else if(entry.getValue().current_room.equals(room)){
                entry.getValue().send(w);
            }
        }
    }
    public void savedata(){
        try {
            
            FileOutputStream fos=new FileOutputStream("data.txt");
            ObjectOutputStream oos=new ObjectOutputStream(fos);
            oos.writeObject(rmm);
            oos.close();
        } catch (Exception e) {
        }
        
    }
    public void loaddata(){
        try {
            FileInputStream fis=new FileInputStream("data.txt");
            ObjectInputStream ois=new ObjectInputStream(fis);
            rmm=(roommanager)ois.readObject();

            ois.close();
        } catch (Exception e) {
        }
    }
    public void saveuserdata(){
        try {
            FileOutputStream fos=new FileOutputStream("userdata.txt");
            ObjectOutputStream oos=new ObjectOutputStream(fos);
            oos.writeObject(pwm);
            oos.close();
        } catch (Exception e) {
        }
    }
    public void loaduserdata(){
        try {
            FileInputStream fis=new FileInputStream("userdata.txt");
            ObjectInputStream ois=new ObjectInputStream(fis);
            pwm=(pwdmanager)ois.readObject();

            ois.close();
        } catch (Exception e) {
        }
    }
    void mainLoop() throws Exception {
        // main loop to monitor port, start new thread to handle new connection 
        File f=new File("data.txt");
        if(f.exists()){
            loaddata();
            System.out.println("loadsuccess");
        }
        File f2=new File("userdata.txt");
        if(f2.exists()){
            loaduserdata();
            System.out.println("loaduserdatasuccess");
        }
        while (true){
            Socket socket = serverSocket.accept();
            WebConnection webConnection = new WebConnection(socket);
            
            WebPackage o = null;
            try {
                o = (WebPackage)webConnection.receive();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (o instanceof LoginRequest) {
                LoginRequest obj = (LoginRequest)o;
                int result=pwm.find(obj.id, obj.password);
                LoginResult lgr=new LoginResult();
                if(result>=0){
                    lgr.success=true;
                    lgr.user=new User();
                    lgr.user.name=obj.id;
                    lgr.user.password=obj.password;
                    lgr.chatroom_info=rmm.hsc;
                    ConnectionHandlerThread c=new ConnectionHandlerThread(webConnection,lgr.user);
                    c.start();
                    connections.put(lgr.user.name,c);
                    System.out.println("success");
                }
                else if(result==-2){
                    lgr.success=false;
                    lgr.error_info=LoginResult.ERROR_TYPE.WRONG_PASSWORD;
                }
                else if (result==-1){
                    lgr.success=false;
                    lgr.error_info=LoginResult.ERROR_TYPE.NO_USER;
                }
                else{
                    System.out.println("uke");
                }
                webConnection.send(lgr);
            }
            else if (o instanceof SignupRequest) {
                System.out.println("received");
                SignupRequest obj = (SignupRequest)o;
                int result=pwm.find(obj.id,obj.password);
                SignupResult sur=new SignupResult();
                if(result==-1){
                    sur.success=true;
                    sur.user=new User();
                    sur.user.name=obj.id;
                    sur.user.password=obj.password;
                    sur.chatroom_info=rmm.hsc;
                    pwm.add(obj.id,obj.password);
                    ConnectionHandlerThread c=new ConnectionHandlerThread(webConnection,sur.user);
                    c.start();
                    connections.put(sur.user.name,c);
                    System.out.println("sgn success");
                    saveuserdata();
                }
                else{
                    sur.success=false;
                    sur.error_info=SignupResult.ERROR_TYPE.USER_EXIST;
                }
                webConnection.send(sur);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Server starting...");

        server = new App();

    }

    public void ThreadExit(String user_name) {
        connections.remove(user_name);
    }
}
class pwdmanager implements Serializable{
    String accounts[];
    String passwords[];
    int cnt;

    public pwdmanager() {
        accounts=new String[1000];
        passwords=new String[1000];
        cnt=0;
    }
    
    public int find(String act,String pwd){
        int bj=-1;
        for(int i=0;i<cnt;i++){
            if(act.equals(accounts[i])){
                bj=-2;
                if(pwd.equals(passwords[i])){
                    bj=i;
                }
                break;
            }
        }
        return bj;
    }
    public void add(String act,String pwd){
        accounts[cnt]=act;
        passwords[cnt]=pwd;
        cnt++;
    }
}
