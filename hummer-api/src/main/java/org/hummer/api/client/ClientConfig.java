package org.hummer.api.client;

public class ClientConfig {
	
	private HostPort hostPort;
	
	private boolean isHttp;

	public HostPort getHostPort() {
		return hostPort;
	}

	public void setHostPort(HostPort hostPort) {
		this.hostPort = hostPort;
	}

	public boolean isHttp() {
		return isHttp;
	}

	public void setHttp(boolean isHttp) {
		this.isHttp = isHttp;
	}
	
	

}
