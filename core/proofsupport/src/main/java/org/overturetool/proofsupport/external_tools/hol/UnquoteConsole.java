package org.overturetool.proofsupport.external_tools.hol;

import java.io.IOException;

import org.overturetool.proofsupport.external_tools.Console;

public class UnquoteConsole extends Console {

	public UnquoteConsole(String command) throws IOException {
		super(buildCommandList(command));
	}
}
