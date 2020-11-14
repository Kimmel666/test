package com.kimmel.Client;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.custom.SashForm;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * 
 * @author kimmel
 * @date 2020年11月12日 下午3:11:57
 * @describe 实现多线程下载，基本思想，点击一次下载按钮，新建一个线程，这个线程中获取网址的连接资源，获取输入资源后，将资源输出至目标文件夹
 *
 */
public class KimmelDownLoad {

	protected Shell shlKimmel;
	private Text text;
	private Table table;
	
	
	private String url = null;
	private Thread th;

	public static void main(String[] args) {
		try {
			KimmelDownLoad window = new KimmelDownLoad();
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
		shlKimmel.open();
		shlKimmel.layout();
		while (!shlKimmel.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlKimmel = new Shell();
		shlKimmel.setImage(SWTResourceManager.getImage("J:\\游戏外快\\王者壁纸大全\\百里玄策·白虎志\\0 (1).jpg"));
		shlKimmel.setSize(670, 518);
		shlKimmel.setText("kimmel下载器");
		shlKimmel.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm = new SashForm(shlKimmel, SWT.VERTICAL);
		
		Composite composite = new Composite(sashForm, SWT.NONE);
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setBounds(10, 29, 61, 17);
		lblNewLabel.setText("下载网址：");
		
		text = new Text(composite, SWT.BORDER);
		text.setBounds(88, 23, 425, 23);
		
		Button btnNewButton = new Button(composite, SWT.NONE);
		
		btnNewButton.setBounds(544, 19, 80, 27);
		btnNewButton.setText("下载");
		
		Composite composite_1 = new Composite(sashForm, SWT.NONE);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		table = new Table(composite_1, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(137);
		tblclmnNewColumn.setText("下载名称");
		
		TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_1.setWidth(136);
		tblclmnNewColumn_1.setText("下载地址");
		
		TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_2.setWidth(142);
		tblclmnNewColumn_2.setText("文件目录");
		
		TableColumn tblclmnNewColumn_3 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_3.setWidth(236);
		tblclmnNewColumn_3.setText("下载进度");
		sashForm.setWeights(new int[] {67, 409});
		
		/**
		 * 下载按钮，新建一个线程，获取网址的连接资源，输出到本地文件夹
		 */
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				url = text.getText().trim();
				if( url == null || "".equals(url)) {
					MessageDialog.openWarning(shlKimmel, "温馨提示", "请输入您要下载的地址...");
					return;
				}
				
				
				DownLoad downLoad = new DownLoad(url);
				th = new Thread(downLoad);
				th.start();
				tblclmnNewColumn.setToolTipText(url);
				
			}
		});

	}
	
	class DownLoad implements Runnable{
		
		private String url;
		private InputStream input = null;
		private FileOutputStream out = null;
		
		

		public DownLoad(String url) {
			this.url = url;
		}

		public void run() {
			try {
				/**
				 * 建立网址连接
				 */
				URL urlconnect = new URL(url);
				URLConnection urlConnection = urlconnect.openConnection();
				urlConnection.connect();
				
				/**
				 * input获取网址资源，out选好输出文件
				 */
				input = urlConnection.getInputStream();
				out = new FileOutputStream("A:\\yc.js\\三期\\class\\day3 网络实例\\"+ url.substring(url.lastIndexOf("/")+1));
				
				/**
				 * 开始运送资源
				 */
				byte[] bt = new byte[1024];
				int len = 0;
				System.out.println("开始下载...");
				while( ( len =  input.read(bt) ) > 0 ) {
					out.write(bt, 0, len);
				}
				System.out.println("下载" + url.substring(url.lastIndexOf("/") + 1)  + "完成");
			} catch (MalformedURLException e) {
				e.printStackTrace();
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
				if( out != null) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
