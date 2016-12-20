package com.liutao.liurobot_android.entity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.os.Environment;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

public class ServerSide extends Thread {
	private static final int HOST_PORT = 8250;
	Socket s = null;
	private static final String FILE_PATH = Environment.getExternalStorageDirectory().getPath();
	public static final int TEXTRESULT = 110;
	public static final int CATERGERY_IMAGE = 120;
	ServerSocket ss = null;
	// 0不成功 1成功
	public int isFileSaveOK = 0;

	@Override
	public void run() {
		try {
			ss = new ServerSocket(HOST_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (true) {
			startReceive(ss);
		}
		// startSend();
	}

	private void startSend() {
		try {
			ServerSocket ss = new ServerSocket(HOST_PORT);
			while (true) {
				// String filePath =
				// Environment.getExternalStorageDirectory().getPath() +
				// "/kingroot.apk";

				// System.out.println("鏂囦欢闀垮害:" + (int) file.length());
				// Log.i("androidserverservicedemo", "鏂囦欢闀垮害:" + (int)
				// file.length());
				s = ss.accept();
				DataInputStream dis = new DataInputStream(new BufferedInputStream(s.getInputStream()));
				dis.readByte();
				// DataInputStream fis = new DataInputStream(new
				// BufferedInputStream(new FileInputStream(filePath)));
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				// dos.writeUTF(file.getName());
				// dos.flush();
				// dos.writeLong((long) file.length());
				// dos.flush();
				int bufferSize = 8192;
				// byte[] buf = new byte[bufferSize];
				// while (true) {
				// int read = 0;
				// if (fis != null) {
				// read = fis.read(buf);
				// }
				// if (read == -1) {
				// break;
				// }
				// dos.write(buf, 0, read);
				// }
				if (!result_.equals("")) {
					byte[] bytes = result_.getBytes();
					dos.write(bytes);
					dos.flush();

				}

				// 娉ㄦ剰鍏抽棴socket閾炬帴鍝︼紝涓嶇劧瀹㈡埛绔細绛夊緟server鐨勬暟鎹繃鏉ワ紝
				// 鐩村埌socket瓒呮椂锛屽鑷存暟鎹笉瀹屾暣銆�
				// fis.close();
				s.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	String[] SendCommandList = new String[50];
	String[] SendCommandList_Flag = new String[50];
	int SendCommandList_Length = 0;
	private synchronized String[] sendCommand(String command, String flag){
		String[] tmpString = new String[2];
		tmpString[0] = "";
		tmpString[1] = "";
		
		if (!command.equals("")){
			SendCommandList[SendCommandList_Length] = command;
			SendCommandList_Flag[SendCommandList_Length] = flag;
			SendCommandList_Length ++;
		}else{
			if (SendCommandList[0] != null){
				tmpString[0] = SendCommandList[0];
				tmpString[1] = SendCommandList_Flag[0];
				
				if (SendCommandList_Length > 1){
					int i;
					for (i = 1; i < SendCommandList_Length; i ++){
						SendCommandList[i - 1] = SendCommandList[i];
						SendCommandList_Flag[i - 1] = SendCommandList_Flag[i];
						SendCommandList_Flag[i] = null;
						SendCommandList[i] = null;
					}		
					SendCommandList_Length --;
				}else{
					SendCommandList_Flag[0] = null;
					SendCommandList[0] = null;
					SendCommandList_Length = 0;
				}
				
			}			
		}
		return  tmpString;
	}
	
	
	String tmp = "";
	public String result_ = "";
	int rec_count = 0;
	//public String Send_Command = "";
	//public boolean send_isfile;
	byte[] All_Data = null;
	File file = null;
	DataOutputStream fileOut = null;
	String fileName ="";
	public boolean setCoordOK = false;
	public void startReceive(ServerSocket ss) {
		DataInputStream dis = null;
		DataOutputStream dos = null;
		// inputStream = cs.getMessageStream();
		try {
			if(ss!=null){
			s = ss.accept();
			String savePath = FILE_PATH;
			int bufferSize = 4096;
			byte[] buf = new byte[bufferSize];
			dis = new DataInputStream(new BufferedInputStream(s.getInputStream()));
			dos = new DataOutputStream(s.getOutputStream());
			// savePath += dis.readUTF();
			Log.d("AndroidClient", "@@@savePath" + savePath);
			int read = 0;
			int passedlen = 0;
			// byte xxxc = dis.readByte();
			// Log.d("AndoridClient", "readByte :" + xxxc+"");
			String str_head = "";
			byte[] bytes = new byte[16];
			String Send_Str = "QQQQPPPPWWWW";
			int Data_Len = 0;

			rec_count++;
			s.setSoTimeout(3000);
			int tmp_len = dis.read(bytes, 0, 16);
			if (tmp_len == 16) {
				str_head = new String(bytes, 0, 12);

				Log.i("str_head", str_head);

				Data_Len = (bytes[12] << 24) & 0xFF000000;
				Data_Len |= (bytes[13] << 16) & 0x00FF0000;
				Data_Len |= (bytes[14] << 8) & 0x0000FF00;
				Data_Len |= (bytes[15]) & 0x000000FF;

				Log.i("Data_Len", Data_Len + "");

				switch (str_head) {

				case "AAAALLLLSSSS": /* 指令返回文件 */
					Log.i("aaaaaaa", "进入了AAALLLLSSSS，头命令式：AAAALLLLSSS");
					isFileSaveOK = 0;
					if(!fileName.equals("")){
						file = new File(FILE_PATH+"/"+fileName+".png");
					
					// 文件输入
					try {
						if (!file.exists()) {
							file.createNewFile();
						} else {
							file.delete();
							file.createNewFile();
						}

					} catch (IOException e) {
						e.printStackTrace();
					}
					}
					fileOut = new DataOutputStream(
							new BufferedOutputStream(new BufferedOutputStream(new FileOutputStream(file))));
					do {
						passedlen += read;
						fileOut.write(buf, 0, read);
						fileOut.flush();

						Log.i("passedlen", passedlen + "");
						if (passedlen == Data_Len)
							break;
					} while ((read = dis.read(buf)) != -1);
					isFileSaveOK = 1;
					if (fileOut != null) {
						try {
							fileOut.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					break;
				case "ZZZZMMMMXXXX": /* 指令返回字符 */
					result_ = "";
					do {
						passedlen += read;

						tmp = new String(buf, 0, read);
						result_ += tmp;
						Log.i("passedlen", passedlen + "");
						Log.i("result_", result_);
						if (passedlen == Data_Len)
							break;

					} while ((read = dis.read(buf)) != -1);
					// mHandler.sendEmptyMessage(TEXTRESULT);
					if (result_.equals("@SetCoord:OK!"))
					{
						setCoordOK = true;
					
					}					
					break;

				case "QQQQPPPPWWWW": /* 询问是否有指令发送 */
				default:
					do {

						if (passedlen == Data_Len)
							break;
						// tmp = new String(buf, 0, read);
						// result_ += tmp;

						passedlen += read;
						Log.i("passedlen", passedlen + "");
					} while ((read = dis.read(buf)) != -1);
					
					String[] sendComm = new String[2];
					sendComm = sendCommand("", "");
					
					Log.i("cccccc", "sendComm:  "+ sendComm[0]);
					if (!sendComm[0].equals("")) /* 没有要发送的指令 */
					{
						String tmp_str;
						if (sendComm[1].equals("1"))
							//tmp_str = "AAAALLLLSSSS" + Send_Command;
							tmp_str = "AAAALLLLSSSS" + sendComm[0];
						else
							//tmp_str = "ZZZZMMMMXXXX" + Send_Command;
							tmp_str = "ZZZZMMMMXXXX" + sendComm[0];

						//send_isfile = false;
						Send_Str = tmp_str; /* 有指令要发送 */
						//Send_Command = "";
						Log.i("cccccc", "执行了!!!!"+Send_Str);
					}

					break;
				}

			}
			dos.write(Send_Str.getBytes());
			dos.flush();

			Log.d("AndroidClient", "====发送了  " + Send_Str+"   指令");
			// Log.d("AndoridClient",
			// "=============================================================" +
			// passedlen);
			// Log.d("AndroidClient", "======" + savePath);

			dos.close();
			dis.close();
			s.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (s != null) {
				try {
					s.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			}
		
	}

	public void setSendCommands(String command, String flag){
		sendCommand(command, flag);
	}
	public void setFileName(String str){
		fileName = str;
	}

}
