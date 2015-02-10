package com.leeson.portal.service.load_test;

import com.leeson.portal.model.Config;
import com.leeson.portal.service.InterfaceControl;


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
		cfg.setAuthType("CHAP");
		cfg.setTimeoutSec("3");
		cfg.setPortalVer("2");
	}

	public void run() {
		InterfaceControl.Method("PORTAL_LOGIN", "leeson", "123456", "192.168.0.100");
		InterfaceControl.Method("PORTAL_LOGINOUT", "leeson", "123456", "192.168.0.100");

	}

	public static void openServer() {

		new PortalAuthServer().start();

	}

}
