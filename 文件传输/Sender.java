/** 
 *  �����ļ��������
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
	 * main����
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		//���ͷ��Ķ˿ں�
		//InetAddress senderHost = InetAddress.getByName("localhost");
	    int senderPort = 1234;
	    
		//���շ���IP���˿ں�
		InetAddress receiverHost = InetAddress.getByName("localhost");
		int receiverPort = 1256;
		
		//���ݰ�socket��UDP����
		DatagramSocket	mySocket = new DatagramSocket();           
            
		//��Ҫ������ļ�
        String filePath = "F:\\gd\\hd.jpg";
        
        //��ȡ�ļ�����
        File file = new File(filePath);
        if(!file.exists()) file.createNewFile();
        	
    	
    	//��ȡ�ļ���С
        long fileLength = file.length();
        //UDP���ļ���С���ͻ���
        ByteArrayOutputStream baos=new ByteArrayOutputStream();  
        DataOutputStream dos=new DataOutputStream(baos);  
        dos.writeLong(fileLength);  
          
        byte[] bufferLength=baos.toByteArray();
    	DatagramPacket datagramLength = new DatagramPacket(bufferLength, bufferLength.length,
    			receiverHost, receiverPort);
    	//�������ݰ�
    	mySocket.send(datagramLength);
    	
        //��ȡ�ļ���
        String[] fileArray = filePath.split("\\\\");
        int fileNameIndex = fileArray.length;
        String fileName = fileArray[fileNameIndex-1];
        //UDP���ļ������ͻ���
    	byte[] bufferName =fileName.getBytes(); 
    	DatagramPacket datagramName = new DatagramPacket(bufferName, bufferName.length,
    			receiverHost, receiverPort);
    	//�������ݰ�
    	mySocket.send(datagramName);
    	  	
    	//�ر�socket
        mySocket.close();
        
        //�����̵߳���Ŀ
      	int threadNum = 0;
      		
      	if(fileLength<20971520){
      		threadNum = 1;
      	}else if(fileLength<104857600){
      		threadNum = 3;
      	}else{
      		threadNum = 5;
      	}
        
        //�½�����Socket	
	    ServerSocket connectionSocket = new ServerSocket(senderPort);
	    
	    //Socket��list
	    List<Server> serverList = new ArrayList<Server>();
	    
	    //�½����߳���Ŀ
        int existingThreadNum = 0;
	    
        //�������ӣ��½��߳�
        while(existingThreadNum < threadNum){
        		    
        	//�½�����socket
        	Socket dataSocket = connectionSocket.accept();
        	
        	//�½������߳�
    	    Server server = new Server(dataSocket,file);
    	    
    	    existingThreadNum++;
    	    
    	    //�����߳�
    	    if(!server.isAlive())
    	    	server.start();
    	    
    	    //��Server���뵽list����ȥ
    	    serverList.add(server);
    	      	
        }
        
    	//����ļ������Ƿ����
	    while(!checkServerState(serverList));
        
	    connectionSocket.close();
    
	}

	private static boolean checkServerState(List<Server> serverList) {
		//�߳�״̬
		boolean complete = false; 
		
		//���
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
