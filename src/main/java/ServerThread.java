import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ServerThread extends Thread{
    private Socket socket;  // 소켓을 담당
    private ChatService server; // 브로드캐스팅을 위한 서버의 주소
    // 소켓으로 부터 연결된 스트림을 원활하게 받아낼 2차 스트림을 선언
    private PrintWriter pw;
    private Scanner in; 
    private UserInfo userInfo;
    
    //생성하자마자 접속된 소켓의 주소와 서버의 주소를 받아와서 멤버에 등록
    public ServerThread(Socket socket, ChatService server, UserInfo userInfo) {
        this.socket = socket;
        this.server = server;
        this.userInfo = userInfo;
        
        // 연결된 소켓으로 부터 데이터를 전송할 스트림을 생성하는데
        // 2차 스트림을 사용해서 한줄 단위로 개선한 형태로 데이터를 전송한다
        try {
        	System.out.println("ServerThread");
        	
            pw = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
        } catch (IOException e) {
            e.printStackTrace();
        }       
    }
    
    public void run() {
        try {
            in = new Scanner(socket.getInputStream());
            // 서버가 소켓으로 부터 전송되어온 데이터를 읽어 내는 영역
            String res = "";
            while(in.hasNext()) {
                res = in.nextLine(); // 스트림으로 한줄단위로 읽어 내는 메서드
                System.out.println("ServerThread.run.Message : " + res);
                transMsg(res);          
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }   
    
    private void transMsg(String res) {
        // ex) talk/user/msg/null
        // ex) draw/color/x/y
         
        StringTokenizer stn = new StringTokenizer(res,"/");
         
        String str1 = stn.nextToken();
        String str2 = stn.nextToken();
        String str3 = stn.nextToken();
        String str4 = stn.nextToken();
        
        if (str1.equals("room"))
        	userInfo.setNickName(str4);
        
        // 서버의 sendMsg로 분석한 토큰을 전달한다.
        server.sendMsg(str1,str2,str3,str4);
    }
 
    public UserInfo getUserInfo()
    {
    	return userInfo;
    }
    
    public PrintWriter getPw() {
        return pw;
    }    
}
