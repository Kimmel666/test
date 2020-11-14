package com.kimmel.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author kimmel
 * @date 2020年11月9日 下午7:11:33
 * @describe 用作服务器，接收大量客户端传来的讯息
 *
 */
public class KimmelServer {

	private ServerSocket server;
	private boolean flag = false;
	private List<Client> clients = new ArrayList<Client>();

	public static void main(String[] args) throws IOException {
		new KimmelServer().startServer();
	}

	private void startServer() throws IOException {

		try {
			server = new ServerSocket(8888);
			System.out.println("服务器已经启动，端口号为： " + server.getLocalPort() );
			flag = true;
		} catch (IOException e) {
			e.printStackTrace();
			flag = false;
		}


		Socket sk = null;
		Client cl = null;
		Thread th = null;


		try {
			while(flag) {
				sk = server.accept();
				cl = new Client(sk);
				th = new Thread(cl);
				th.start();
				clients.add(cl);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} 





	}

	class Client implements Runnable{

		private Socket sk = null;
		private String nickName;  //我刚刚给你改了这里  ,咋又把这里加上去了 。  都说这里不能声明为空

		private boolean connected = false;
		private DataInputStream input = null;
		private DataOutputStream out = null;



		/**
		 * 客户端类构造函数
		 * @param sk
		 */
		public Client( Socket sk) {
			try {
				this.sk = sk;
				connected = true;
				input = new DataInputStream(sk.getInputStream());
				out = new DataOutputStream(sk.getOutputStream());
			} catch (IOException e) {
				connected = false;
				e.printStackTrace();
			}
		}


		/**
		 * run就是对信息流进行处理了
		 */
		public void run() {
			String info = null;

			try {
				while(connected) {
					info = input.readUTF();
					if( info.startsWith("login#")) {
						nickName = info.replaceFirst("login#", "");
						System.out.println("已经接受到");
					}

					for(Client cl : clients ) {
						cl.send(info);
					}

					if( info.startsWith("exit#")) {
						clients.remove(this);
						this.connected = false;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if( input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				if( out != null ) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				if( sk != null) {
					try {
						sk.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}



		}


		private void send(String info) {
			try {
				out.writeUTF(info);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
