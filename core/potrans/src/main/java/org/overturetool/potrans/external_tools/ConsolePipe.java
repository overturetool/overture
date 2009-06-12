package org.overturetool.potrans.external_tools;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
				// TODO remove debug method
				debugReadMessage();
				writeCurrentLine();
				// TODO remove debug methods
				debugWriteMessage();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		debugTerminatedMessage();
	}

	private void writeCurrentLine() {
		output.writeLine(buffer);
	}

	private void debugReadMessage() {
		String formatedDate = getFormatedDate();
		System.err.println("[Thread: " + Thread.currentThread().getName() + "] [" + formatedDate + "] [R]: " + buffer);
	}
	
	private void debugWriteMessage() {
		String formatedDate = getFormatedDate();
		System.err.println("[Thread: " + Thread.currentThread().getName() + "] [" + formatedDate + "] [W]: " + buffer);
	}
	
	private void debugTerminatedMessage() {
		String formatedDate = getFormatedDate();
		System.err.println("[Thread: " + Thread.currentThread().getName() + "] [" + formatedDate + "] [T]");
	}

	protected String getFormatedDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("[yyyy/MM/dd HH:mm:ss:SSS");
		Date now = new Date();
		String formatedDate = dateFormat.format(now);
		return formatedDate;
	}

	protected boolean readNextLine() throws IOException {
		return (buffer = input.readLine()) != null;
	}

}
