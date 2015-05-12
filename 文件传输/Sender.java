/** 
 *  传输文件，传输端
 *  @author lihao
 */ 

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Sender {


	/**
	 * main函数
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		//发送方的端口号
		//InetAddress senderHost = InetAddress.getByName("localhost");
	    int senderPort = 1234;
	    
		//接收方的IP及端口号
		InetAddress receiverHost = InetAddress.getByName("localhost");
		int receiverPort = 1256;
		
		//数据包socket，UDP发送
		DatagramSocket	mySocket = new DatagramSocket();           
            
		//需要传输的文件
        String filePath = "F:\\gd\\hd.jpg";
        
        //获取文件对象
        File file = new File(filePath);
        if(!file.exists()) file.createNewFile();
        	
    	
    	//获取文件大小
        long fileLength = file.length();
        //UDP传文件大小到客户端
        ByteArrayOutputStream baos=new ByteArrayOutputStream();  
        DataOutputStream dos=new DataOutputStream(baos);  
        dos.writeLong(fileLength);  
          
        byte[] bufferLength=baos.toByteArray();
    	DatagramPacket datagramLength = new DatagramPacket(bufferLength, bufferLength.length,
    			receiverHost, receiverPort);
    	//发送数据包
    	mySocket.send(datagramLength);
    	
        //获取文件名
        String[] fileArray = filePath.split("\\\\");
        int fileNameIndex = fileArray.length;
        String fileName = fileArray[fileNameIndex-1];
        //UDP传文件名到客户端
    	byte[] bufferName =fileName.getBytes(); 
    	DatagramPacket datagramName = new DatagramPacket(bufferName, bufferName.length,
    			receiverHost, receiverPort);
    	//发送数据包
    	mySocket.send(datagramName);
    	  	
    	//关闭socket
        mySocket.close();
        
        //接收线程的数目
      	int threadNum = 0;
      		
      	if(fileLength<20971520){
      		threadNum = 1;
      	}else if(fileLength<104857600){
      		threadNum = 3;
      	}else{
      		threadNum = 5;
      	}
        
        //新建连接Socket	
	    ServerSocket connectionSocket = new ServerSocket(senderPort);
	    
	    //Socket的list
	    List<Server> serverList = new ArrayList<Server>();
	    
	    //新建的线程数目
        int existingThreadNum = 0;
	    
        //接收连接，新建线程
        while(existingThreadNum < threadNum){
        		    
        	//新建数据socket
        	Socket dataSocket = connectionSocket.accept();
        	
        	//新建发送线程
    	    Server server = new Server(dataSocket,file);
    	    
    	    existingThreadNum++;
    	    
    	    //启动线程
    	    if(!server.isAlive())
    	    	server.start();
    	    
    	    //将Server加入到list里面去
    	    serverList.add(server);
    	      	
        }
        
    	//检查文件传输是否完成
	    while(!checkServerState(serverList));
        
	    connectionSocket.close();
    
	}

	private static boolean checkServerState(List<Server> serverList) {
		//线程状态
		boolean complete = false; 
		
		//检查
      	for(int i = 0; i < serverList.size(); i++){
    	    if(serverList.get(i).isAlive()){
    	    	complete = false;
    	    	break;
    	    }
    	    else{
    	    	complete = true;
    	    }
    		  
        }
		
      	return complete;
	}

}
