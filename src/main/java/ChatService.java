import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

// github init 2016.12.09 by sunrc

public class ChatService {
    //클라이언트 소켓이 접속될 때 저장할 Collection선언
    private ArrayList<ServerThread> clist;
    private ServerSocket ss;
    private String reip;
    
    public ChatService() {        
        try {
            // 서버 소켓 생성
            ss = new ServerSocket(9999);
            System.out.println("ChatService.Server Start!");
             
            //ArrayList 생성
            clist = new ArrayList<ServerThread>();
             
        } catch (IOException e) {
            e.printStackTrace();
        }
    }     
    
    public void exe() {
        while(true) { //지속적인 서비스
            // 클라이언트의 소켓이 접속했을 때 동작하는 메서드
            Socket s=null;
            try {
                s = ss.accept();
                String reip = s.getInetAddress().getHostAddress();
                System.out.println("ChatService.Log : " +reip);
                
                UserInfo userInfo = new UserInfo(reip);
                
                // 접속해온 클라이언트의 서비스를 위임받는 클래스를 객체로 생성하면서
                // 생성자를 통해 필요한 정보를 주입한다.
                ServerThread ct = new ServerThread(s, this, userInfo);
                // 접속자의 주소를 기억하기 위해서 ArrayList에 기억
                clist.add(ct);
                // 각 스레드를 시작한다.
                ct.start();
                System.out.println("ChatService.Current number of Clients :" + clist.size());
            } catch (IOException e) {
                e.printStackTrace();
            }
            reip = s.getInetAddress().getHostAddress();
            System.out.println("ChatService.Ip : " + reip);     
        }
    }   
    
    public void sendMsg(String str1, String str2, String str3, String str4) {
        // protocol
        // ex) talk/user/msg/null
        // ex) draw/color/x/y
        StringBuffer sb = new StringBuffer();
        if(str1.equals("talk")) {
            sb.append("talk/"+str2+"/["+reip+"]/"+str4+ "/null" );
                        
            for(ServerThread e : clist) {
                e.getPw().println(sb.toString());
                System.out.println("clist : " + sb.toString());
            }
        }else {
            sb.append(str1).append("/");
            sb.append(str2).append("/");
            sb.append(str3).append("/");
            sb.append(str4);
            
            System.out.println("ChatService.sendMsg.str1 : " + str1);
            System.out.println("ChatService.sendMsg.str2 : " + str2);
            System.out.println("ChatService.sendMsg.str3 : " + str3);
            System.out.println("ChatService.sendMsg.str4 : " + str4);
            
            String userList = getUserList();
            
            for(ServerThread e : clist) {                
                e.getPw().println(userList);
                System.out.println("ChatService.sendMsg.userList : " + userList);
            }
        }            
    }    
    
    public String getUserList()
    {
    	StringBuffer sb = new StringBuffer();
    	sb.append("UserList");
    	
        for(ServerThread e : clist) {
        	sb.append("/").append(e.getUserInfo().getNickName());
        }
        
        return sb.toString();
    }
    
    public static void main(String[] args) {
        new ChatService().exe();
    }    
}
