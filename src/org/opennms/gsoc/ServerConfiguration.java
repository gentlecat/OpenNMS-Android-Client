package org.opennms.gsoc;

public class ServerConfiguration {
	private static ServerConfiguration serverConfigurationInstance;
    private boolean https = false;
    private String host = "demo.opennms.org";
    private int port = 8980;
    private String path = "/opennms/rest";
    private String username = "demo";
    private String password = "demo";
    private String base;
    
    protected ServerConfiguration() {
    }

    public static ServerConfiguration getInstance() {
            if (serverConfigurationInstance == null) {
            	serverConfigurationInstance = new ServerConfiguration();
            }
            return serverConfigurationInstance;
    }

    public boolean getHttps() {
            return this.https;
    }
    public void setHttps(final boolean https) {
            this.https = https;
            this.base = null;
    }
    public String getHost() {
            return this.host;
    }
    public void setHost(final String host) {
            this.host = host;
            this.base = null;
    }
    public int getPort() {
            return this.port;
    }
    public void setPort(final int port) {
            this.port = port;
            this.base = null;
    }
    public String getPath() {
            return this.path;
    }
    public void setPath(final String path) {
            this.path = path;
            this.base = null;
    }
    public String getUsername() {
            return this.username;
    }
    public void setUsername(final String username) {
            this.username = username;
    }
    public String getPassword() {
            return this.password;
    }
    public void setPassword(final String password) {
            this.password = password;
    }
    
    public String getBase() {
            if (this.base == null) {
            	this.base = String.format("http%s://%s%s", (https? "s":""), this.host, this.path);
            }
            return this.base;
    }
}
