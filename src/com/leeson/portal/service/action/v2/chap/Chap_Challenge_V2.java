package com.leeson.portal.service.action.v2.chap;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.log4j.Logger;

import com.leeson.portal.service.utils.Authenticator;
import com.leeson.portal.service.utils.PortalUtil;

/**
 * Challenge_V2包
 * 
 * @author LeeSon QQ:25901875
 * 
 */
public class Chap_Challenge_V2 {
	
	private static Logger log = Logger.getLogger(Chap_Challenge_V2.class);
	
	public static byte[] challenge(String Bas_IP, int bas_PORT, int timeout_Sec,byte[] SerialNo, byte[] UserIP, String sharedSecret) {
		DatagramSocket dataSocket=null;// 创建连接
		byte[] ErrorInfo = new byte[1];// 创建ErrorInfo包
		byte[] Req_Challenge = new byte[32];// 创建Req_Challenge包
		byte[] BBuff = new byte[16];
		byte[] Attrs = new byte[0];
		Req_Challenge[0] = (byte) 2;
		Req_Challenge[1] = (byte) 1;
		Req_Challenge[2] = (byte) 0;
		Req_Challenge[3] = (byte) 0;
		Req_Challenge[4] = SerialNo[0];
		Req_Challenge[5] = SerialNo[1];
		Req_Challenge[6] = (byte) 0;
		Req_Challenge[7] = (byte) 0;
		Req_Challenge[8] = UserIP[0];
		Req_Challenge[9] = UserIP[1];
		Req_Challenge[10] = UserIP[2];
		Req_Challenge[11] = UserIP[3];
		Req_Challenge[12] = (byte) 0;
		Req_Challenge[13] = (byte) 0;
		Req_Challenge[14] = (byte) 0;
		Req_Challenge[15] = (byte) 0;
		for (int i = 0; i < 16; i++) {
			BBuff[i] = Req_Challenge[i];
		}
		byte[] Authen = Authenticator.MK_Authen(BBuff, Attrs, sharedSecret);
		for (int i = 0; i < 16; i++) {
			Req_Challenge[16 + i] = Authen[i];
		}
		log.info("REQ Challenge" + PortalUtil.Getbyte2HexString(Req_Challenge));
		try {
			dataSocket = new DatagramSocket();
			DatagramPacket requestPacket = new DatagramPacket(Req_Challenge,32, InetAddress.getByName(Bas_IP), bas_PORT);// 创建发送数据包并发送给服务器
			dataSocket.send(requestPacket);
			byte[] ACK_Challenge_Data = new byte[50];// 接收服务器的数据包
			DatagramPacket receivePacket = new DatagramPacket(ACK_Challenge_Data, 50);
			dataSocket.setSoTimeout(timeout_Sec * 1000);// 设置请求超时3秒
			dataSocket.receive(receivePacket);
			log.info("ACK Challenge" + PortalUtil.Getbyte2HexString(ACK_Challenge_Data));
			if ((int) (ACK_Challenge_Data[14] & 0xFF) == 0) {
				log.info("发送Challenge请求成功,准备发送REQ Auth!!!");
				return ACK_Challenge_Data;
			} else if ((int) (ACK_Challenge_Data[14] & 0xFF) == 1) {
				log.info("发送Challenge请求被拒绝!!!");
				ErrorInfo[0] = (byte) 11;
				return ErrorInfo;
			} else if ((int) (ACK_Challenge_Data[14] & 0xFF) == 2) {
				log.info("发送Challenge连接已建立!!!");
				ErrorInfo[0] = (byte) 12;
				return ErrorInfo;
			} else if ((int) (ACK_Challenge_Data[14] & 0xFF) == 3) {
				log.info("系统繁忙，请稍后再试!!!");
				ErrorInfo[0] = (byte) 13;
				return ErrorInfo;
			} else if ((int) (ACK_Challenge_Data[14] & 0xFF) == 4) {
				log.info("发送Challenge请求出现未知错误!!!");
				ErrorInfo[0] = (byte) 14;
				return ErrorInfo;
			} else {
				log.info("发送Challenge请求出现未知错误!!!");
				ErrorInfo[0] = (byte) 14;
				return ErrorInfo;
			}
		} catch (IOException e) {
			log.info("发送Challenge请求无响应!!!");
			ErrorInfo[0] = (byte) 01;
			return ErrorInfo;
		} finally {
			dataSocket.close();
		}
	}
}
