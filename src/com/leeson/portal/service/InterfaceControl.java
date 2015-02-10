package com.leeson.portal.service;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import com.leeson.portal.model.Config;
import com.leeson.portal.model.PortalConst;
import com.leeson.portal.service.action.v1.chap.Chap_Auth_V1;
import com.leeson.portal.service.action.v1.chap.Chap_Challenge_V1;
import com.leeson.portal.service.action.v1.chap.Chap_Quit_V1;
import com.leeson.portal.service.action.v1.pap.PAP_Auth_V1;
import com.leeson.portal.service.action.v1.pap.PAP_Quit_V1;
import com.leeson.portal.service.action.v2.chap.Chap_Auth_V2;
import com.leeson.portal.service.action.v2.chap.Chap_Challenge_V2;
import com.leeson.portal.service.action.v2.chap.Chap_Quit_V2;
import com.leeson.portal.service.action.v2.pap.PAP_Auth_V2;
import com.leeson.portal.service.action.v2.pap.PAP_Quit_V2;
import com.leeson.portal.service.utils.PortalUtil;

/**
 * 供外部调用接口
 * 
 * @author LeeSon QQ:25901875
 * 
 */
public class InterfaceControl {

	private static Logger log = Logger.getLogger(InterfaceControl.class);

	private static Config config=Config.getInstance();
	/**
	 * basIp AC通信IP地址 basPort AC socket端口号 sharedSecret BAS和Portal
	 * Server之间的共享密钥secret 相当于签名 authType 定义认证方式 有PAP/CHAP两种 timeoutSec 定义请求超时时间
	 * 单位秒 portalVer portal协议版本 目前有1.0和2.0
	 */
	final static String basIp = config.getBas_ip();
//	final static int basPort = NumberUtils.toInt((String) config.getBas_port());
	final static int basPort = Integer.parseInt(config.getBas_port());
	final static String sharedSecret = config.getSharedSecret();
	final static String authType = config.getAuthType();
//	final static int timeoutSec = NumberUtils.toInt((String) config.getTimeoutSec());
	final static int timeoutSec = Integer.parseInt(config.getTimeoutSec());
//	final static int portalVer = NumberUtils.toInt((String) config.getPortalVer());
	final static int portalVer = Integer.parseInt(config.getPortalVer());

	public static Boolean Method(String Action, String userName,
			String passWord, String ip) {
		byte[] SerialNo = PortalUtil.SerialNo();// 报文序列号
		byte[] UserIP = new byte[4];
		String[] ips = ip.split("[.]");
		// 将ip地址加入字段UserIP
		for (int i = 0; i < 4; i++) {
			int m = NumberUtils.toInt(ips[i]);
			byte b = (byte) m;
			UserIP[i] = b;
		}
		
		//PAP=============================================================
		if (authType.equals(PortalConst.PAP) && portalVer == 1) {// V1 PAP
			log.info("使用Portal V1协议，PAP认证方式！！");
			if (Action.equals(PortalConst.PORTAL_LOGIN)) {
				return PAP_Auth_V1.auth(basIp, basPort, timeoutSec, userName,
						passWord, SerialNo, UserIP);
			} else {
				return PAP_Quit_V1.quit(0, basIp, basPort, timeoutSec,
						SerialNo, UserIP);
			}
		} else if (authType.equals(PortalConst.PAP) && portalVer == 2) {// V2 PAP
			log.info("使用Portal V2协议，PAP认证方式！！");
			if (Action.equals(PortalConst.PORTAL_LOGIN)) {
				return PAP_Auth_V2.auth(basIp, basPort, timeoutSec, userName,
						passWord, SerialNo, UserIP, sharedSecret);
			} else {
				return PAP_Quit_V2.quit(0, basIp, basPort, timeoutSec,
						SerialNo, UserIP, sharedSecret);
			}
		} 
		
		//CHAP=============================================================
		else if (authType.equals(PortalConst.CHAP) && portalVer == 2) {// V2 CHAP
			return Portal_V2(Action, userName, passWord, basIp, basPort,
					timeoutSec, SerialNo, UserIP, sharedSecret);
		} else if (authType.equals(PortalConst.CHAP) && portalVer == 1) {// V1 CHAP
			return Portal_V1(Action, userName, passWord, basIp, basPort,
					timeoutSec, SerialNo, UserIP);
		} else {
			log.info("参数错误,认证方式或版本号错误!!!");
			return false;
		}
	}

