//package org.overture.ide.debug.core.launching.debug;
//
//
///**
// * use {@link DbgpConnectionConfig}
// * 
// * @deprecated
// */
//public class DbgpInterpreterConfig {
//	private InterpreterConfig config;
//
//	public DbgpInterpreterConfig(InterpreterConfig config) {
//		this.config = config;
//	}
//
//	public InterpreterConfig getConfig() {
//		return config;
//	}
//
//	public void setHost(String host) {
//		config.setProperty(DbgpConstants.HOST_PROP, host);
//	}
//
//	public String getHost() {
//		return (String) config.getProperty(DbgpConstants.HOST_PROP);
//	}
//
//	public void setPort(int port) {
//		config.setProperty(DbgpConstants.PORT_PROP, Integer.toString(port));
//	}
//
//	public int getPort() {
//		return Integer.parseInt((String) config
//				.getProperty(DbgpConstants.PORT_PROP));
//	}
//
//	public void setSessionId(String sessionId) {
//		config.setProperty(DbgpConstants.SESSION_ID_PROP, sessionId);
//	}
//
//	public String getSessionId() {
//		return (String) config.getProperty(DbgpConstants.SESSION_ID_PROP);
//	}
//}
