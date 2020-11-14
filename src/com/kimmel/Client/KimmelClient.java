package com.kimmel.Client;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class KimmelClient implements Runnable{

	protected Shell shell;
	private Text text;
	private Text text_1;
	private Text text_2;
	private Text text_3;
	private Text text_4;

	private Tree tree;
	private TreeItem ti;
	private Socket sk = null;
	private DataInputStream input = null;
	private DataOutputStream out = null;

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
			KimmelClient window = new KimmelClient();
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
		shell.setText("kimmel聊天室");
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
		shell.setImage(SWTResourceManager.getImage("J:\\游戏外快\\王者壁纸大全\\安琪拉-如懿\\0 (5).jpg"));
		shell.setSize(790, 516);
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));

		SashForm sashForm = new SashForm(shell, SWT.VERTICAL);

		Composite composite = new Composite(sashForm, SWT.NONE);

		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setBounds(10, 24, 52, 17);
		lblNewLabel.setText("IP地址：");

		text = new Text(composite, SWT.BORDER);
		text.setText("127.0.0.1");
		text.setBounds(68, 21, 106, 23);

		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setText("端口号：");
		lblNewLabel_1.setBounds(191, 27, 52, 17);

		text_1 = new Text(composite, SWT.BORDER);
		text_1.setText("8888");
		text_1.setBounds(249, 24, 106, 23);

		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		lblNewLabel_2.setText("昵称：");
		lblNewLabel_2.setBounds(379, 27, 52, 17);

		text_2 = new Text(composite, SWT.BORDER);
		text_2.setBounds(437, 24, 106, 23);

		Button btnNewButton = new Button(composite, SWT.NONE);

		btnNewButton.setBounds(581, 24, 52, 27);
		btnNewButton.setText("登录");

		Button btnNewButton_1 = new Button(composite, SWT.NONE);
		btnNewButton_1.setText("退出");
		btnNewButton_1.setBounds(662, 24, 52, 27);

		Composite composite_1 = new Composite(sashForm, SWT.NONE);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));

		SashForm sashForm_1 = new SashForm(composite_1, SWT.NONE);

		Composite composite_3 = new Composite(sashForm_1, SWT.NONE);
		composite_3.setLayout(new FillLayout(SWT.HORIZONTAL));

		text_3 = new Text(composite_3, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);

		Composite composite_4 = new Composite(sashForm_1, SWT.NONE);
		composite_4.setLayout(new FillLayout(SWT.HORIZONTAL));

		tree = new Tree(composite_4, SWT.BORDER);
		sashForm_1.setWeights(new int[] {644, 127});

		Composite composite_2 = new Composite(sashForm, SWT.NONE);

		text_4 = new Text(composite_2, SWT.BORDER);
		text_4.setBounds(10, 10, 658, 50);

		Button btnNewButton_2 = new Button(composite_2, SWT.NONE);

		btnNewButton_2.setBounds(684, 10, 69, 50);
		btnNewButton_2.setText("发送");
		sashForm.setWeights(new int[] {64, 338, 69});
		/**
		 * 登录：需要连接服务器，发送登录数据,创建新的线程
		 */
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				connect();
				btnNewButton.setEnabled(false);

				th = new Thread(KimmelClient.this);
				th.start();

				send("login#" +  nickName);
				System.out.println(nickName);
			}

		});

		/**
		 * 发送按钮,将文本框内的信息获取后，初步判断是否发送，并清空文本框
		 */
		btnNewButton_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String info = text_4.getText().trim();

				if( info == null || "".equals(info) ) {
					return;
				}

				send("info#" + nickName + "说： " + info);
				text_4.setText("");

			}
		});

	}

	/**
	 * 连接服务器,你需要获取本机IP地址，服务器端口号,昵称信息。同时新建一个socket对象，将其实例化
	 */
	private void connect() {
		String ip = text.getText().trim();
		int port = Integer.parseInt(text_1.getText().trim());
		nickName = text_2.getText().trim();

		try {
			sk = new Socket(ip,port);

			input = new DataInputStream(sk.getInputStream());
			out = new DataOutputStream(sk.getOutputStream());

			connected = true;
			System.out.println("客户端已连接至服务器...");

		} catch (IOException e) {
			e.printStackTrace();
			connected = false;
			System.exit(0);
		}
	}

	/**
	 * 这是线程在运行过程中时，处理的步骤
	 */
	public void run() {

		try {
			while( connected ) {
				if( sk.isConnected() ) {
					msg = input.readUTF();

					if( msg.startsWith("login#")) {
						msg = msg.replaceFirst("login#", "");
						System.out.println("接受到参数了01");
						
						Display.getDefault().asyncExec(new Runnable() {

							public void run() {
								
									ti = new TreeItem(tree, SWT.NONE);
									ti.setText(msg);
									System.out.println("接受到参数了02");
								
							}
						});
					}else if( msg.startsWith("info#")) {
						msg = msg.replaceFirst("info#", "");

						Display.getDefault().asyncExec(new Runnable() {

							public void run() {
								text_3.append(msg + "\r\n"); 

							}
						});
					}else if(msg.startsWith("exit#")) {
//						//问题应该是在这里
//						this.connected = false;
//						this.sk.close();
//						break;
					}


				} 
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 发送数据
	 * @param string
	 * @param nickName
	 */
	private void send(String msg) {
		try {
			out.writeUTF(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
