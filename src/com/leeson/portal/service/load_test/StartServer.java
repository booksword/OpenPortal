package com.leeson.portal.service.load_test;

/**
 * 压力测试入口
 * 
 * @author LeeSon QQ:25901875
 * 
 */
public class StartServer {

	public static void main(String[] args) {
		// TODO Auto-generated mewhile(true){
		for (int i = 0; i <= 100000; i++) {
			new Thread() {
				public void run() {
					try {
						PortalAuthServer.openServer();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				};

			}.start();
		}
		
		
		for (int i = 0; i <= 100000; i++) {
			new Thread() {
				public void run() {
					try {
						PortalAuthServer.openServer();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				};

			}.start();
		}

	}

}
