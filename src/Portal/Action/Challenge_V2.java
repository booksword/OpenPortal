package Portal.Action;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import Portal.Utils.Make_Authenticator;
import Portal.Utils.WR;
import Portal.Utils.Write2Log;
/**
 * Challenge_V2包
 * @author LeeSon  QQ:25901875
 *
 */
public class Challenge_V2 {

	
	// 创建ErrorInfo包
	byte[] ErrorInfo = new byte[1];
	// 创建Req_Challenge包
	byte[] BBuff = new byte[16];

	// 创建连接
	DatagramSocket dataSocket;
	
	public byte[] Action(String Bas_IP, int bas_PORT,int timeout_Sec,byte[] buff,String sharedSecret) {
		// 给Req_Challenge包赋值
		for (int i = 0; i < BBuff.length; i++) {
			BBuff[i] = buff[i];
		}
		BBuff[1] = (byte) 1;
		byte[] Attrs=new byte[0];
		byte[] Authen=Make_Authenticator.MK_Authen(BBuff, Attrs, sharedSecret);
		byte[] Req_Challenge=new byte[32];
		for (int i = 0; i < BBuff.length; i++) {
			Req_Challenge[i] = BBuff[i];
		}
		for (int i = 0; i < Authen.length; i++) {
			Req_Challenge[BBuff.length+i] = Authen[i];
		}
		
		

		System.out.println("REQ Challenge" + WR.Getbyte2HexString(Req_Challenge));
		Write2Log.Wr2Log("REQ Challenge" + WR.Getbyte2HexString(Req_Challenge));

		try {

			dataSocket = new DatagramSocket();
			// 创建发送数据包并发送给服务器

			DatagramPacket requestPacket = new DatagramPacket(Req_Challenge,
					Req_Challenge.length, InetAddress.getByName(Bas_IP), bas_PORT);
			dataSocket.send(requestPacket);
		
			// 接收服务器的数据包
			byte[] ACK_Data = new byte[50];
			DatagramPacket receivePacket = new DatagramPacket(ACK_Data,
					ACK_Data.length);
			//设置请求超时3秒
			dataSocket.setSoTimeout(timeout_Sec*1000);
			dataSocket.receive(receivePacket);
			
			byte[] ACK_Challenge_Data = new byte[receivePacket.getLength()];
			for (int i = 0; i < receivePacket.getLength(); i++) {
				ACK_Challenge_Data[i] = ACK_Data[i];
			}
			System.out
					.println("ACK Challenge" + WR.Getbyte2HexString(ACK_Challenge_Data));
			Write2Log.Wr2Log("ACK Challenge" + WR.Getbyte2HexString(ACK_Challenge_Data));
			
			if ((int)(ACK_Challenge_Data[14] & 0xFF)==0){
				System.out.println("请求Challenge成功！！");
				System.out.println("准备发送REQ Auth");
				Write2Log.Wr2Log("请求Challenge成功！！");
				Write2Log.Wr2Log("准备发送REQ Auth");
				return ACK_Challenge_Data;
			}else if((int)(ACK_Challenge_Data[14] & 0xFF)==1){
				System.out.println("请求Challenge被拒绝");
				Write2Log.Wr2Log("请求Challenge被拒绝");
				ErrorInfo[0] = (byte) 11;
				return ErrorInfo;
			}else if((int)(ACK_Challenge_Data[14] & 0xFF)==2){
				System.out.println("此链接已建立");
				Write2Log.Wr2Log("此链接已建立");
				ErrorInfo[0] = (byte) 12;
				return ErrorInfo;
			}else if((int)(ACK_Challenge_Data[14] & 0xFF)==3){
				System.out.println("有一个用户正在认证过程中，请稍后再试");
				Write2Log.Wr2Log("有一个用户正在认证过程中，请稍后再试");
				ErrorInfo[0] = (byte) 13;
				return ErrorInfo;
			}else if((int)(ACK_Challenge_Data[14] & 0xFF)==4){
				System.out.println("用户请求Challenge失败（发生错误）");
				Write2Log.Wr2Log("用户请求Challenge失败（发生错误）");
				ErrorInfo[0] = (byte) 14;
				return ErrorInfo;
			}else{
				System.out.println("用户请求Challenge失败（发生未知错误）");
				Write2Log.Wr2Log("用户请求Challenge失败（发生未知错误）");
				ErrorInfo[0] = (byte) 14;
				return ErrorInfo;
			}

		} catch (IOException e) {
			System.out.println("Challenge挑战请求超时！！！");
			Write2Log.Wr2Log("Challenge挑战请求超时！！！");
			ErrorInfo[0] = (byte) 01;
			return ErrorInfo;
		} finally{
			dataSocket.close();
		}
		
		
	}
}
