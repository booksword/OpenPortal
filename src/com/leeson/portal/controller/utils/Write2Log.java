package com.leeson.portal.controller.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
* This class is used for 日志文件输出
* @author LeeSon  QQ:25901875
* @version 1.0, 2015年2月10日 上午12:23:04
 */
public class Write2Log {
	public static void Wr2Log(String aaa) {
		String path = System.getProperty("user.dir");

		int index = path.lastIndexOf("\\");
		if (index != -1) {
			path = path.substring(0, index);
		}
		Date date = new Date();
		SimpleDateFormat fd = new SimpleDateFormat("yyyy-MM-dd");

		File dir = new File(path + "\\logs");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir, "OpenPortal_log_" + fd.format(date) + ".txt");
		if (!file.exists()) {

			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileWriter writer;
		try {
			writer = new FileWriter(file, true);
			writer.write(aaa + "\r\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
