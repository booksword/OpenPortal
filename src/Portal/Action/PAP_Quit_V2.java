package Portal.Action;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import Portal.Utils.Make_Authenticator;
import Portal.Utils.WR;

/**
 * Quit_V2包
 * 
 * @author LeeSon QQ:25901875
 * 
 */
public class PAP_Quit_V2 {

	// 创建Req_Quit包
	byte[] Req_Quit = new byte[32];
	byte[] BBuff = new byte[16];

	byte[] ACK_Data = new byte[32];
	// 创建连接
	DatagramSocket dataSocket;

	public int Action(int type, String Bas_IP, int bas_PORT, int timeout_Sec,
			byte[] SerialNo, byte[] UserIP, String sharedSecret) {

		Req_Quit[0] = (byte) 2;
		Req_Quit[1] = (byte) 5;
		Req_Quit[2] = (byte) 1;
		Req_Quit[3] = (byte) 0;
		Req_Quit[4] = SerialNo[0];
		Req_Quit[5] = SerialNo[1];
		Req_Quit[6] = (byte) 0;
		Req_Quit[7] = (byte) 0;
		Req_Quit[8] = UserIP[0];
		Req_Quit[9] = UserIP[1];
		Req_Quit[10] = UserIP[2];
		Req_Quit[11] = UserIP[3];
		Req_Quit[12] = (byte) 0;
		Req_Quit[13] = (byte) 0;
		Req_Quit[14] = (byte) 0;
		Req_Quit[15] = (byte) 0;

		if (type == 0) {

			for (int i = 0; i < 16; i++) {
				BBuff[i] = Req_Quit[i];
			}
			byte[] Attrs = new byte[0];
			byte[] Authen = Make_Authenticator.MK_Authen(BBuff, Attrs,
					sharedSecret);
			for (int i = 0; i < 16; i++) {
				Req_Quit[16 + i] = Authen[i];
			}

			System.out.println("REQ Quit" + WR.Getbyte2HexString(Req_Quit));

			try {

				dataSocket = new DatagramSocket();
				// 创建发送数据包并发送给服务器

				DatagramPacket requestPacket = new DatagramPacket(Req_Quit, 32,
						InetAddress.getByName(Bas_IP), bas_PORT);
				dataSocket.send(requestPacket);

				// 接收服务器的数据包

				DatagramPacket receivePacket = new DatagramPacket(ACK_Data, 32);
				// 设置请求超时3秒
				dataSocket.setSoTimeout(timeout_Sec * 100);
				dataSocket.receive(receivePacket);

				System.out.println("ACK Quit" + WR.Getbyte2HexString(ACK_Data));

			} catch (IOException e) {
				System.out.println("下线请求服务器无响应！！！");
				Ack_Quit_Error(Req_Quit, Bas_IP, bas_PORT, sharedSecret);
				return 10;
			} finally {
				dataSocket.close();
			}

			if ((int) (ACK_Data[14] & 0xFF) != 0) {
				if ((int) (ACK_Data[14] & 0xFF) == 1) {
					System.out.println("请求下线被拒绝");
					return 11;
				} else if ((int) (ACK_Data[14] & 0xFF) == 2) {
					System.out.println("请求下线出错");
					return 12;
				}

			} else {
				System.out.println("请求下线成功！！");
			}
			return 0;
			// ---------------------------------------------------------------------------------------------------------------------------
		} else {

			Req_Quit[14] = (byte) 1;

			for (int i = 0; i < 16; i++) {
				BBuff[i] = Req_Quit[i];
			}
			byte[] Attrs = new byte[0];
			byte[] Authen = Make_Authenticator.MK_Authen(BBuff, Attrs,
					sharedSecret);

			for (int i = 0; i < 16; i++) {
				Req_Quit[16 + i] = Authen[i];
			}

			if (type == 2) {
				System.out.println("发送Auth超时回复报文： "
						+ WR.Getbyte2HexString(Req_Quit));
			} else {
				System.out.println("发送未知超时回复报文： "
						+ WR.Getbyte2HexString(Req_Quit));
			}

			try {

				dataSocket = new DatagramSocket();
				// 创建发送数据包并发送给服务器

				DatagramPacket requestPacket = new DatagramPacket(Req_Quit, 32,
						InetAddress.getByName(Bas_IP), bas_PORT);
				dataSocket.send(requestPacket);
				if (type == 2) {
					System.out.println("发送Auth超时回复报文成功！！！！");
				} else {
					System.out.println("发送未知超时回复报文成功！！！！");
				}

			} catch (IOException e) {
				if (type == 2) {
					System.out.println("发送Auth超时回复报文失败！！！！");
				} else {
					System.out.println("发送未知超时回复报文失败！！！！");
				}
				return 0;
			} finally {
				dataSocket.close();
			}
			return 0;
		}

	}

	private void Ack_Quit_Error(byte[] Req_Quit, String Bas_IP, int bas_PORT,
			String sharedSecret) {
		Req_Quit[14] = (byte) 1;
		for (int i = 0; i < 16; i++) {
			BBuff[i] = Req_Quit[i];
		}

		byte[] Attrs = new byte[0];
		byte[] Authen = Make_Authenticator
				.MK_Authen(BBuff, Attrs, sharedSecret);

		for (int i = 0; i < 16; i++) {
			Req_Quit[16 + i] = Authen[i];
		}

		System.out.println("发送下线请求超时回复报文: " + WR.Getbyte2HexString(Req_Quit));
		DatagramSocket dataSocket = null;
		try {

			dataSocket = new DatagramSocket();
			// 创建发送数据包并发送给服务器

			DatagramPacket requestPacket = new DatagramPacket(Req_Quit, 32,
					InetAddress.getByName(Bas_IP), bas_PORT);
			dataSocket.send(requestPacket);

		} catch (IOException e) {
			System.out.println("下线请求超时回复报文发送失败！！！");
		} finally {
			dataSocket.close();
		}
	}
}
