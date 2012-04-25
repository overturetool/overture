package org.overturetool.proofsupport.external_tools;

import java.io.IOException;
import java.io.InputStream;

public interface ConsoleReader {

	public String readLine() throws IOException;
	
	public String readBlock() throws IOException;
	
	public void removeConsoleHeader() throws IOException;
	
	public void setInputStream(InputStream is);
	
	public boolean isReady() throws IOException;
}
