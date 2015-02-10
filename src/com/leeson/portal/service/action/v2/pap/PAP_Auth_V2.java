package com.leeson.portal.service.action.v2.pap;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.log4j.Logger;

import com.leeson.portal.service.utils.Authenticator;
import com.leeson.portal.service.utils.PortalUtil;

/**
 * Auth_V2包
 * 
 * @author LeeSon QQ:25901875
 * 
 */
public class PAP_Auth_V2 {
	
	private static Logger log = Logger.getLogger(PAP_Auth_V2.class);
	
	// 创建连接
	public static boolean auth(String Bas_IP, int bas_PORT, int timeout_Sec,String in_username, String in_password, byte[] SerialNo,byte[] UserIP, String sharedSecret) {
		byte[] Username = in_username.getBytes();
		byte[] password = in_password.getBytes();
		byte[] authbuff = new byte[4 + Username.length + password.length];
		authbuff[0] = (byte) 1;
		authbuff[1] = (byte) (Username.length + 2);
		for (int i = 0; i < Username.length; i++) {
			authbuff[2 + i] = Username[i];
		}
		authbuff[2 + Username.length] = (byte) 2;
		authbuff[3 + Username.length] = (byte) (password.length + 2);
		for (int i = 0; i < password.length; i++) {
			authbuff[4 + Username.length + i] = password[i];
		}
		return PAP_Auth_V2.Req_Auth(Username, password, SerialNo, UserIP, authbuff, timeout_Sec, Bas_IP, bas_PORT, sharedSecret);
	}
	
	// 构建Req_Auth包
	public static boolean Req_Auth(byte[] Username,byte[] password, byte[] SerialNo,byte[] UserIP, byte[] authbuff,int timeout_Sec,String Bas_IP,int bas_PORT,String sharedSecret) {
		DatagramSocket dataSocket = null;
		// 创建Req_Auth包
		byte[] Req_Auth = new byte[32 + 4 + Username.length + password.length];
		Req_Auth[0] = (byte) 2;
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
		byte[] BBuff = new byte[16];
		for (int i = 0; i < 16; i++) {
			BBuff[i] = Req_Auth[i];
		}
		byte[] Authen = Authenticator.MK_Authen(BBuff, authbuff,sharedSecret);
		for (int i = 0; i < 16; i++) {
			Req_Auth[16 + i] = Authen[i];
		}
		for (int i = 0; i < authbuff.length; i++) {
			Req_Auth[32 + i] = authbuff[i];
		}
		log.info("REQ Auth" + PortalUtil.Getbyte2HexString(Req_Auth));
		try {
			dataSocket = new DatagramSocket();
			// 创建发送数据包并发送给服务器
			DatagramPacket requestPacket = new DatagramPacket(Req_Auth,Req_Auth.length, InetAddress.getByName(Bas_IP), bas_PORT);
			dataSocket.send(requestPacket);
			// 接收服务器的数据包
			byte[] ACK_Auth_Data = new byte[32];
			DatagramPacket receivePacket = new DatagramPacket(ACK_Auth_Data, 32);
			// 设置请求超时3秒
			dataSocket.setSoTimeout(timeout_Sec * 1000);
			dataSocket.receive(receivePacket);
			log.info("ACK Auth" + PortalUtil.Getbyte2HexString(ACK_Auth_Data));
			if ((int) (ACK_Auth_Data[14] & 0xFF) == 0 || ((int) (ACK_Auth_Data[14] & 0xFF) == 2)) {
				log.info("认证成功,准备发送AFF_ACK_AUTH!!!");
				return PAP_Auth_V2.AFF_Ack_Auth(SerialNo, UserIP, Bas_IP, bas_PORT, sharedSecret);
			} else {
				if ((int) (ACK_Auth_Data[14] & 0xFF) == 1) {
					log.info("发送认证请求被拒绝!!!");
				} 
	
//已经连接，判断为登陆成功！								
//				else if ((int) (ACK_Auth_Data[14] & 0xFF) == 2) {
//					log.info("发生认证请求连接已建立!!!");
//				}
				
				else if ((int) (ACK_Auth_Data[14] & 0xFF) == 3) {
					log.info("系统繁忙，请稍后再试!!!");
				} else if ((int) (ACK_Auth_Data[14] & 0xFF) == 4) {
					log.info("发送认证请求失败!!!");
				} else {
					log.info("发送认证请求出现未知错误!!!");
				}
				return false;
			}
		} catch (IOException e) {
			log.info("发送认证请求无响应!!!");
			PAP_Quit_V2.quit(2, Bas_IP, bas_PORT, timeout_Sec, SerialNo,
					UserIP, sharedSecret);// 发送超时回复报文
			return false;
		} finally {
			dataSocket.close();
		}
	}
	
	//构建AFF_Ack_Auth包
	public static boolean AFF_Ack_Auth(byte[] SerialNo,byte[] UserIP,String Bas_IP,int bas_PORT,String sharedSecret) {
		// 创建连接
		DatagramSocket dataSocket=null;
		// 创建AFF_Ack_Auth包
		byte[] AFF_Ack_Auth_Data = new byte[32];
		// 给AFF_ACK_AUTH包赋值
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
		byte[] BBBuff = new byte[16];
		for (int i = 0; i < BBBuff.length; i++) {
			BBBuff[i] = AFF_Ack_Auth_Data[i];
		}
		byte[] Attrs = new byte[0];
		byte[] BAuthen = Authenticator.MK_Authen(BBBuff, Attrs,sharedSecret);
		for (int i = 0; i < 16; i++) {
			AFF_Ack_Auth_Data[16 + i] = BAuthen[i];
		}
		log.info("AFF_Ack_Auth" + PortalUtil.Getbyte2HexString(AFF_Ack_Auth_Data));
		try {
			dataSocket = new DatagramSocket();
			// 创建发送数据包并发送给服务器
			DatagramPacket requestPacket = new DatagramPacket(AFF_Ack_Auth_Data, 32, InetAddress.getByName(Bas_IP),bas_PORT);
			dataSocket.send(requestPacket);
			log.info("发送AFF_Ack_Auth认证成功回复报文成功!!!");
			return true;
		} catch (IOException e) {
			log.info("发送AFF_Ack_Auth认证成功回复报文出错!!!");
			return false;
		} finally {
			dataSocket.close();
		}
	}
}
