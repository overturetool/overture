package org.overturetool.potrans.external_tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UnquoteConsole extends Console {

	public UnquoteConsole(String command) throws IOException {
		super(buildCommandList(command));
	}
}
