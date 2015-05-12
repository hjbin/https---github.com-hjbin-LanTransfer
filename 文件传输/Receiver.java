/** 
 *  传输文件，接收端
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
	 * main函数
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		//发送方的IP和端口号
		InetAddress senderHost = InetAddress.getByName("localhost");
		int senderPort = 1234;
		
		//存放文件路径
		String filePath = "E:\\test\\";
		
		final int MAX_LEN = 100; 
		
		//本方的IP和端口号
		InetAddress selfHost = InetAddress.getByName("localhost");
		int selfPort = 1256;
		
		//UDP接收文件名和文件大小
		DatagramSocket	mySocket = new DatagramSocket(selfPort,selfHost); 
	
		//接收文件大小
		byte[ ] bufferLength = new byte[MAX_LEN];                                     
	    DatagramPacket datagramLength = 
				new DatagramPacket(bufferLength, MAX_LEN);
		mySocket.receive(datagramLength);
		//读取文件长度
		ByteArrayInputStream bais=new ByteArrayInputStream(bufferLength);  
        DataInputStream dis=new DataInputStream(bais);
        long fileLength = dis.readLong(); 
        System.out.println(fileLength); 
		
		//接收文件名
		byte[ ] bufferName = new byte[MAX_LEN];                                     
	    DatagramPacket datagramName = 
				new DatagramPacket(bufferName, MAX_LEN);
		mySocket.receive(datagramName);
		String fileName = new String(bufferName);
		System.out.println(fileName);
			
		
		//关闭socket
		mySocket.close( );
				
		//文件路径（包括文件名）
		String receFileStr = filePath + fileName;
		File receFile = new File(receFileStr);

		
		//根据文件大小决定线程数目
		if(fileLength<20971520){   //fileLength<20971520  
			
			/*long startPos = 0;*/
			
			//最初的开始位置
			long initStartPos = 0;
			
			//TODO::这个数应该是从数据库里取出来的
			long startPos = 0;
			
			//已经传送的长度
			long sentLength = startPos - initStartPos;
			
			Socket dataSocket = new Socket(senderHost, senderPort);
			Client client = new Client(dataSocket,receFile,startPos,fileLength - sentLength);
			
			//启动线程
			if(!client.isAlive())
				client.start();
			
			//检查发送线程是否结束
    	    while(client.isAlive());
			
		}else if(fileLength<104857600){  //fileLength<104857600
			
			/*long startPos1 = 0;
			long startPos2 = fileLength/3;
			long startPos3 = 2*fileLength/3;*/
			
			//最初的开始位置
			long initStartPos1 = 0;
			long initStartPos2 = fileLength/3;
			long initStartPos3 = 2*fileLength/3;
			
			//TODO::这几个数应该是从数据库里取出来的
			long startPos1 = 0;
			long startPos2 = fileLength/3;
			long startPos3 = 2*fileLength/3;	
			
			//已经传送的长度
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
			
			//检查发送线程是否结束
    	    while(client1.isAlive() || client2.isAlive() 
    	    		   || client3.isAlive());
			
		}else{
			
			/*long startPos1 = 0;
			long startPos2 = fileLength/5;
			long startPos3 = 2*fileLength/5;
			long startPos4 = 3*fileLength/5;
			long startPos5 = 4*fileLength/5;*/
			
			//最初的开始位置
			long initStartPos1 = 0;
			long initStartPos2 = fileLength/3;
			long initStartPos3 = 2*fileLength/3;
			long initStartPos4 = 3*fileLength/3;
			long initStartPos5 = 4*fileLength/3;
			
			//TODO::这几个数应该是从数据库里取出来的
			long startPos1 = 0;
			long startPos2 = fileLength/5;
			long startPos3 = 2*fileLength/5;
			long startPos4 = 3*fileLength/5;
			long startPos5 = 4*fileLength/5;
			
			//已经传送的长度
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
		
			
			//检查发送线程是否结束
    	    while(client1.isAlive() || client2.isAlive() 
    	    		   || client3.isAlive() || client4.isAlive() || client5.isAlive());
    	   
		}	
		
	}//end Main


}//end class

