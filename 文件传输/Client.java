/** 
 *  �����ļ��������߳�
 *  @author lihao
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
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
 * �ļ����ն�
 */
public class Client extends Thread {

	/* ����Socket* */
	private Socket dataSocket;

	/* �ļ���С* */
	private long fileLength;

	/* �ļ�* */
	private File receFile;

	/* �����ļ���ȡλ��* */
	private long startPos;

	/* md5�����* */
	private boolean md5Check;
	
	/* �ļ��Ƿ�ϵ�**/
    private boolean fileInterrupt = false;

	/* ��Կ* */
	private String key = "lele";

	/**
	 * ���캯�����������
	 */
	public Client(Socket dataSocket, File receFile, long startPos,
			long fileLength) {

		this.dataSocket = dataSocket;
		this.receFile = receFile;
		this.startPos = startPos;
		System.out.println(startPos);
		this.fileLength = fileLength;

	}

	/**
	 * run�����������ļ�
	 */
	@Override
	public void run() {

		// OutputStream
		OutputStream outStream = null;
		try {
			outStream = dataSocket.getOutputStream();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		DataOutputStream dos = new DataOutputStream(outStream);

		// ��������շ��ļ������Ϣ
		try {
			sendFileInfo(dos);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// ������
		InputStream inStream = null;
		try {
			inStream = dataSocket.getInputStream();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// data�����
		DataInputStream dis = new DataInputStream(inStream);

		// �����ļ�
		try {
			readFileStream(inStream, dis);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			dataSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * ��ȡ�ļ������������浽�ļ���
	 * 
	 * @throws Exception
	 */
	private void readFileStream(InputStream inStream, DataInputStream dis){
		
		//TODO::�Ѿ���ȡ�ĳ��� ,total������һ����������ʾ�������
		long total = 0;
		
		try{
			// �ļ���ȡλ������
			RandomAccessFile fos = new RandomAccessFile(receFile, "rw");

			// ��λ�ļ�ָ�뵽 nPos λ��
			try {
				// �ļ�ָ���ƶ����Ǹ�λ��
				fos.seek(startPos);
			} catch (IOException e) {
				e.printStackTrace();
			}

			byte[] bufFile = new byte[1024 * 1024];

			MessageDigest messageDigest = MessageDigest.getInstance("MD5");

			while (total < fileLength) {
			

				// ��ȡ��������md5ֵ
				String md5Rece = dis.readUTF();

				// ������ȡ���Ͷ�һ�η���������
				int length = dis.readInt();
				int len = 0;
				int off = 0;
				do {
					len = inStream.read(bufFile, off, length - len); // ��������
					off += len;
				} while (off < length);

				messageDigest.update(Arrays.copyOfRange(bufFile, 0, length));

				// �����յ����ֽ����������md5ֵ
				String md5 = parseByte2HexStr(messageDigest.digest());

				// ���ǰ��md5ֵ
				if (md5Rece.equals(md5)) {
					md5Check = true;
				} else {
					md5Check = false;
					System.out.println("md5�������� " + md5Check);
					break;
				}

				byte[] AES = aesDecrypt(Arrays.copyOfRange(bufFile, 0, length), key);

				total += AES.length;
				if (len != -1) {
					fos.write(AES, 0, AES.length); // д��Ӳ���ļ�

				} else {
					break;
				}
				
				//���շ������Ͽ�����
				if(fileInterrupt == true){
					//TODO::��һ�ο�ʼ��λ�ã����λ��Ӧ�ô�����
					startPos += total;
					System.out.println("���շ������Ͽ����ӣ��´ο�ʼλ�ã� "+startPos);					
					break;
				}
			}

			fos.close();
		}catch(EOFException e){
			System.out.println("���Ͷ���ͣ����");
			//TODO::��һ�ο�ʼ��λ�ã����λ��Ӧ�ô�����
			startPos += total;
			System.out.println("���Ͷ���ͣ���ͣ��´ο�ʼλ�ã� "+startPos);					
		
		}catch(SocketException e){
			System.out.println("���Ͷ�socket����");
			//TODO::��һ�ο�ʼ��λ�ã����λ��Ӧ�ô�����
			startPos += total;
			System.out.println("���Ͷ�socket���ˣ��´ο�ʼλ�ã� "+startPos);					
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * �����ļ������Ϣ
	 */
	private void sendFileInfo(DataOutputStream dos) throws IOException {

		// �����ļ���С
		dos.writeLong(fileLength);
		dos.flush();

		// �����ļ���ʼλ��
		dos.writeLong(startPos);
		dos.flush();

	}

	/**
	 * AES����
	 */
	public static byte[] aesDecrypt(byte[] encryptBytes, String decryptKey)
			throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(128, new SecureRandom(decryptKey.getBytes()));

		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(kgen.generateKey()
				.getEncoded(), "AES"));
		return cipher.doFinal(encryptBytes);

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

}
