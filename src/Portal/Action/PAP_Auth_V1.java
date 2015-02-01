package Portal.Action;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import Portal.Utils.WR;

/**
 * Auth_V1包
 * 
 * @author LeeSon QQ:25901875
 * 
 */
public class PAP_Auth_V1 {

	// 创建连接
	DatagramSocket dataSocket;

	public int Action(String Bas_IP, int bas_PORT, int timeout_Sec,
			String in_username, String in_password, byte[] SerialNo,
			byte[] UserIP) {

		byte[] Username = in_username.getBytes();
		byte[] password = in_password.getBytes();

		// 创建Req_Auth包
		byte[] Req_Auth = new byte[20 + Username.length + password.length];

		Req_Auth[0] = (byte) 1;
		Req_Auth[1] = (byte) 3;
		Req_Auth[2] = (byte) 1;
		Req_Auth[3] = (byte) 0;
		Req_Auth[4] = SerialNo[0];
		Req_Auth[5] = SerialNo[1];
		Req_Auth[6] = (byte) 0;
		Req_Auth[7] = (byte) 0;
		Req_Auth[8] = UserIP[0];
		Req_Auth[9] = UserIP[1];
		Req_Auth[10] = UserIP[2];
		Req_Auth[11] = UserIP[3];
		Req_Auth[12] = (byte) 0;
		Req_Auth[13] = (byte) 0;
		Req_Auth[14] = (byte) 0;
		Req_Auth[15] = (byte) 2;

		Req_Auth[16] = (byte) 1;
		Req_Auth[17] = (byte) (Username.length + 2);
		for (int i = 0; i < Username.length; i++) {
			Req_Auth[18 + i] = Username[i];
		}
		Req_Auth[18 + Username.length] = (byte) 2;
		Req_Auth[19 + Username.length] = (byte) (password.length + 2);
		for (int i = 0; i < password.length; i++) {
			Req_Auth[20 + Username.length + i] = password[i];
		}

		System.out.println("REQ Auth" + WR.Getbyte2HexString(Req_Auth));

		try {

			dataSocket = new DatagramSocket();
			// 创建发送数据包并发送给服务器

			DatagramPacket requestPacket = new DatagramPacket(Req_Auth,
					Req_Auth.length, InetAddress.getByName(Bas_IP), bas_PORT);
			dataSocket.send(requestPacket);

			// 接收服务器的数据包
			byte[] ACK_Data = new byte[16];
			DatagramPacket receivePacket = new DatagramPacket(ACK_Data,
					ACK_Data.length);
			// 设置请求超时3秒
			dataSocket.setSoTimeout(timeout_Sec * 1000);
			dataSocket.receive(receivePacket);

			System.out.println("ACK Auth" + WR.Getbyte2HexString(ACK_Data));

			if ((int) (ACK_Data[14] & 0xFF) == 0) {
				System.out.println("认证成功！！");
				System.out.println("准备发送AFF_ACK_AUTH");
			} else if ((int) (ACK_Data[14] & 0xFF) == 1) {
				System.out.println("用户认证请求被拒绝");
				return 21;
			} else if ((int) (ACK_Data[14] & 0xFF) == 2) {
				System.out.println("用户链接已建立");
				return 22;
			} else if ((int) (ACK_Data[14] & 0xFF) == 3) {
				System.out.println("有一个用户正在认证过程中，请稍后再试");
				return 23;
			} else if ((int) (ACK_Data[14] & 0xFF) == 4) {
				System.out.println("用户认证失败（发生错误）");
				return 24;
			}

		} catch (IOException e) {
			System.out.println("用户认证服务器无响应！！！");
			new PAP_Quit_V1().Action(2, Bas_IP, bas_PORT, timeout_Sec,
					SerialNo, UserIP);
			return 02;
		} finally {
			dataSocket.close();
		}

		// 创建AFF_Ack_Auth包
		byte[] AFF_Ack_Auth_Data = new byte[16];

		AFF_Ack_Auth_Data[0] = (byte) 1;
		AFF_Ack_Auth_Data[1] = (byte) 7;
		AFF_Ack_Auth_Data[2] = (byte) 1;
		AFF_Ack_Auth_Data[3] = (byte) 0;
		AFF_Ack_Auth_Data[4] = SerialNo[0];
		AFF_Ack_Auth_Data[5] = SerialNo[1];
		AFF_Ack_Auth_Data[6] = (byte) 0;
		AFF_Ack_Auth_Data[7] = (byte) 0;
		AFF_Ack_Auth_Data[8] = UserIP[0];
		AFF_Ack_Auth_Data[9] = UserIP[1];
		AFF_Ack_Auth_Data[10] = UserIP[2];
		AFF_Ack_Auth_Data[11] = UserIP[3];
		AFF_Ack_Auth_Data[12] = (byte) 0;
		AFF_Ack_Auth_Data[13] = (byte) 0;
		AFF_Ack_Auth_Data[14] = (byte) 0;
		AFF_Ack_Auth_Data[15] = (byte) 0;

		System.out.println("AFF_Ack_Auth"
				+ WR.Getbyte2HexString(AFF_Ack_Auth_Data));

		try {

			dataSocket = new DatagramSocket();
			// 创建发送数据包并发送给服务器

			DatagramPacket requestPacket = new DatagramPacket(
					AFF_Ack_Auth_Data, AFF_Ack_Auth_Data.length,
					InetAddress.getByName(Bas_IP), bas_PORT);
			dataSocket.send(requestPacket);
			System.out.println("发送AFF_Ack_Auth成功！！");
			return 0;
		} catch (IOException e) {
			System.out.println("发送AFF_Ack_Auth出错！！");
			return 0;
		} finally {
			dataSocket.close();
		}

	}

}
