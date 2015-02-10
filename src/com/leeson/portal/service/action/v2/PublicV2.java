package com.leeson.portal.service.action.v2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.log4j.Logger;

import com.leeson.portal.service.utils.Authenticator;
import com.leeson.portal.service.utils.PortalUtil;

public class PublicV2 {
	
	private static Logger log = Logger.getLogger(PublicV2.class);
	
	
	public static boolean choose(int type,byte[] Req_Quit,int timeout_Sec,String Bas_IP,int bas_PORT,String sharedSecret){
		if (type == 0) {
			return PublicV2.offline(Req_Quit, timeout_Sec, Bas_IP, bas_PORT, sharedSecret);
		} else {
			return PublicV2.timeoutAffirm(Req_Quit, Bas_IP, bas_PORT, sharedSecret);
		}
	}
	
	/**
	 * 发送请求退出请求
	 * @author LeeSon QQ:25901875
	 * 
	 */
	public static boolean offline(byte[] Req_Quit,int timeout_Sec,String Bas_IP,int bas_PORT,String sharedSecret) {
		
		DatagramSocket dataSocket = null;// 创建连接
		byte[] ACK_Data = new byte[32];
		byte[] BBuff = new byte[16];
		for (int i = 0; i < 16; i++) {
			BBuff[i] = Req_Quit[i];
		}
		byte[] Authen = Authenticator.MK_Authen(BBuff, new byte[0],sharedSecret);
		for (int i = 0; i < 16; i++) {
			Req_Quit[16 + i] = Authen[i];
		}
		log.info("REQ Quit" + PortalUtil.Getbyte2HexString(Req_Quit));
		try {
			dataSocket = new DatagramSocket();
			DatagramPacket requestPacket = new DatagramPacket(Req_Quit, 32, InetAddress.getByName(Bas_IP), bas_PORT);// 创建发送数据包并发送给服务器
			dataSocket.send(requestPacket);
			DatagramPacket receivePacket = new DatagramPacket(ACK_Data, 32);// 接收服务器的数据包
			dataSocket.setSoTimeout(timeout_Sec * 100);// 设置请求超时3秒
			dataSocket.receive(receivePacket);
			log.info("ACK Quit" + PortalUtil.Getbyte2HexString(ACK_Data));
		} catch (IOException e) {
			log.info("发送下线请求无响应!!!");
			return false;
		} finally {
			dataSocket.close();
		}
		if ((int) (ACK_Data[14] & 0xFF) == 1) {
			log.info("发送下线请求被拒绝!!!");
			return false;
		} else if ((int) (ACK_Data[14] & 0xFF) == 2) {
			log.info("发送下线请求出现错误!!!");
			return false;
		}else {
			log.info("请求下线成功！！");
			return true;
		}
	}
	/**
	 * 连接请求超时回复报文
	 * @author LeeSon QQ:25901875
	 *
	 */
	public static boolean timeoutAffirm(byte[] Req_Quit,String Bas_IP,int bas_PORT,String sharedSecret) {
		DatagramSocket dataSocket = null;// 创建连接
		byte[] BBuff = new byte[16];
		Req_Quit[14] = (byte) 1;
		for (int i = 0; i < 16; i++) {
			BBuff[i] = Req_Quit[i];
		}
		byte[] Authen = Authenticator.MK_Authen(BBuff, new byte[0], sharedSecret);
		for (int i = 0; i < 16; i++) {
			Req_Quit[16 + i] = Authen[i];
		}
		try {
			dataSocket = new DatagramSocket();
			DatagramPacket requestPacket = new DatagramPacket(Req_Quit, 32,InetAddress.getByName(Bas_IP), bas_PORT);// 创建发送数据包并发送给服务器
			dataSocket.send(requestPacket);
			log.info("发送超时回复报文成功:"+PortalUtil.Getbyte2HexString(Req_Quit));
			return true;
		} catch (IOException e) {
			log.info("请求超时回复报文发生未知错误!");
			return false;
		} finally {
			dataSocket.close();
		}
	}
}
