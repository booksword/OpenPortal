package LeeSon.Portal.Service;

import org.apache.log4j.Logger;

import LeeSon.Portal.Domain.Config;
import LeeSon.Portal.Service.Action.Chap_Auth_V1;
import LeeSon.Portal.Service.Action.Chap_Auth_V2;
import LeeSon.Portal.Service.Action.Chap_Challenge_V1;
import LeeSon.Portal.Service.Action.Chap_Challenge_V2;
import LeeSon.Portal.Service.Action.Chap_Quit_V1;
import LeeSon.Portal.Service.Action.Chap_Quit_V2;
import LeeSon.Portal.Service.Action.PAP_Auth_V1;
import LeeSon.Portal.Service.Action.PAP_Auth_V2;
import LeeSon.Portal.Service.Action.PAP_Quit_V1;
import LeeSon.Portal.Service.Action.PAP_Quit_V2;
import LeeSon.Portal.Utils.WR;
import LeeSon.Portal.Utils.Write2Log;

/**
 * 调用接口
 * 
 * @author LeeSon QQ:25901875
 * 
 */
public class Service {
	// 构建portal协议中的字段包

	Logger logger=Logger.getLogger(Service.class);
	byte[] SerialNo = new byte[2];
	byte[] ReqID = new byte[2];
	byte[] UserIP = new byte[4];
	byte[] Challenge = new byte[16];

	public Service() {
		/*
		 * 给SerialNo[]赋值 创建随机数SerialNo byte[]
		 */
		short SerialNo_int = (short) (1 + Math.random() * 32767);
		for (int i = 0; i < 2; i++) {
			int offset = (SerialNo.length - 1 - i) * 8;
			SerialNo[i] = (byte) ((SerialNo_int >>> offset) & 0xff);
		}
	}

	// public int Method(String Action, String in_username, String in_password,
	// String ip, String basIP, String basPORT, String portalVer,
	// String authType, String timeoutSec, String sharedSecret) {
	public int Method(String Action, String in_username, String in_password,
			String ip, Config cfg) {

		// String Bas_IP = basIP;
		// int bas_PORT = Integer.parseInt(basPORT);
		// int portal_Ver = Integer.parseInt(portalVer);
		// int auth_Type = Integer.parseInt(authType);
		// int timeout_Sec = Integer.parseInt(timeoutSec);

		String Bas_IP = cfg.getBas_ip();
		int bas_PORT = Integer.parseInt(cfg.getBas_port());
		int portal_Ver = Integer.parseInt(cfg.getPortalVer());
		int auth_Type = Integer.parseInt(cfg.getAuthType());
		int timeout_Sec = Integer.parseInt(cfg.getTimeoutSec());
		String sharedSecret = cfg.getSharedSecret();

		/*
		 * 给UserIP[]赋值 接收客户ip地址 IP地址压缩成4字节,如果要进一步处理的话,就可以转换成一个int了.
		 */
		String[] ips = ip.split("[.]");
		// 将ip地址加入字段UserIP
		for (int i = 0; i < 4; i++) {
			int m = Integer.parseInt(ips[i]);
			byte b = (byte) m;
			UserIP[i] = b;
		}

		// V1 PAP
		if ((auth_Type == 1) && (portal_Ver == 1)) {
			System.out.println("使用Portal V1协议，PAP认证方式！！");
			Write2Log.Wr2Log("使用Portal V1协议，PAP认证方式！！");
			logger.debug("使用Portal V1协议，PAP认证方式！！");
			if (Action.equals("Login")) {

				return new PAP_Auth_V1().Action(Bas_IP, bas_PORT, timeout_Sec,
						in_username, in_password, SerialNo, UserIP);

			}
			if (Action.equals("LoginOut")) {
				return new PAP_Quit_V1().Action(0, Bas_IP, bas_PORT,
						timeout_Sec, SerialNo, UserIP);
			}
			return 99;
		}

		// V2 PAP
		if ((auth_Type == 1) && (portal_Ver == 2)) {
			System.out.println("使用Portal V2协议，PAP认证方式！！");
			Write2Log.Wr2Log("使用Portal V2协议，PAP认证方式！！");
			logger.debug("使用Portal V2协议，PAP认证方式！！");
			if (Action.equals("Login")) {

				return new PAP_Auth_V2().Action(Bas_IP, bas_PORT, timeout_Sec,
						in_username, in_password, SerialNo, UserIP,
						sharedSecret);

			}
			if (Action.equals("LoginOut")) {
				return new PAP_Quit_V2().Action(0, Bas_IP, bas_PORT,
						timeout_Sec, SerialNo, UserIP, sharedSecret);
			}
			return 99;
		}

		// V2 CHAP
		if ((auth_Type == 0) && (portal_Ver == 2)) {
			return Portal_V2(Action, in_username, in_password, Bas_IP,
					bas_PORT, timeout_Sec, SerialNo, UserIP, sharedSecret);
		}

		// V1 CHAP
		if ((auth_Type == 0) && (portal_Ver == 1)) {
			return Portal_V1(Action, in_username, in_password, Bas_IP,
					bas_PORT, timeout_Sec, SerialNo, UserIP);
		}
		return 55;

	}

