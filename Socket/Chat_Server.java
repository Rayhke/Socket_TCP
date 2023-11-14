package Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Chat_Server {
	
	public static void main(String[] args) {
		Chat_Server CS = new Chat_Server();
		CS.start();
	}
	
	public void start() {
		ServerSocket serverSocket = null;
		Socket socket = null;
		
		System.out.println("[서버 On] : 서버가 연결 되었습니다.");
		
		try {
			serverSocket = new ServerSocket(10250);
			
			while (true) {
				// 접속자가 있다면 연결
				socket = serverSocket.accept();
				
				// client가 접속할 때 마다, 새로운 스레드 생성
				ServerReceiver receiver = new ServerReceiver(socket);	
				receiver.start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (serverSocket != null) {
				try {
					serverSocket.close();
					System.out.println("[서버 Off] : 서버가 종료 되었습니다.");
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("[서버 Error] : 소켓 통신 오류");
				}
			}
		}
	}
}

class ServerReceiver extends Thread {
	
	// 프린트 라이더 타입으로 값 저장
	static List<PrintWriter> list = Collections.synchronizedList(new ArrayList<PrintWriter>());
	
	Socket socket = null;
	// 버퍼 리더 (입력 스트림)
	BufferedReader in = null;
	// 프린트 라이더 (출력 스트림)
	PrintWriter out = null;
	
	BufferedReader echo = new BufferedReader(new InputStreamReader(System.in));
	
	public ServerReceiver (Socket socket) {
		this.socket = socket;
		
		try {
			
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream());
			list.add(out); // 스레드 리스트에 넣기
			// while (true) {
				// 서버 쪽에서 메세지 뿌리기
			// }
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void run() {
		String name = "";
		
		try {
			// 첫 연결은 Client 에서 넘어온 name을 수신
			name = in.readLine();
			System.out.println("[" + name + "] 접속 On");
			sendAll("[" + name + "]님이 입장 하셨습니다.");
			
			while (in != null) {
				String inputMsg = in.readLine();
				System.out.println("[" + name + "] : " + inputMsg);
				sendAll(out, ("[" + name + "] : " + inputMsg));
			}
			
		} catch (IOException e) {
			System.out.println("[" + name + "] 접속 Down");
			// e.printStackTrace();
		} finally {
			sendAll("[" + name + "]님이 나갔습니다.");
			list.remove(out);
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("[" + name + "] 접속 Off");
		
	}
	private void sendAll (String echo) {
		for (PrintWriter out : list) {
			out.println(echo);
			out.flush();
		}
	}
	
	private void sendAll (PrintWriter sendUser, String echo) {
		for (PrintWriter out : list) {
			if (sendUser == out) { continue; }
			out.println(echo);
			out.flush();
		}
	}
}
