package org.overturetool.proofsupport.external_tools;

import java.io.IOException;
import java.io.InputStream;

public interface ConsoleReader {

	public String readLine() throws IOException;
	
	public void setInputStream(InputStream is);
	
	public int read(char[] cbuf) throws IOException;
}
