
import Common.ChatroomInfo;
import Common.Message.Message;
import Common.Message.MessageHistory;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap; 

public class roommanager implements Serializable{
    public ConcurrentHashMap<String,roomdata> hs=new ConcurrentHashMap<>();
    public HashSet<ChatroomInfo> hsc=new HashSet<>();
    public boolean exists(String s){
        return hs.containsKey(s);
    }
    public roomdata getroomdata(String s){
        if(exists(s)){
            return hs.get(s);
        }
        return null;
    }
    public MessageHistory getmessagehistory(String s){
        if(exists(s)){
            return hs.get(s).hist;
        }
        return null;
    }
    public boolean addroom(String s){
        if(exists(s)){
            return false;
        }
        hs.put(s, new roomdata());
        ChatroomInfo cri=new ChatroomInfo();
        cri.name=s;
        cri.user_num=0;
        hsc.add(cri);
        return true;
    }
    public void changenum(String s,int num){
        Iterator<ChatroomInfo> iterator = hsc.iterator();
        ChatroomInfo targetObject = null;
        while (iterator.hasNext()) {
            ChatroomInfo obj = iterator.next();
            if (obj.name.equals(s)) {
                targetObject = obj;
                break;
            }
        }
        hsc.remove(targetObject);
        targetObject.user_num+=num;
        hsc.add(targetObject);
    }
    public ChatroomInfo getroominfo(String s){
        Iterator<ChatroomInfo> iterator = hsc.iterator();
        ChatroomInfo targetObject = null;
        while (iterator.hasNext()) {
            ChatroomInfo obj = iterator.next();
            if (obj.name.equals(s)) {
                targetObject = obj;
                break;
            }
        }
        return targetObject;
    }
    public void addmsg(String s,Message msg){
        roomdata rd=getroomdata(s);
        if (rd == null) {
            rd = new roomdata();
        }
        else hs.remove(s);
        rd.hist.addmessage(msg);
        hs.put(s, rd);
    }
}