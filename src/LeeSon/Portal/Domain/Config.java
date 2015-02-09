package LeeSon.Portal.Domain;

public class Config {
	private String bas_ip;
	private String bas_port;
	private String portalVer;
	private String authType;
	private String timeoutSec;
	private String sharedSecret;
	private String portal_port;

	private static Config instance = new Config();

	private Config() {
	}

	public static Config getInstance() {
		return instance;
	}

	@Override
	public String toString() {
		return "Config [bas_ip=" + bas_ip + ", bas_port=" + bas_port
				+ ", portalVer=" + portalVer + ", authType=" + authType
				+ ", timeoutSec=" + timeoutSec + ", sharedSecret="
				+ sharedSecret + ", portal_port=" + portal_port + "]";
	}

	public String getBas_ip() {
		return bas_ip;
	}

	public void setBas_ip(String bas_ip) {
		this.bas_ip = bas_ip;
	}

	public String getBas_port() {
		return bas_port;
	}

	public void setBas_port(String bas_port) {
		this.bas_port = bas_port;
	}

	public String getPortalVer() {
		return portalVer;
	}

	public void setPortalVer(String portalVer) {
		this.portalVer = portalVer;
	}

	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	public String getTimeoutSec() {
		return timeoutSec;
	}

	public void setTimeoutSec(String timeoutSec) {
		this.timeoutSec = timeoutSec;
	}

	public String getSharedSecret() {
		return sharedSecret;
	}

	public void setSharedSecret(String sharedSecret) {
		this.sharedSecret = sharedSecret;
	}

	public String getPortal_port() {
		return portal_port;
	}

	public void setPortal_port(String portal_port) {
		this.portal_port = portal_port;
	}

}
