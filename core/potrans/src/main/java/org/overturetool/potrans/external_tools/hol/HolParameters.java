package org.overturetool.potrans.external_tools.hol;

import java.util.Map;

import org.overturetool.potrans.external_tools.SystemProperties;

public class HolParameters {

	protected final static String MOSML_LIB_DIR = "lib";
	protected final static String MOSML_BIN_DIR = "bin";
	protected final static String MOSML_BINARY = "mosml";

	protected final static String HOL_BIN_DIR = "bin";
	protected final static String UNQUOTE_BINARY = "unquote";

	protected String mosmlDir = null;
	protected String holDir = null;
	protected Map<String, String> holEnv = null;

	public HolParameters(String mosmlDir, String holDir) {
		this.mosmlDir = mosmlDir;
		this.holDir = holDir;
		this.holEnv = new HolEnvironmentBuilder(mosmlDir, MOSML_LIB_DIR,
				MOSML_BIN_DIR).getEnvironment();
	}

	public String getMosmlBinaryPath() {
		StringBuffer sb = new StringBuffer(mosmlDir).append(
				SystemProperties.FILE_SEPARATOR).append(MOSML_BIN_DIR).append(
				MOSML_BINARY);
		return sb.toString();
	}

	public String getUnquoteCommand() {
		StringBuffer sb = new StringBuffer(holDir).append(
				SystemProperties.FILE_SEPARATOR).append(HOL_BIN_DIR).append(
				UNQUOTE_BINARY);
		return sb.toString();
	}

	public String getHolDir() {
		return holDir;
	}

	public void setHolDir(String holDir) {
		this.holDir = holDir;
	}

	public Map<String, String> getHolEnv() {
		return holEnv;
	}

	public void setHolEnv(Map<String, String> holEnv) {
		this.holEnv = holEnv;
	}
}
