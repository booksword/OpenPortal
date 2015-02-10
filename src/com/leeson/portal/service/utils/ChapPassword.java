package com.leeson.portal.service.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

/**
 * 生成ChapPassword
 * 
 * @author LeeSon QQ:25901875
 * 
 */
public class ChapPassword {

	private static Logger log = Logger.getLogger(ChapPassword.class);

	public static byte[] MK_ChapPwd(byte[] ReqID, byte[] Challenge, byte[] usp)
			throws UnsupportedEncodingException {
		byte ChapPwd[] = new byte[16];
		// 初始化chappassword byte[]
		byte[] buf = new byte[1 + usp.length + Challenge.length];
		// 给chappassword byte[] 传值
		/*
		 * Chap_Password的生成：Chap_Password的生成遵循标准的Radious协议中的Chap_Password
		 * 生成方法（参见RFC2865）。 密码加密使用MD5算法，MD5函数的输入为ChapID ＋ Password ＋Challenge
		 * 其中，ChapID取ReqID的低 8 位，Password的长度不够协议规定的最大长度，其后不需要补零。
		 */
		buf[0] = ReqID[1];
		for (int i = 0; i < usp.length; i++) {
			buf[1 + i] = usp[i];
		}
		for (int i = 0; i < Challenge.length; i++) {
			buf[1 + usp.length + i] = Challenge[i];
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
			ChapPwd = md.digest();
			log.info("生成Chap-Password" + PortalUtil.Getbyte2HexString(ChapPwd));
		} catch (NoSuchAlgorithmException e) {
			log.info("生成Chap-Password出错！");
		}
		return ChapPwd;
	}
}
