package com.liang.tcpserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	MulticastSocket s = null;
	InetAddress group;
	private String line = "";
	Handler mHandler;
	private Button send_btn;
	private TextView show_tv;
	private TextView ip_tv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ip_tv = (TextView) findViewById(R.id.ip_tv);
		getIP();
		System.out.println("oncreate");
		show_tv = (TextView) findViewById(R.id.show_tv);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what == 0x1233) {
					show_tv.setText("nihao " + line);
				}

			}
		};
		new Thread(new UDPServerThread()).start();

		send_btn = (Button) findViewById(R.id.send_btn);
		send_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new Thread(new UDPClinetThread()).start();
			}
		});

	}

	private void getIP() {
		// ��ȡwifi����

		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		// �ж�wifi�Ƿ���

		if (!wifiManager.isWifiEnabled()) {

			wifiManager.setWifiEnabled(true);

		}

		WifiInfo wifiInfo = wifiManager.getConnectionInfo();

		int ipAddress = wifiInfo.getIpAddress();

		String ip = intToIp(ipAddress);

		ip_tv.setText(ip);

	}

	private String intToIp(int i) {

		return (i & 0xFF) + "." +

		((i >> 8) & 0xFF) + "." +

		((i >> 16) & 0xFF) + "." +

		(i >> 24 & 0xFF);

	}

	class UDPServerThread implements Runnable {
		// UDP�������˿�
		public void run() {
			try {
				group = InetAddress.getByName("239.255.255.105");
				s = new MulticastSocket(9001);
				s.setTimeToLive(255);
				System.out.println("�鲥���ͳ����ʼ����ϣ����ڼ����鲥��....");
				s.joinGroup(group);
				System.out.println("�ɹ������鲥��,���ڽ����鲥��Ϣ....");
				while (true) {
					byte[] buf = new byte[1024];
					DatagramPacket packet = new DatagramPacket(buf, buf.length);
					s.receive(packet);
					System.out.println("���յ� : "
							+ new String(packet.getData(), packet.getOffset(),
									packet.getLength()));
					// Message message = handler.obtainMessage();
					// Bundle bundler = new Bundle();
					// bundler.putString("MainActivity", "���յ� : "
					// + new String(packet.getData(), packet.getOffset(),
					// packet.getLength()));
					line = new String(packet.getData(), packet.getOffset(),
							packet.getLength());
					
					if (line.equals(ip_tv.getText().toString())){ 
						continue;
					}
					
					
					handler.sendEmptyMessage(0x1233);

					// message.setData(bundler);
					// handler.sendMessage(message);
					// String msg = "MainActivity";
					// DatagramPacket packet1 = new
					// DatagramPacket(msg.getBytes(),
					// msg.length(), group, 9002);
					// s.send(packet1);
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				s.close();
			}
		}
	}

	class UDPClinetThread implements Runnable {
		// UDP�������˿�
		public void run() {
			try {
				MulticastSocket s = null;
				InetAddress group;
				group = InetAddress.getByName("239.255.255.105");
				s = new MulticastSocket();

				System.out.println("�鲥���ͳ����ʼ�����");
				String msg = ip_tv.getText().toString();
				DatagramPacket packet = new DatagramPacket(msg.getBytes(),
						msg.length(), group, 9001);
				System.out.println("���ڷ����鲥��Ϣ....");
				try {
					s.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("���ͳɹ�");
				s.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 0x1233) {
				show_tv.setText(line);
			}
		}
	};
}
