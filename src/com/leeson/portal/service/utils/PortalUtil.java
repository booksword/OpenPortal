package com.leeson.portal.service.utils;

public class PortalUtil {

	/**
	 * UDP传输16进制字节流包很容易丢失 出现乱码的几率相当大 所以这里我们先转成16进制字符串再做一个拼接操作
	 * 
	 * 注意这里b[ i ] & 0xFF将一个byte和 0xFF进行了与运算。 b[ i ] & 0xFF运算后得出的仍然是个int,那么为何要和
	 * 0xFF进行与运算呢?直接 Integer.toHexString(b[ i ]); 将byte强转为int不行吗?答案是不行的. 其原因在于:
	 * 1.byte的大小为8bits而int的大小为32bits 2.java的二进制采用的是补码形式
	 * byte是一个字节保存的,有8个位,即8个0、1。 8位的第一个位是符号位, 也就是说0000 0001代表的是数字1 1000
	 * 0000代表的就是-1 所以正数最大位0111 1111,也就是数字127 负数最大为1111 1111,也就是数字-128
	 * 
	 * @author LeeSon QQ:25901875
	 * 
	 */
	public static String Getbyte2HexString(byte[] b) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex);
		}
		return ("[" + sb.toString() + "]");
	}

	public static byte[] SerialNo() {
		byte[] SerialNo = new byte[2];
		short SerialNo_int = (short) (1 + Math.random() * 32767);
		for (int i = 0; i < 2; i++) {
			int offset = (SerialNo.length - 1 - i) * 8;
			SerialNo[i] = (byte) ((SerialNo_int >>> offset) & 0xff);
		}
		return SerialNo;
	}
}
