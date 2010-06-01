package org.overture.ide.debug.core.launching.debug;


/**
 * Data object to hold host/port/sessionId and static methods to load from/save
 * to {@link InterpreterConfig}
 */
public class DbgpConnectionConfig {
	private final String host;
	private final int port;
	private final String sessionId;

	private DbgpConnectionConfig(String host, int port, String sessionId) {
		this.host = host;
		this.port = port;
		this.sessionId = sessionId;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getSessionId() {
		return sessionId;
	}

//	public static DbgpConnectionConfig load(InterpreterConfig config) {
//		String host = (String) config.getProperty(DbgpConstants.HOST_PROP);
//		String port = (String) config.getProperty(DbgpConstants.PORT_PROP);
//		String sessionId = (String) config
//				.getProperty(DbgpConstants.SESSION_ID_PROP);
//		return new DbgpConnectionConfig(host, Integer.parseInt(port), sessionId);
//	}
//
//	public static void save(InterpreterConfig config, String host, int port,
//			String sessionId) {
//		config.setProperty(DbgpConstants.HOST_PROP, host);
//		config.setProperty(DbgpConstants.PORT_PROP, Integer.toString(port));
//		config.setProperty(DbgpConstants.SESSION_ID_PROP, sessionId);
//	}
}
