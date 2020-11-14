package com.yc.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
	private ServerSocket ssk = null;
	private boolean started = false;
	private List<Client> clients = new ArrayList<Client>();
	
	public static void main(String[] args) {
		new Server().startServer();
	}

	private void startServer() {
		try {
			ssk = new ServerSocket(8888);
			started = true;
			System.out.println("服务器启动成功，占用端口号8888...");
		} catch (IOException e) {
			e.printStackTrace();
			started = false;
		}
		
		Socket sk = null;
		Client cl = null;
		Thread th = null;
		//监听客户端的请求，如果有客户端连接上来就创建一个线程去处理
		
		try {
			while(started) {
				sk = ssk.accept();
				System.out.println("有客户端连接上来了...");
				
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
		private String nickName;
		
		private DataInputStream dis = null;
		private DataOutputStream dos = null;
		private boolean connected = false;

		public Client(Socket sk) {
			try {
				this.sk = sk;
				connected = true;
				dis = new DataInputStream(sk.getInputStream());
				dos = new DataOutputStream(sk.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			String info = null;
			try {
				while( connected ) {
					info = dis.readUTF();
					
					if(info.startsWith("login#")) {
						nickName = info.replaceFirst("login#", "");
					}
					
					for( Client cl : clients ) {
						cl.send(info);
					}
					
					if( info.startsWith("exit#")) {
						clients.remove(this);
						this.connected = false;
					}
					
					
				}
			} catch (IOException e) {
				e.printStackTrace();
			} 
			finally {
				if( dis != null ) {
					try {
						dis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if( dos != null) {
					try {
						dos.close();
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

		private void send(String msg) {
			
			try {
				dos.writeUTF(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
}


