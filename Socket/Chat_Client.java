package Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Chat_Client {

	public static void main(String[] args) {
		Chat_Client CC = new Chat_Client();
		CC.start();
	}
	
	public void start() {
		Socket socket = null;
		BufferedReader in = null;
		
		BufferedReader echo = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			socket = new Socket("localhost", 10250);
			System.out.println("[서버 접속] : 서버에 연결 되었습니다.");
			System.out.print("닉네임을 입력 해주세요 : ");
			String name = echo.readLine();
			
			ClientReceiver receiver = new ClientReceiver(socket, name);
			receiver.start();
			
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while (in != null) {
				String inputMsg = in.readLine();
				// inputMsg 에 담긴 값이 자기 닉네임이 붙은 문자열 형태로 뿌려 졌다면 종료
				if (("[" + name + "]님이 나갔습니다.").equals(inputMsg)) { break; }
				System.out.println(inputMsg);
			}
			
			
		} catch (IOException e) {
			System.out.println("[서버 Error] : 소켓 통신 오류");
			// e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("[접속 Off] : 접속이 종료 되었습니다.");
	}
	
}

class ClientReceiver extends Thread {
	Socket socket = null;
	String name = null;
	
	BufferedReader echo = new BufferedReader(new InputStreamReader(System.in));
	
	public ClientReceiver(Socket socket, String name) {
		this.socket = socket;
		this.name = name;
	}
	
	public void run() {
		PrintStream out = null;
		
		try {
			out = new PrintStream(socket.getOutputStream());
			out.println(name);
			out.flush();
			
			while (true) {
				String outputMsg = echo.readLine();
				out.println(outputMsg);
				out.flush();
				if ("OUT".equals(outputMsg)) { break; }
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
