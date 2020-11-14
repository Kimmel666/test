package com.yc.chat;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.custom.SashForm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;

/**
 * 
 * @author kimmel
 * @date 2020年11月6日 上午9:34:33
 * @describe 用作与服务端通信的客户端
 *
 */
public class WeChat implements Runnable{

	protected Shell shell;
	private Text text;
	private Text text_1;
	private Text text_2;
	private Text text_3;
	private Text text_4;
	
	private Tree tree;
	private TreeItem ti;
	private Socket sk = null;
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	
	private String msg = null;
	private boolean connected = false;
	private Thread th = null;
	private String nickName;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			WeChat window = new WeChat();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.setText("yc聊天室");
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(1091, 686);
		shell.setText("SWT Application");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm = new SashForm(shell, SWT.VERTICAL);
		
		Composite composite = new Composite(sashForm, SWT.NONE);
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setBounds(23, 26, 59, 24);
		lblNewLabel.setText("服务器IP：");
		
		text_2 = new Text(composite, SWT.BORDER);
		text_2.setText("127.0.0.1");
		text_2.setBounds(86, 24, 105, 23);
		
		text_3 = new Text(composite, SWT.BORDER);
		text_3.setText("8888");
		text_3.setBounds(292, 24, 105, 23);
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setText("端口：");
		lblNewLabel_1.setBounds(229, 26, 59, 24);
		
		text_4 = new Text(composite, SWT.BORDER);
		text_4.setBounds(492, 24, 105, 23);
		
		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		lblNewLabel_2.setText("昵称：");
		lblNewLabel_2.setBounds(429, 26, 59, 24);
		
		Button btnNewButton_1 = new Button(composite, SWT.NONE);
		
		btnNewButton_1.setBounds(724, 21, 80, 27);
		btnNewButton_1.setText("登录");
		
		Button btnNewButton_2 = new Button(composite, SWT.NONE);
		btnNewButton_2.setBounds(847, 21, 80, 27);
		btnNewButton_2.setText("退出");
		
		Composite composite_1 = new Composite(sashForm, SWT.NONE);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm_1 = new SashForm(composite_1, SWT.NONE);
		
		Composite composite_3 = new Composite(sashForm_1, SWT.NONE);
		composite_3.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		text = new Text(composite_3, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		
		Composite composite_4 = new Composite(sashForm_1, SWT.NONE);
		composite_4.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		tree = new Tree(composite_4, SWT.BORDER);
		sashForm_1.setWeights(new int[] {918, 154});
		
		Composite composite_2 = new Composite(sashForm, SWT.NONE);
		
		text_1 = new Text(composite_2, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		text_1.setBounds(10, 20, 906, 65);
		
		Button btnNewButton = new Button(composite_2, SWT.NONE);
		
		btnNewButton.setBounds(937, 24, 111, 61);
		btnNewButton.setText("发送");
		sashForm.setWeights(new int[] {60, 487, 94});
		
		//登录
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				connect();
				btnNewButton_1.setEnabled(false);
				
				th = new Thread(WeChat.this);
				th.start();
				
				send("login#" + nickName );
				
			}

			
		});
		
		//发送消息的方法
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String info = text_1.getText().trim();
				if( info == null || "".equals(info) ) {
					return;
				}
				
				send( "info#" + nickName + "说：" + info);
				text_1.setText("");
			}
		});
		

	}
	
	private void connect() {
		/**
		 * 获取ip,端口号,昵称
		 */
		String ip = text_2.getText().trim();
		int port = Integer.parseInt(text_3.getText().trim());
		nickName = text_4.getText().trim();
		
		/**
		 * 建立与服务器的连接
		 */
		try {
			sk = new Socket(ip, port);
			
			dis = new DataInputStream(sk.getInputStream());
			dos = new DataOutputStream(sk.getOutputStream());
			
			connected = true;
		} catch (IOException e) {
			e.printStackTrace();
			connected = false;
			System.exit(0);
		}
		
	}

	@Override
	public void run() {
		try {
			while(connected) {
				if(sk.isConnected()) {
					
					msg = dis.readUTF();
					
					
					/**
					 * 客户端显示消息的方法
					 */
					if( msg.startsWith("login#")) {
						msg = msg.replaceFirst("login#", "");
						Display.getDefault().asyncExec(new Runnable() {
							
							public void run() {
								ti = new TreeItem(tree, SWT.NONE);
								ti.setText(msg);
							}
						});
					}else if( msg.startsWith("info#")) {
						msg = msg.replaceFirst("info#", "");
						
						Display.getDefault().asyncExec(new Runnable() {
							
							public void run() {
								text.append(msg + "\r\n"); 
								
							}
						});
					}else if(msg.startsWith("exit#")) {
						
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
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
