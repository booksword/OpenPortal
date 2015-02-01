package Portal.Action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import Portal.Utils.Make_Authenticator;
import Portal.Utils.Make_ChapPassword;
import Portal.Utils.WR;

/**
 * Auth_V2包
 * 
 * @author LeeSon QQ:25901875
 * 
 */
public class Chap_Auth_V2 {

	// 创建ErrorInfo包
	byte[] ErrorInfo = new byte[1];
	// 创建ChapPassword包
	byte[] ChapPassword = new byte[16];
	// 创建连接
	DatagramSocket dataSocket;

	public byte[] Action(String Bas_IP, int bas_PORT, int timeout_Sec,
			String in_username, String in_password, byte[] SerialNo,
			byte[] UserIP, byte[] ReqID, byte[] Challenge, String sharedSecret) {

		byte[] Username = in_username.getBytes();
		byte[] password = in_password.getBytes();
		try {
			ChapPassword = Make_ChapPassword.MK_ChapPwd(ReqID, Challenge,
					password);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte[] authbuff = new byte[4 + Username.length + ChapPassword.length];

		authbuff[0] = (byte) 1;
		authbuff[1] = (byte) (Username.length + 2);
		for (int i = 0; i < Username.length; i++) {
			authbuff[2 + i] = Username[i];
		}
		authbuff[2 + Username.length] = (byte) 4;
		authbuff[3 + Username.length] = (byte) (ChapPassword.length + 2);
		for (int i = 0; i < ChapPassword.length; i++) {
			authbuff[4 + Username.length + i] = ChapPassword[i];
		}

		// 创建Req_Auth包
		byte[] Req_Auth = new byte[36 + Username.length + ChapPassword.length];

		Req_Auth[0] = (byte) 2;
		Req_Auth[1] = (byte) 3;
		Req_Auth[2] = (byte) 0;
		Req_Auth[3] = (byte) 0;
		Req_Auth[4] = SerialNo[0];
		Req_Auth[5] = SerialNo[1];
		Req_Auth[6] = ReqID[0];
		Req_Auth[7] = ReqID[1];
		Req_Auth[8] = UserIP[0];
		Req_Auth[9] = UserIP[1];
		Req_Auth[10] = UserIP[2];
		Req_Auth[11] = UserIP[3];
		Req_Auth[12] = (byte) 0;
		Req_Auth[13] = (byte) 0;
		Req_Auth[14] = (byte) 0;
		Req_Auth[15] = (byte) 2;

		byte[] BBuff = new byte[16];
		for (int i = 0; i < 16; i++) {
			BBuff[i] = Req_Auth[i];
		}
		byte[] Authen = Make_Authenticator.MK_Authen(BBuff, authbuff,
				sharedSecret);
		for (int i = 0; i < 16; i++) {
			Req_Auth[16 + i] = Authen[i];
		}

		for (int i = 0; i < authbuff.length; i++) {
			Req_Auth[32 + i] = authbuff[i];
		}

		System.out.println("REQ Auth" + WR.Getbyte2HexString(Req_Auth));

		try {

			dataSocket = new DatagramSocket();
			// 创建发送数据包并发送给服务器

			DatagramPacket requestPacket = new DatagramPacket(Req_Auth,
					Req_Auth.length, InetAddress.getByName(Bas_IP), bas_PORT);
			dataSocket.send(requestPacket);

			// 接收服务器的数据包
			byte[] ACK_Auth_Data = new byte[32];
			DatagramPacket receivePacket = new DatagramPacket(ACK_Auth_Data, 32);
			// 设置请求超时3秒
			dataSocket.setSoTimeout(timeout_Sec * 1000);
			dataSocket.receive(receivePacket);

			System.out
					.println("ACK Auth" + WR.Getbyte2HexString(ACK_Auth_Data));

			if ((int) (ACK_Auth_Data[14] & 0xFF) == 0) {
				System.out.println("认证成功！！");
				System.out.println("准备发送AFF_ACK_AUTH");
			} else if ((int) (ACK_Auth_Data[14] & 0xFF) == 1) {
				System.out.println("用户认证请求被拒绝");
				ErrorInfo[0] = (byte) 21;
				return ErrorInfo;
			} else if ((int) (ACK_Auth_Data[14] & 0xFF) == 2) {
				System.out.println("用户链接已建立");
				ErrorInfo[0] = (byte) 22;
				return ErrorInfo;
			} else if ((int) (ACK_Auth_Data[14] & 0xFF) == 3) {
				System.out.println("有一个用户正在认证过程中，请稍后再试");
				ErrorInfo[0] = (byte) 23;
				return ErrorInfo;
			} else if ((int) (ACK_Auth_Data[14] & 0xFF) == 4) {
				System.out.println("用户认证失败（发生错误）");
				ErrorInfo[0] = (byte) 24;
				return ErrorInfo;
			}

		} catch (IOException e) {
			System.out.println("用户认证服务器无响应！！！");
			ErrorInfo[0] = (byte) 02;
			return ErrorInfo;
		} finally {
			dataSocket.close();
		}

		// 创建AFF_Ack_Auth包
		byte[] AFF_Ack_Auth_Data = new byte[32];
		// 给AFF_ACK_AUTH包赋值
		AFF_Ack_Auth_Data[0] = (byte) 2;
		AFF_Ack_Auth_Data[1] = (byte) 7;
		AFF_Ack_Auth_Data[2] = (byte) 0;
		AFF_Ack_Auth_Data[3] = (byte) 0;
		AFF_Ack_Auth_Data[4] = SerialNo[0];
		AFF_Ack_Auth_Data[5] = SerialNo[1];
		AFF_Ack_Auth_Data[6] = ReqID[0];
		AFF_Ack_Auth_Data[7] = ReqID[1];
		AFF_Ack_Auth_Data[8] = UserIP[0];
		AFF_Ack_Auth_Data[9] = UserIP[1];
		AFF_Ack_Auth_Data[10] = UserIP[2];
		AFF_Ack_Auth_Data[11] = UserIP[3];
		AFF_Ack_Auth_Data[12] = (byte) 0;
		AFF_Ack_Auth_Data[13] = (byte) 0;
		AFF_Ack_Auth_Data[14] = (byte) 0;
		AFF_Ack_Auth_Data[15] = (byte) 0;

		for (int i = 0; i < 16; i++) {
			BBuff[i] = AFF_Ack_Auth_Data[i];
		}
		byte[] Attrs = new byte[0];
		byte[] BAuthen = Make_Authenticator.MK_Authen(BBuff, Attrs,
				sharedSecret);

		for (int i = 0; i < 16; i++) {
			AFF_Ack_Auth_Data[16 + i] = BAuthen[i];
		}

		System.out.println("AFF_Ack_Auth"
				+ WR.Getbyte2HexString(AFF_Ack_Auth_Data));

		try {

			dataSocket = new DatagramSocket();
			// 创建发送数据包并发送给服务器

			DatagramPacket requestPacket = new DatagramPacket(
					AFF_Ack_Auth_Data, 32, InetAddress.getByName(Bas_IP),
					bas_PORT);
			dataSocket.send(requestPacket);
			System.out.println("发送AFF_Ack_Auth成功！！");

		} catch (IOException e) {
			System.out.println("发送AFF_Ack_Auth出错！！");
			ErrorInfo[0] = (byte) 20;
			return ErrorInfo;
		} finally {
			dataSocket.close();
		}
		ErrorInfo[0] = (byte) 20;
		return ErrorInfo;

	}

}
