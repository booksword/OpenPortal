package LeeSon.Portal.Service.LoadTest;

import LeeSon.Portal.Domain.Config;
import LeeSon.Portal.Service.Service;

/**
 * 压力测试调用
 * 
 * @author LeeSon QQ:25901875
 * 
 */
public class PortalAuthServer extends Thread {

	Config cfg = Config.getInstance();

	public PortalAuthServer() {
		cfg.setBas_ip("192.168.0.6");
		cfg.setBas_port("2000");
		cfg.setPortal_port("50100");
		cfg.setSharedSecret("LeeSon");
		cfg.setAuthType("0");
		cfg.setTimeoutSec("3");
		cfg.setPortalVer("2");
	}

	public void run() {
		new Service().Method("Login", "leeson", "iwsiqh", "27.103.192.200", cfg);
		new Service().Method("LoginOut", "leeson", "iwsiqh", "27.103.192.200",
				cfg);

	}

	public static void openServer() {

		new PortalAuthServer().start();

	}

}
