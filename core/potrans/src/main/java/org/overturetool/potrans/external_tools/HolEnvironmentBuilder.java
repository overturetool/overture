package org.overturetool.potrans.external_tools;

import java.util.HashMap;
import java.util.Map;

public class HolEnvironmentBuilder {

	protected final static String PATH = "PATH";
	protected final static String DYLD_LIBRARY_PATH = "DYLD_LIBRARY_PATH";
	protected final static String MOSMLHOME = "MOSMLHOME";
	protected final static String MOSMLHOMEVAR = "${" + MOSMLHOME + "}";
	
	protected final String mosmlLibDir;
	protected final String mosmlBinDir;
	protected final String mosmlDir;
	
	
	public HolEnvironmentBuilder(String mosmlDir, String mosmlLibDir, String mosmlBinDir) {
		this.mosmlDir = mosmlDir;
		this.mosmlLibDir = mosmlLibDir;
		this.mosmlBinDir = mosmlBinDir;
	}
	
	public Map<String,String> getEnvironment() {
		HashMap<String,String> map = new HashMap<String,String>(3);
		map.put(MOSMLHOME, mosmlDir);
		map.put(DYLD_LIBRARY_PATH, MOSMLHOMEVAR + SystemProperties.FILE_SEPARATOR + mosmlLibDir);
		map.put(PATH, MOSMLHOMEVAR + SystemProperties.FILE_SEPARATOR + mosmlBinDir);
		
		return map;
	}

}
