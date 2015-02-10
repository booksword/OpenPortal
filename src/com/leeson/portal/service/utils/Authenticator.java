package com.leeson.portal.service.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

/**
 * 生成V2协议中Authenticator字段
 * 
 * @author LeeSon QQ:25901875
 * 
 */
public class Authenticator {

	private static Logger log = Logger.getLogger(Authenticator.class);

	/**
	 * 生成Request Authenticator结果 以字节流Ver + Type + PAP/CHAP + Rsvd + SerialNo +
	 * ReqID + UserIP + UserPort + ErrCode + AttrNum + 16个字节的0 + request
	 * attributes + secret作为MD5的输入， 得到的MD5输出就是请求报文的验证字Request Authenticator的内容
	 * 
	 * @param Buff
	 *            基础16字节包
	 * @param Attrs
	 *            request attributes字段包
	 * @param Secret
	 *            Secret字段
	 * @return Request Authenticator 16字节
	 */
	public static byte[] MK_Authen(byte[] Buff, byte[] Attrs,
			String sharedSecret) {
		byte[] Secret = sharedSecret.getBytes();
		byte Authen[] = new byte[16];
		// 初始化buf byte[]
		byte[] buf = new byte[Buff.length + 16 + Attrs.length + Secret.length];
		// 给buf byte[] 传值
		for (int i = 0; i < Buff.length; i++) {
			buf[i] = Buff[i];
		}
		for (int i = 0; i < 16; i++) {
			buf[Buff.length + i] = (byte) 0;
		}
		if (Attrs.length > 0) {
			for (int i = 0; i < Attrs.length; i++) {
				buf[Buff.length + 16 + i] = Attrs[i];
			}
			for (int i = 0; i < Secret.length; i++) {
				buf[Buff.length + 16 + Attrs.length + i] = Secret[i];
			}
		} else {
			for (int i = 0; i < Secret.length; i++) {
				buf[Buff.length + 16 + i] = Secret[i];
			}
		}
		// 生成Chap-Password
		/**
		 * MessageDigest 通过其getInstance系列静态函数来进行实例化和初始化。 MessageDigest 对象通过使用
		 * update 方法处理数据。任何时候都可以调用 reset 方法重置摘要。 一旦所有需要更新的数据都已经被更新了，应该调用 digest
		 * 方法之一完成哈希计算并返回结果。 对于给定数量的更新数据，digest 方法只能被调用一次。digest
		 * 方法被调用后，MessageDigest 对象被重新设置成其初始状态。
		 */
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(buf);
			Authen = md.digest();
			log.info("生成Request Authenticator :"
					+ PortalUtil.Getbyte2HexString(Authen));
		} catch (NoSuchAlgorithmException e) {
			log.info("生成Request Authenticator出错！");
		}
		return Authen;
	}
}
