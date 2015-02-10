package com.leeson.portal.service.action.v1.chap;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.log4j.Logger;

import com.leeson.portal.service.utils.ChapPassword;
import com.leeson.portal.service.utils.PortalUtil;

/**
 * Auth_V1包
 * 
 * @author LeeSon QQ:25901875
 * V1版本构建认证报文
 * 
 */
public class Chap_Auth_V1 {
	
	private static Logger log = Logger.getLogger(Chap_Auth_V1.class);
	
	public static byte[] auth(String Bas_IP, int bas_PORT, int timeout_Sec,String in_username, String in_password, byte[] SerialNo,byte[] UserIP, byte[] ReqID, byte[] Challenge) {
		// 创建ChapPassword包
		byte[] ChapPass = new byte[16];
		byte[] Username = in_username.getBytes();
		byte[] password = in_password.getBytes();
		try {
			ChapPass = ChapPassword.MK_ChapPwd(ReqID, Challenge,password);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Chap_Auth_V1.Req_Auth(Username, ChapPass, SerialNo, UserIP, ReqID,timeout_Sec,Bas_IP,bas_PORT);
	}
	
	// 构建Req_Auth包
	public static byte[] Req_Auth(byte[] Username,byte[] ChapPass, byte[] SerialNo,byte[] UserIP, byte[] ReqID,int timeout_Sec,String Bas_IP,int bas_PORT) {
		// 创建连接
		DatagramSocket dataSocket=null;
		// 创建ErrorInfo包
		byte[] ErrorInfo = new byte[1];
		byte[] Req_Auth = new byte[20 + Username.length + ChapPass.length];
		Req_Auth[0] = (byte) 1;
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
		Req_Auth[16] = (byte) 1;
		Req_Auth[17] = (byte) (Username.length + 2);
		for (int i = 0; i < Username.length; i++) {
			Req_Auth[18 + i] = Username[i];
		}
		Req_Auth[18 + Username.length] = (byte) 4;
		Req_Auth[19 + Username.length] = (byte) (ChapPass.length + 2);
		for (int i = 0; i < ChapPass.length; i++) {
			Req_Auth[20 + Username.length + i] = ChapPass[i];
		}
		log.info("REQ Auth" + PortalUtil.Getbyte2HexString(Req_Auth));
		try {
			dataSocket = new DatagramSocket();
			DatagramPacket requestPacket = new DatagramPacket(Req_Auth,Req_Auth.length, InetAddress.getByName(Bas_IP), bas_PORT);// 创建发送数据包并发送给服务器
			dataSocket.send(requestPacket);
			byte[] ACK_Auth_Data = new byte[16];// 接收服务器的数据包
			DatagramPacket receivePacket = new DatagramPacket(ACK_Auth_Data,ACK_Auth_Data.length);
			dataSocket.setSoTimeout(timeout_Sec * 1000);// 设置请求超时3秒
			dataSocket.receive(receivePacket);
			log.info("ACK Auth" + PortalUtil.Getbyte2HexString(ACK_Auth_Data));
			if ((int) (ACK_Auth_Data[14] & 0xFF) == 0) {
				log.info("认证成功,准备发送AFF_ACK_AUTH!!!");
				return Chap_Auth_V1.AFF_Ack_Auth(SerialNo, UserIP, ReqID,Bas_IP,bas_PORT);//构建AFF_Ack_Auth包
			} else if ((int) (ACK_Auth_Data[14] & 0xFF) == 1) {
				log.info("发送认证请求被拒绝!!!");
				ErrorInfo[0] = (byte) 21;
				return ErrorInfo;
			} else if ((int) (ACK_Auth_Data[14] & 0xFF) == 2) {
				log.info("发送认证请求连接已建立!!!");
				ErrorInfo[0] = (byte) 22;
				return ErrorInfo;
			} else if ((int) (ACK_Auth_Data[14] & 0xFF) == 3) {
				log.info("系统繁忙,请稍后再试!!!");
				ErrorInfo[0] = (byte) 23;
				return ErrorInfo;
			} else if ((int) (ACK_Auth_Data[14] & 0xFF) == 4) {
				log.info("发送认证请求失败!!!");
				ErrorInfo[0] = (byte) 24;
				return ErrorInfo;
			} else {
				log.info("发送认证请求出现未知错误!!!");
				ErrorInfo[0] = (byte) 02;
				return ErrorInfo;
			}
		} catch (IOException e) {
			log.info("发送认证请求无响应!!!");
			ErrorInfo[0] = (byte) 02;
			return ErrorInfo;
		} finally {
			dataSocket.close();
		}
	}
	
	/**
	 * 构建AFF_Ack_Auth包
	 * @author LeeSon QQ:25901875
	 *
	 */
	public static byte[] AFF_Ack_Auth(byte[] SerialNo,byte[] UserIP, byte[] ReqID,String Bas_IP,int bas_PORT) {
		// 创建连接
		DatagramSocket dataSocket=null;
		// 创建ErrorInfo包
		byte[] ErrorInfo = new byte[1];
		byte[] AFF_Ack_Auth = new byte[16];
		// 给AFF_ACK_AUTH包赋值
		AFF_Ack_Auth[0] = (byte) 1;
		AFF_Ack_Auth[1] = (byte) 7;
		AFF_Ack_Auth[2] = (byte) 0;
		AFF_Ack_Auth[3] = (byte) 0;
		AFF_Ack_Auth[4] = SerialNo[0];
		AFF_Ack_Auth[5] = SerialNo[1];
		AFF_Ack_Auth[6] = ReqID[0];
		AFF_Ack_Auth[7] = ReqID[1];
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
			DatagramPacket requestPacket = new DatagramPacket(AFF_Ack_Auth, 16, InetAddress.getByName(Bas_IP),bas_PORT);// 创建发送数据包并发送给服务器
			dataSocket.send(requestPacket);
			log.info("发送AFF_Ack_Auth认证成功响应报文回复成功!!!");
		} catch (IOException e) {
			log.info("发送AFF_Ack_Auth认证成功响应报文回复失败!!!");
		} finally {
			dataSocket.close();
		}
		ErrorInfo[0] = (byte) 20;
		return ErrorInfo;
	}
}
