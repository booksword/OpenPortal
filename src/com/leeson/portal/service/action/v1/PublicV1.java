package com.leeson.portal.service.action.v1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.log4j.Logger;

import com.leeson.portal.service.utils.PortalUtil;

public class PublicV1 {

	private static Logger log = Logger.getLogger(PublicV1.class);

	public static boolean choose(int type, byte[] Req_Quit, int timeout_Sec,
			String Bas_IP, int bas_PORT) {
		if (type == 0) {
			return PublicV1.offline(Req_Quit, timeout_Sec, Bas_IP, bas_PORT);
		} else {
			return PublicV1.timeoutAffirm(type, Req_Quit, Bas_IP, bas_PORT);
		}
	}

	/**
	 * 发送下线请求
	 * 
	 * @author LeeSon QQ:25901875
	 * 
	 */
	public static boolean offline(byte[] Req_Quit, int timeout_Sec,
			String Bas_IP, int bas_PORT) {
		log.info("REQ Quit" + PortalUtil.Getbyte2HexString(Req_Quit));
		DatagramSocket dataSocket = null;// 创建连接
		byte[] ACK_Data = new byte[16];
		try {
			dataSocket = new DatagramSocket();
			DatagramPacket requestPacket = new DatagramPacket(Req_Quit, 16,
					InetAddress.getByName(Bas_IP), bas_PORT);// 创建发送数据包并发送给服务器
			dataSocket.send(requestPacket);
			DatagramPacket receivePacket = new DatagramPacket(ACK_Data, 16);// 接收服务器的数据包
			dataSocket.setSoTimeout(timeout_Sec * 100);// 设置请求超时时间
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
		} else {
			log.info("请求下线成功！！！");
			return true;
		}
	}

	/**
	 * 请求超时回复报文   V1没有这个功能
	 * 
	 * @author LeeSon QQ:25901875
	 * 
	 */
	public static boolean timeoutAffirm(int type, byte[] Req_Quit,
			String Bas_IP, int bas_PORT) {
		DatagramSocket dataSocket = null;// 创建连接
		Req_Quit[14] = (byte) 1;
		try {
			dataSocket = new DatagramSocket();
			DatagramPacket requestPacket = new DatagramPacket(Req_Quit, 16,
					InetAddress.getByName(Bas_IP), bas_PORT);// 创建发送数据包并发送给服务器
			dataSocket.send(requestPacket);
			log.info("发送超时回复报文成功: " + PortalUtil.Getbyte2HexString(Req_Quit));
			return true;
		} catch (IOException e) {
			log.info("发送超时回复报文出现未知错误！！！");
			return false;
		} finally {
			dataSocket.close();
		}
	}

}
