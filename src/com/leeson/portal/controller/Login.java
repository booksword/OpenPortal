package com.leeson.portal.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.leeson.portal.model.Config;
import com.leeson.portal.service.InterfaceControl;

/**
 * 登录判断
 * 
 * @author LeeSon QQ:25901875
 */
public class Login extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1966047929923869408L;

	public Config cfg = Config.getInstance();

	Logger logger = Logger.getLogger(Login.class);

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		try {
			HttpSession session = request.getSession();
			String username = (String) session.getAttribute("username");
			String password = (String) session.getAttribute("password");
			String ip = (String) session.getAttribute("ip");
			if ((ip.equals("") || ip == null)
					|| (username.equals("") || username == null)
					|| (password.equals("") || password == null)) {
				request.setAttribute("msg", "非法访问！");
				request.getRequestDispatcher("/index.jsp").forward(request,
						response);
				return;
			} else {
				request.setAttribute("msg", "请不要重复刷新！");
				request.getRequestDispatcher("/index.jsp").forward(request,
						response);
				return;
			}

		} catch (Exception e) {
			// TODO: handle exception
			request.setAttribute("msg", "请重新登录！");
			request.getRequestDispatcher("/index.jsp").forward(request,
					response);
			return;
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		/*
		 * 校验验证码 1. 从session中获取正确的验证码 2. 从表单中获取用户填写的验证码 3. 进行比较！ 4.
		 * 如果相同，向下运行，否则保存错误信息到request域，转发到login.jsp
		 */
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		String sessionCode = (String) request.getSession().getAttribute(
				"session_vcode");
		String paramCode = request.getParameter("vcode");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String ip = request.getRemoteAddr();// 获取客户端的ip

		if ((username.equals("")) || (username == null)) {
			request.setAttribute("msg", "用户名不能为空！");
			request.getRequestDispatcher("/index.jsp").forward(request,
					response);
			return;
		}
		if ((password.equals("")) || (password == null)) {
			request.setAttribute("msg", "密码不能为空！");
			request.getRequestDispatcher("/index.jsp").forward(request,
					response);
			return;
		}

		if (!paramCode.equalsIgnoreCase(sessionCode)) {
			request.setAttribute("msg", "验证码错误！");
			request.getRequestDispatcher("/index.jsp").forward(request,
					response);
			return;
		}

		ServletContext sc = this.getServletContext();
		if (sc.getAttribute("config") == null) {
			InitConfig(request, response, cfg);
			ServletContext context = this.getServletContext();
			context.setAttribute("config", cfg);
		}

		
		logger.info("请求认证    用户：" + username + " 密码:" + password + " IP地址:"
				+ ip);

		// int info = new Action().Method("Login", username, password, ip,
		// bas_ip,
		// bas_port, portalVer, authType, timeoutSec, sharedSecret);
		Boolean info = InterfaceControl.Method("PORTAL_LOGIN", username, password, ip);
		if (info==true) {
			Cookie cookie = new Cookie("uname", username);
			cookie.setMaxAge(60 * 60 * 24);
			response.addCookie(cookie);
			HttpSession session = request.getSession();
			session.setAttribute("username", username);
			session.setAttribute("password", password);
			session.setAttribute("ip", ip);
			request.setAttribute("msg", "认证成功！");
			// request.getRequestDispatcher("/index.jsp").forward(request,
			// response);
			String path = request.getContextPath();
			response.sendRedirect(response.encodeUrl(path + "/loginSucc.jsp"));
		} else {
			
			request.setAttribute("msg", "认证失败！！");
			RequestDispatcher qr = request.getRequestDispatcher("/index.jsp");
			qr.forward(request, response);
		}
	}

	private void InitConfig(HttpServletRequest request,
			HttpServletResponse response, Config cfg) throws ServletException,
			IOException {
		
//		@SuppressWarnings("deprecation")
//		String cfgPath = request.getRealPath("/");// 获取服务器的webroot路径  已经过期
		String cfgPath = this.getServletContext().getRealPath("/");  // 获取服务器的页面绝对路径
		FileInputStream fis = null;
		Properties config = new Properties();
		File file = new File(cfgPath + "config.properties");
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			logger.info("config.properties 配置文件不存在！！");
			request.setAttribute("msg", "config.properties 配置文件不存在！！");
			request.getRequestDispatcher("/index.jsp").forward(request,
					response);
			return;
		}

		try {
			config.load(fis);
			// bas_ip = config.getProperty("bas_ip");
			// bas_port = config.getProperty("bas_port");
			// portal_port = config.getProperty("portal_port");
			// sharedSecret = config.getProperty("sharedSecret");
			// authType = config.getProperty("authType");
			// timeoutSec = config.getProperty("timeoutSec");
			// portalVer = config.getProperty("portalVer");

			cfg.setBas_ip(config.getProperty("bas_ip"));
			cfg.setBas_port(config.getProperty("bas_port"));
			cfg.setPortal_port(config.getProperty("portal_port"));
			cfg.setSharedSecret(config.getProperty("sharedSecret"));
			cfg.setAuthType(config.getProperty("authType"));
			cfg.setTimeoutSec(config.getProperty("timeoutSec"));
			cfg.setPortalVer(config.getProperty("portalVer"));
			// #chap 0 pap 1

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("config.properties 数据库配置文件读取失败！！");
			request.setAttribute("msg", "config.properties 数据库配置文件读取失败！！");
			request.getRequestDispatcher("/index.jsp").forward(request,
					response);
			return;
		} finally {
			fis.close();
		}
		logger.info("初始化参数，读取配置文件：" + config);
	}

}
