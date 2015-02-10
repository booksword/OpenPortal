package com.leeson.portal.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.leeson.portal.controller.utils.VerifyCode;

/**
 * 验证码判断
 * 
 * @author LeeSon QQ:25901875
 */
public class VCodeServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6731196330461009661L;

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/*
		 * 1. 生成图片 2. 保存图片上的文本到session域中 3. 把图片响应给客户端
		 */
		VerifyCode vc = new VerifyCode();
		BufferedImage image = vc.getImage();
		request.getSession().setAttribute("session_vcode", vc.getText());// 保存图片上的文本到session域

		VerifyCode.output(image, response.getOutputStream());
	}

}
