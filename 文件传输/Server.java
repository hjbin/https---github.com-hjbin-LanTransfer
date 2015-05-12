/** 
 *  �����ļ��������߳�
 *  @author lihao
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

/**
 * �ļ����Ͷ�
 */
public class Server extends Thread {

	/* ����socket**/
	private Socket dataSocket;

	/* �ļ�**/
	private File file;

	/* �ļ�����**/
	private long fileLength;

	/* �ļ���ȡ��ʼλ��**/
	private long startPos;
	
	/* �ļ��Ƿ�ϵ�**/
    private boolean fileInterrupt = false;
	
	/* ��Կ**/
	private String key = "lele";

	/**
	 * ���캯�����������
	 */
	public Server(Socket dataSocket, File file) {

		this.dataSocket = dataSocket;
		this.file = file;

	}

	/**
	 * run�����������ļ�
	 */
	@Override
	public void run() {

		// ������
		InputStream inStream = null;
		try {
			inStream = dataSocket.getInputStream();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// data�����
		DataInputStream dis = new DataInputStream(inStream);

		// �����ļ������Ϣ
		try {
			readFileInfo(dis);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// ���ļ�
		OutputStream outStream = null;
		try {
			outStream = dataSocket.getOutputStream();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		DataOutputStream dos = new DataOutputStream(outStream);
		// �����ļ�
		try {
			sendFile(outStream, dos);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * �����ļ�
	 * 
	 * @throws Exception
	 */
	private void sendFile(OutputStream outStream, DataOutputStream dos){

		try{
			// ���ԴӶ����ֽڿ�ʼд���ļ���
			RandomAccessFile fis = null;
			fis = new RandomAccessFile(file, "rw");
			fis.seek(startPos);

			byte[] bufFile = new byte[1024];
			int len = 0;

			//TODO::�Ѿ�������ֽ��� ,total������һ����������ʾ�������
			long total = 0;
			

			// MD5����
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");

			while (total < fileLength) {

				len = fis.read(bufFile);

				// �Ѿ���ȡ���ֽ���
				total += len;

				// ��ȡ���ֽ���������Χ
				if (total > fileLength) {
					//total�ص�֮ǰ��λ��
					total -= len;

					fis.seek(startPos + total);

					int remainingLength = (int) (fileLength - total);

					// ������ȡ����һ����
					int readLen = 0;
					int off = 0;
					do {
						readLen = fis.read(bufFile, off, remainingLength - readLen); // ��������
						off += readLen;
					} while (off < remainingLength);

					len = remainingLength;
					total = fileLength;
				}

				if (len != -1) {
					byte[] AesBufFile = aesEncrypt(
							Arrays.copyOfRange(bufFile, 0, len), key);

					messageDigest.update(AesBufFile);
					String md5 = parseByte2HexStr(messageDigest.digest());
					dos.writeUTF(md5);

					int length = AesBufFile.length;

					dos.writeInt(length);
					
					// ����Ӳ���϶�ȡ���ֽ�����д��socket�����
					outStream.write(AesBufFile, 0, length); 
				} else {
					break;
				}	
				
				//���ͷ������Ͽ�����
				if(fileInterrupt){
					//��һ�ο�ʼ��λ��
					//startPos += total;
					//System.out.println("���ͷ������Ͽ�����,��һ�ο�ʼλ�ã� "+startPos);
					
					break;
				}
				
			}

			fis.close();
			dataSocket.close();
			
		}catch(SocketException e){
			System.out.println("���ն���ͣ����");
			
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	/**
	 * AES����
	 */
	public static byte[] aesEncrypt(byte[] fileBytes, String encryptKey)
			throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(128, new SecureRandom(encryptKey.getBytes()));

		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(kgen.generateKey()
				.getEncoded(), "AES"));

		return cipher.doFinal(fileBytes);
	}

	/**
	 * �ֽ�����ת����16���Ƶ��ַ���
	 */
	public static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * ��ȡ�ļ������Ϣ
	 */
	private void readFileInfo(DataInputStream dis) throws IOException {

		fileLength = dis.readLong();
		startPos = dis.readLong();

	}

}
