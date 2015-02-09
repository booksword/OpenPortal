package LeeSon.Portal.Web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import LeeSon.Portal.Domain.Config;
import LeeSon.Portal.Service.Service;
import LeeSon.Portal.Utils.Write2Log;

/**
 * 退出
 * 
 * @author LeeSon QQ:25901875
 */
public class LoginOut extends HttpServlet {

	Logger logger=Logger.getLogger(LoginOut.class);
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
			System.out.println("请求下线    用户：" + username + " 密码:" + password
					+ " IP地址:" + ip);
			Write2Log.Wr2Log("请求下线    用户：" + username + " 密码:" + password
					+ " IP地址:" + ip);
			logger.debug("请求下线    用户：" + username + " 密码:" + password
					+ " IP地址:" + ip);
			int info = 99;
			if (!(ip.equals("") || ip == null)) {
				info = new Service().Method("LoginOut", username, password, ip,
						Config.getInstance());
			} else {
				session.removeAttribute("username");
				session.removeAttribute("password");
				request.setAttribute("msg", "退出异常，请重新登录后再退出！");
				request.getRequestDispatcher("/index.jsp").forward(request,
						response);
			}

			if (info == 0) {
				session.removeAttribute("username");
				session.removeAttribute("password");
				request.setAttribute("msg", "用户退出登录！");
				request.getRequestDispatcher("/index.jsp").forward(request,
						response);
			} else {
				if (info == 10) {
					session.removeAttribute("username");
					session.removeAttribute("password");
					request.setAttribute("msg", "请求下线超时!！");
				} else if (info == 11) {
					request.setAttribute("msg", "请求下线被拒绝!！");
				} else if (info == 12) {
					request.setAttribute("msg", "请求下线出错!!");
				} else if (info == 55) {
					request.setAttribute("msg",
							"配置文件错误（Portal协议版本或认证方式配置出错！！！）");
				} else if (info == 99) {
					request.setAttribute("msg", "传递非法参数！！");
				}

				RequestDispatcher qr = request
						.getRequestDispatcher("/index.jsp");
				qr.forward(request, response);
			}
		} catch (Exception e) {
			// TODO: handle exception
			request.setAttribute("msg", "退出异常，请重新登录后再退出！");
			request.getRequestDispatcher("/index.jsp").forward(request,
					response);
		}

	}

}
