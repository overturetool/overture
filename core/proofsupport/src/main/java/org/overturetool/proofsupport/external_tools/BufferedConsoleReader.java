package org.overturetool.proofsupport.external_tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BufferedConsoleReader implements ConsoleReader {

	protected BufferedReader bis = null;
	
	public BufferedConsoleReader() {
	}

	public String readLine() throws IOException {
		return bis.readLine();
	}

	public void setInputStream(InputStream is) {
		bis = new BufferedReader(new InputStreamReader(is));
	}

	// TODO remove this method after refactoring MosmlHolConsoles
	public int read(char[] cbuf) throws IOException {
		return bis.read(cbuf);
	}

	public String readBlock() throws IOException {
		return readLine();
	}

	public void removeConsoleHeader() throws IOException {
		// Buffered reader doesn't know the underlying process
	}

}
