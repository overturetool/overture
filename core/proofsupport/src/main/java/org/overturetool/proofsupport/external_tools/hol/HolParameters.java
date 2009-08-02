package org.overturetool.proofsupport.external_tools.hol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.overturetool.proofsupport.external_tools.Utilities;

public class HolParameters {

	protected final static String MOSML_LIB_DIR = "lib";
	protected final static String MOSML_BIN_DIR = "bin";
	protected final static String MOSML_BINARY = "mosml";

	protected final static String HOL_BIN_DIR = "bin";
	protected final static String UNQUOTE_BINARY = "unquote";

	protected String mosmlDir = null;
	protected String holDir = null;

	protected static String commandArgumentsFormat = "-quietdec -P full -I _HOLDIR_/sigobj "
			+ "_HOLDIR_/std.prelude "
			+ "_HOLDIR_/tools/unquote-init.sml "
			+ "_HOLDIR_/tools/end-init-boss.sml";

	public HolParameters(String mosmlDir, String holDir) {
		this.mosmlDir = mosmlDir;
		this.holDir = holDir;
	}

	public String getMosmlBinaryPath() {
		StringBuffer sb = new StringBuffer(mosmlDir).append(
				Utilities.FILE_SEPARATOR).append(MOSML_BIN_DIR).append(
				Utilities.FILE_SEPARATOR).append(MOSML_BINARY);
		return sb.toString();
	}

	public String getUnquoteBinaryPath() {
		StringBuffer sb = new StringBuffer(holDir).append(
				Utilities.FILE_SEPARATOR).append(HOL_BIN_DIR).append(
				Utilities.FILE_SEPARATOR).append(UNQUOTE_BINARY);
		return sb.toString();
	}

	public String getHolDir() {
		return holDir;
	}

	public void setHolDir(String holDir) {
		this.holDir = holDir;
	}

	protected String[] formatCommandArguments() {
		String[] preFormatedArgs = commandArgumentsFormat.split(" ");
		String[] formatedArgs = new String[preFormatedArgs.length];
		for (int i = 0; i < preFormatedArgs.length; i++)
			formatedArgs[i] = preFormatedArgs[i].replaceAll("_HOLDIR_", Matcher
					.quoteReplacement(holDir));
		return formatedArgs;
	}

	public List<String> buildMosmlHolCommand() {
		String[] mosmlArguments = formatCommandArguments();
		ArrayList<String> list = new ArrayList<String>(
				mosmlArguments.length + 1);
		list.add(getMosmlBinaryPath());
		for (String argument : mosmlArguments)
			list.add(argument);
		return list;
	}

	public List<String> buildUnquoteCommand() {
		ArrayList<String> list = new ArrayList<String>(1);
		list.add(getUnquoteBinaryPath());
		return list;
	}
}