	private int Portal_V2(String Action, String in_username,
			String in_password, String Bas_IP, int bas_PORT, int timeout_Sec,
			byte[] SerialNo, byte[] UserIP, String sharedSecret) {
		System.out.println("使用Portal V2协议，Chap认证方式！！");
		Write2Log.Wr2Log("使用Portal V2协议，Chap认证方式！！");
		logger.debug("使用Portal V2协议，Chap认证方式！！");
		if (Action.equals("Login")) {
			// 创建Ack_Challenge_V2包
			byte[] Ack_Challenge_V2 = new Chap_Challenge_V2().Action(Bas_IP,
					bas_PORT, timeout_Sec, SerialNo, UserIP, sharedSecret);
			// 如果出错直接返回错误信息
			if (Ack_Challenge_V2.length == 1) {
				new Chap_Quit_V2().Action(1, Bas_IP, bas_PORT, timeout_Sec,
						SerialNo, UserIP, ReqID, sharedSecret);
				return (int) (Ack_Challenge_V2[0] & 0xFF);
			}
			ReqID[0] = Ack_Challenge_V2[6];
			ReqID[1] = Ack_Challenge_V2[7];
			for (int i = 0; i < 16; i++) {
				Challenge[i] = Ack_Challenge_V2[34 + i];
			}
			System.out
					.println("获得Challenge：" + WR.Getbyte2HexString(Challenge));
			Write2Log.Wr2Log("获得Challenge：" + WR.Getbyte2HexString(Challenge));
			logger.debug("获得Challenge：" + WR.Getbyte2HexString(Challenge));
			// 创建Ack_Challenge_V2包
			byte[] Ack_Auth_V2 = new Chap_Auth_V2().Action(Bas_IP, bas_PORT,
					timeout_Sec, in_username, in_password, SerialNo, UserIP,
					ReqID, Challenge, sharedSecret);
			// 如果出错直接返回错误信息
			if ((int) (Ack_Auth_V2[0] & 0xFF) == 20) {
				return 0;
			} else if ((int) (Ack_Auth_V2[0] & 0xFF) == 22) {
				return 22;
			} else {
				new Chap_Quit_V2().Action(2, Bas_IP, bas_PORT, timeout_Sec,
						SerialNo, UserIP, ReqID, sharedSecret);
				return (int) (Ack_Auth_V2[0] & 0xFF);
			}

		}
		if (Action.equals("LoginOut")) {
			return new Chap_Quit_V2().Action(0, Bas_IP, bas_PORT, timeout_Sec,
					SerialNo, UserIP, ReqID, sharedSecret);
		}

		return 99;
	}

	private int Portal_V1(String Action, String in_username,
			String in_password, String Bas_IP, int bas_PORT, int timeout_Sec,
			byte[] SerialNo, byte[] UserIP) {
		System.out.println("使用Portal V1协议，Chap认证方式！！");
		Write2Log.Wr2Log("使用Portal V1协议，Chap认证方式！！");
		logger.debug("使用Portal V1协议，Chap认证方式！！");
		if (Action.equals("Login")) {
			// 创建Ack_Challenge_V1包
			byte[] Ack_Challenge_V1 = new Chap_Challenge_V1().Action(Bas_IP,
					bas_PORT, timeout_Sec, SerialNo, UserIP);
			// 如果出错直接返回错误信息
			if (Ack_Challenge_V1.length == 1) {
				new Chap_Quit_V1().Action(1, Bas_IP, bas_PORT, timeout_Sec,
						SerialNo, UserIP, ReqID);
				return (int) (Ack_Challenge_V1[0] & 0xFF);
			}
			ReqID[0] = Ack_Challenge_V1[6];
			ReqID[1] = Ack_Challenge_V1[7];
			for (int i = 0; i < 16; i++) {
				Challenge[i] = Ack_Challenge_V1[18 + i];
			}
			System.out
					.println("获得Challenge：" + WR.Getbyte2HexString(Challenge));
			Write2Log.Wr2Log("获得Challenge：" + WR.Getbyte2HexString(Challenge));
			logger.debug("获得Challenge：" + WR.Getbyte2HexString(Challenge));
			// 创建Ack_Challenge_V1包
			byte[] Ack_Auth_V1 = new Chap_Auth_V1().Action(Bas_IP, bas_PORT,
					timeout_Sec, in_username, in_password, SerialNo, UserIP,
					ReqID, Challenge);
			// 如果出错直接返回错误信息
			if ((int) (Ack_Auth_V1[0] & 0xFF) == 20) {
				return 0;
			} else if ((int) (Ack_Auth_V1[0] & 0xFF) == 22) {
				return 22;
			} else {
				new Chap_Quit_V1().Action(2, Bas_IP, bas_PORT, timeout_Sec,
						SerialNo, UserIP, ReqID);
				return (int) (Ack_Auth_V1[0] & 0xFF);
			}
		}
		if (Action.equals("LoginOut")) {
			return new Chap_Quit_V1().Action(0, Bas_IP, bas_PORT, timeout_Sec,
					SerialNo, UserIP, ReqID);
		}
		return 99;
	}
}
