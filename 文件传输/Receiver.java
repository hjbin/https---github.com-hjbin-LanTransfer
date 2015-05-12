/** 
 *  �����ļ������ն�
 *  @author lihao
 */ 

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;


public class Receiver {

	/**
	 * main����
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		//���ͷ���IP�Ͷ˿ں�
		InetAddress senderHost = InetAddress.getByName("localhost");
		int senderPort = 1234;
		
		//����ļ�·��
		String filePath = "E:\\test\\";
		
		final int MAX_LEN = 100; 
		
		//������IP�Ͷ˿ں�
		InetAddress selfHost = InetAddress.getByName("localhost");
		int selfPort = 1256;
		
		//UDP�����ļ������ļ���С
		DatagramSocket	mySocket = new DatagramSocket(selfPort,selfHost); 
	
		//�����ļ���С
		byte[ ] bufferLength = new byte[MAX_LEN];                                     
	    DatagramPacket datagramLength = 
				new DatagramPacket(bufferLength, MAX_LEN);
		mySocket.receive(datagramLength);
		//��ȡ�ļ�����
		ByteArrayInputStream bais=new ByteArrayInputStream(bufferLength);  
        DataInputStream dis=new DataInputStream(bais);
        long fileLength = dis.readLong(); 
        System.out.println(fileLength); 
		
		//�����ļ���
		byte[ ] bufferName = new byte[MAX_LEN];                                     
	    DatagramPacket datagramName = 
				new DatagramPacket(bufferName, MAX_LEN);
		mySocket.receive(datagramName);
		String fileName = new String(bufferName);
		System.out.println(fileName);
			
		
		//�ر�socket
		mySocket.close( );
				
		//�ļ�·���������ļ�����
		String receFileStr = filePath + fileName;
		File receFile = new File(receFileStr);

		
		//�����ļ���С�����߳���Ŀ
		if(fileLength<20971520){   //fileLength<20971520  
			
			/*long startPos = 0;*/
			
			//����Ŀ�ʼλ��
			long initStartPos = 0;
			
			//TODO::�����Ӧ���Ǵ����ݿ���ȡ������
			long startPos = 0;
			
			//�Ѿ����͵ĳ���
			long sentLength = startPos - initStartPos;
			
			Socket dataSocket = new Socket(senderHost, senderPort);
			Client client = new Client(dataSocket,receFile,startPos,fileLength - sentLength);
			
			//�����߳�
			if(!client.isAlive())
				client.start();
			
			//��鷢���߳��Ƿ����
    	    while(client.isAlive());
			
		}else if(fileLength<104857600){  //fileLength<104857600
			
			/*long startPos1 = 0;
			long startPos2 = fileLength/3;
			long startPos3 = 2*fileLength/3;*/
			
			//����Ŀ�ʼλ��
			long initStartPos1 = 0;
			long initStartPos2 = fileLength/3;
			long initStartPos3 = 2*fileLength/3;
			
			//TODO::�⼸����Ӧ���Ǵ����ݿ���ȡ������
			long startPos1 = 0;
			long startPos2 = fileLength/3;
			long startPos3 = 2*fileLength/3;	
			
			//�Ѿ����͵ĳ���
			long sentLength1 = startPos1 - initStartPos1;
			long sentLength2 = startPos2 - initStartPos2;
			long sentLength3 = startPos3 - initStartPos3;
			
			
			Socket dataSocket1 = new Socket(senderHost, senderPort);
			Client client1 = new Client(dataSocket1,receFile,startPos1,fileLength/3 - sentLength1);
			Socket dataSocket2 = new Socket(senderHost, senderPort);
			Client client2 = new Client(dataSocket2,receFile,startPos2,fileLength/3 - sentLength2);
			Socket dataSocket3 = new Socket(senderHost, senderPort);
			Client client3 = new Client(dataSocket3,receFile,startPos3,fileLength-2*fileLength/3 - sentLength3);
			
			if(!client1.isAlive())
				client1.start();
			if(!client2.isAlive())
				client2.start();
			if(!client3.isAlive())
				client3.start();
			
			//��鷢���߳��Ƿ����
    	    while(client1.isAlive() || client2.isAlive() 
    	    		   || client3.isAlive());
			
		}else{
			
			/*long startPos1 = 0;
			long startPos2 = fileLength/5;
			long startPos3 = 2*fileLength/5;
			long startPos4 = 3*fileLength/5;
			long startPos5 = 4*fileLength/5;*/
			
			//����Ŀ�ʼλ��
			long initStartPos1 = 0;
			long initStartPos2 = fileLength/3;
			long initStartPos3 = 2*fileLength/3;
			long initStartPos4 = 3*fileLength/3;
			long initStartPos5 = 4*fileLength/3;
			
			//TODO::�⼸����Ӧ���Ǵ����ݿ���ȡ������
			long startPos1 = 0;
			long startPos2 = fileLength/5;
			long startPos3 = 2*fileLength/5;
			long startPos4 = 3*fileLength/5;
			long startPos5 = 4*fileLength/5;
			
			//�Ѿ����͵ĳ���
			long sentLength1 = startPos1 - initStartPos1;
			long sentLength2 = startPos2 - initStartPos2;
			long sentLength3 = startPos3 - initStartPos3;
			long sentLength4 = startPos4 - initStartPos4;
			long sentLength5 = startPos5 - initStartPos5;
			
			Socket dataSocket1 = new Socket(senderHost, senderPort);
			Client client1 = new Client(dataSocket1,receFile,startPos1,fileLength/5 - sentLength1);
			Socket dataSocket2 = new Socket(senderHost, senderPort);
			Client client2 = new Client(dataSocket2,receFile,startPos2,fileLength/5 - sentLength2);
			Socket dataSocket3 = new Socket(senderHost, senderPort);
			Client client3 = new Client(dataSocket3,receFile,startPos3,fileLength/5 - sentLength3);
			Socket dataSocket4 = new Socket(senderHost, senderPort);
			Client client4 = new Client(dataSocket4,receFile,startPos4,fileLength/5 - sentLength4);
			Socket dataSocket5 = new Socket(senderHost, senderPort);
			Client client5 = new Client(dataSocket5,receFile,startPos5,fileLength-4*fileLength/5 - sentLength5);
			
			
			if(!client1.isAlive())
				client1.start();
			if(!client2.isAlive())
				client2.start();
			if(!client3.isAlive())
				client3.start();
			if(!client4.isAlive())
				client4.start();
			if(!client5.isAlive())
				client5.start();	
		
			
			//��鷢���߳��Ƿ����
    	    while(client1.isAlive() || client2.isAlive() 
    	    		   || client3.isAlive() || client4.isAlive() || client5.isAlive());
    	   
		}	
		
	}//end Main


}//end class

