package com.leeson.portal.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import com.leeson.portal.service.InterfaceControl;



/**
 * 退出
 * 
 * @author LeeSon QQ:25901875
 */
public class LoginOut extends HttpServlet {

	
	private static final long serialVersionUID = 5118888119558797794L;
	Logger logger = Logger.getLogger(LoginOut.class);

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setAttribute("msg", "请不要重复刷新！");
		request.getRequestDispatcher("/index.jsp").forward(request, response);

	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		try {
			HttpSession session = request.getSession();
			String ip = (String) session.getAttribute("ip");
			String username = (String) session.getAttribute("username");
			String password = (String) session.getAttribute("password");
			
			logger.info("请求下线    用户：" + username + " 密码:" + password
					+ " IP地址:" + ip);
			
			if (ip.equals("") || ip == null) {
				session.removeAttribute("username");
				session.removeAttribute("password");
				request.setAttribute("msg", "用户信息丢失，请重新登录后再退出！");
				request.getRequestDispatcher("/index.jsp").forward(request,
						response);
				return;
			} else {
				Boolean info = InterfaceControl.Method("PORTAL_LOGINOUT", username, password, ip);
			

			if (info == true) {
				session.removeAttribute("username");
				session.removeAttribute("password");
				request.setAttribute("msg", "用户退出登录！");
				request.getRequestDispatcher("/index.jsp").forward(request,
						response);
			} else {
				
				request.setAttribute("msg", "下线失败,请稍后再试！！");
				RequestDispatcher qr = request
						.getRequestDispatcher("/index.jsp");
				qr.forward(request, response);
			}
			}
		} catch (Exception e) {
			// TODO: handle exception
			request.setAttribute("msg", "用户信息丢失，请重新登录后再退出！");
			request.getRequestDispatcher("/index.jsp").forward(request,
					response);
		}

	}

}
