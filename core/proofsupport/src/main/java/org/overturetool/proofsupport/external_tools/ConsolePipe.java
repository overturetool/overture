package org.overturetool.proofsupport.external_tools;

import java.io.IOException;

public class ConsolePipe implements Runnable {

	protected final Console input;
	protected final Console output;
	protected String buffer = null;

	public ConsolePipe(Console input, Console output) {
		this.input = input;
		this.output = output;		
	}

	public void run() {
		try {
			while(readNextLine()) {
				writeCurrentLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	private void writeCurrentLine() {
		output.writeLine(buffer);
	}

	protected boolean readNextLine() throws IOException {
		return (buffer = input.readLine()) != null;
	}

}