	private static boolean Portal_V2(String Action, String userName,
			String passWord, String basIp, int basPort, int timeoutSec,
			byte[] SerialNo, byte[] UserIP, String sharedSecret) {
		log.info("使用Portal V2协议，Chap认证方式！！");
		byte[] ReqID = new byte[2];
		if (Action.equals(PortalConst.PORTAL_LOGIN)) {
			byte[] Challenge = new byte[16];
			// 创建Ack_Challenge_V2包
			byte[] Ack_Challenge_V2 = Chap_Challenge_V2.challenge(basIp,
					basPort, timeoutSec, SerialNo, UserIP, sharedSecret);
			// 如果出错直接返回错误信息
			if (Ack_Challenge_V2.length == 1) {
				Chap_Quit_V2.quit(1, basIp, basPort, timeoutSec, SerialNo,
						UserIP, ReqID, sharedSecret);// 发送超时回复报文
				return false;
			} else {
				ReqID[0] = Ack_Challenge_V2[6];
				ReqID[1] = Ack_Challenge_V2[7];
				for (int i = 0; i < 16; i++) {
					Challenge[i] = Ack_Challenge_V2[34 + i];
				}
				log.info("获得Challenge："
						+ PortalUtil.Getbyte2HexString(Challenge));
				// 创建Ack_Challenge_V2包
				byte[] Ack_Auth_V2 = Chap_Auth_V2.auth(basIp, basPort,
						timeoutSec, userName, passWord, SerialNo, UserIP,
						ReqID, Challenge, sharedSecret);
				if ((int) (Ack_Auth_V2[0] & 0xFF) != 20
						&& (int) (Ack_Auth_V2[0] & 0xFF) != 22) {
					    Chap_Quit_V2.quit(2, basIp, basPort, timeoutSec, SerialNo,
							UserIP, ReqID, sharedSecret);// 发送超时回复报文
					return false;
				} else {
					return true;
				}
			}
		} else {
			return Chap_Quit_V2.quit(0, basIp, basPort, timeoutSec, SerialNo,
					UserIP, ReqID, sharedSecret);
		}
	}

	private static boolean Portal_V1(String Action, String userName,
			String passWord, String basIp, int basPort, int timeoutSec,
			byte[] SerialNo, byte[] UserIP) {
		log.info("使用Portal V1协议，Chap认证方式！！");
		byte[] ReqID = new byte[2];
		if (Action.equals(PortalConst.PORTAL_LOGIN)) {
			byte[] Challenge = new byte[16];
			// 创建Ack_Challenge_V1包
			byte[] Ack_Challenge_V1 = Chap_Challenge_V1.Action(basIp, basPort,
					timeoutSec, SerialNo, UserIP);
			// 如果出错直接返回错误信息
			if (Ack_Challenge_V1.length == 1) {
				Chap_Quit_V1.quit(1, basIp, basPort, timeoutSec, SerialNo,
						UserIP, ReqID);// 发送超时回复报文
				return false;
			} else {
				ReqID[0] = Ack_Challenge_V1[6];
				ReqID[1] = Ack_Challenge_V1[7];
				for (int i = 0; i < 16; i++) {
					Challenge[i] = Ack_Challenge_V1[18 + i];
				}
				log.info("获得Challenge："
						+ PortalUtil.Getbyte2HexString(Challenge));
				// 创建Ack_Challenge_V1包
				byte[] Ack_Auth_V1 = Chap_Auth_V1.auth(basIp, basPort,
						timeoutSec, userName, passWord, SerialNo, UserIP,
						ReqID, Challenge);
				if ((int) (Ack_Auth_V1[0] & 0xFF) != 20
						&& (int) (Ack_Auth_V1[0] & 0xFF) != 22) {
					Chap_Quit_V1.quit(2, basIp, basPort, timeoutSec, SerialNo,
							UserIP, ReqID);// 发送超时回复报文
					return false;
				} else {
					return true;
				}
			}
		} else {
			return Chap_Quit_V1.quit(0, basIp, basPort, timeoutSec, SerialNo,
					UserIP, ReqID);
		}
	}
}
