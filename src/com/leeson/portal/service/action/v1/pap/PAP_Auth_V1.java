package com.leeson.portal.service.action.v1.pap;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.log4j.Logger;

import com.leeson.portal.service.utils.PortalUtil;

/**
 * Auth_V1包
 * 
 * @author LeeSon QQ:25901875
 * 
 */
public class PAP_Auth_V1 {
	
	private static Logger log = Logger.getLogger(PAP_Auth_V1.class);
	
	public static boolean auth(String Bas_IP, int bas_PORT, int timeout_Sec,String in_username, String in_password, byte[] SerialNo,byte[] UserIP) {
		return PAP_Auth_V1.Req_Auth(in_username.getBytes(), in_password.getBytes(), SerialNo, UserIP, timeout_Sec, Bas_IP, bas_PORT);
	}
	
	// 构建Req_Auth包
	public static boolean Req_Auth(byte[] Username,byte[] password, byte[] SerialNo,byte[] UserIP,int timeout_Sec,String Bas_IP,int bas_PORT) {
		DatagramSocket dataSocket=null;// 创建连接
		byte[] Req_Auth = new byte[20 + Username.length + password.length];// 创建Req_Auth包
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
		log.info("REQ Auth" + PortalUtil.Getbyte2HexString(Req_Auth));
		try {
			dataSocket = new DatagramSocket();
			DatagramPacket requestPacket = new DatagramPacket(Req_Auth,Req_Auth.length, InetAddress.getByName(Bas_IP), bas_PORT);// 创建发送数据包并发送给服务器
			dataSocket.send(requestPacket);
			byte[] ACK_Data = new byte[16];// 接收服务器的数据包
			DatagramPacket receivePacket = new DatagramPacket(ACK_Data,ACK_Data.length);
			dataSocket.setSoTimeout(timeout_Sec * 1000);// 设置请求超时
			dataSocket.receive(receivePacket);
			log.info("ACK Auth" + PortalUtil.Getbyte2HexString(ACK_Data));
			if (((int) (ACK_Data[14] & 0xFF) == 0) || ((int) (ACK_Data[14] & 0xFF) == 2)) {
				log.info("认证成功,准备发送AFF_ACK_AUTH!!!");
				return PAP_Auth_V1.AFF_Ack_Auth(SerialNo, UserIP, Bas_IP, bas_PORT);
			} else {
				if ((int) (ACK_Data[14] & 0xFF) == 1) {
					log.info("发送认证请求被拒绝!!!");
				} 
//已经连接，判断为登陆成功！				
//				else if ((int) (ACK_Data[14] & 0xFF) == 2) {
//					log.info("发送认证请求连接已建立!!!");
//				}
				
				
				else if ((int) (ACK_Data[14] & 0xFF) == 3) {
					log.info("系统繁忙，请稍后再试!!!");
				} else if ((int) (ACK_Data[14] & 0xFF) == 4) {
					log.info("发送认证请求失败!!!");
				} else {
					log.info("发送认证请求出现未知错误!!!");
				}
				return false;
			}
		} catch (IOException e) {
			log.info("发送认证请求无响应!!!");
			PAP_Quit_V1.quit(2, Bas_IP, bas_PORT, timeout_Sec, SerialNo,
					UserIP);// 发送超时回复报文
			return false;
		} finally {
			dataSocket.close();
		}
	}
	
	//构建AFF_Ack_Auth包
	public static boolean AFF_Ack_Auth(byte[] SerialNo,byte[] UserIP,String Bas_IP,int bas_PORT) {
		DatagramSocket dataSocket=null;// 创建连接
		byte[] AFF_Ack_Auth = new byte[16];// 创建AFF_Ack_Auth包
		AFF_Ack_Auth[0] = (byte) 1;
		AFF_Ack_Auth[1] = (byte) 7;
		AFF_Ack_Auth[2] = (byte) 1;
		AFF_Ack_Auth[3] = (byte) 0;
		AFF_Ack_Auth[4] = SerialNo[0];
		AFF_Ack_Auth[5] = SerialNo[1];
		AFF_Ack_Auth[6] = (byte) 0;
		AFF_Ack_Auth[7] = (byte) 0;
		AFF_Ack_Auth[8] = UserIP[0];
		AFF_Ack_Auth[9] = UserIP[1];
		AFF_Ack_Auth[10] = UserIP[2];
		AFF_Ack_Auth[11] = UserIP[3];
		AFF_Ack_Auth[12] = (byte) 0;
		AFF_Ack_Auth[13] = (byte) 0;
		AFF_Ack_Auth[14] = (byte) 0;
		AFF_Ack_Auth[15] = (byte) 0;
		log.info("AFF_Ack_Auth" + PortalUtil.Getbyte2HexString(AFF_Ack_Auth));
		try {
			dataSocket = new DatagramSocket();
			DatagramPacket requestPacket = new DatagramPacket(AFF_Ack_Auth, AFF_Ack_Auth.length,InetAddress.getByName(Bas_IP), bas_PORT);// 创建发送数据包并发送给服务器
			dataSocket.send(requestPacket);
			log.info("发送AFF_Ack_Auth认证成功确认报文成功！！");
			return true;
		} catch (IOException e) {
			log.info("发送AFF_Ack_Auth认证成功确认报文出错！！");
			return false;
		} finally {
			dataSocket.close();
		}
	}
}
