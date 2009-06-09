package org.overturetool.potrans.external_tools.hol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;


public class HolInnerThread implements Runnable {

	private final HolParameters holParam;
	private final BufferedReader input;
	private final PrintWriter output;
	private boolean quit = false;
	
	public HolInnerThread(HolParameters holParam, InputStream input, OutputStream output) {
		this.holParam = holParam;
		this.input = new BufferedReader(new InputStreamReader(input));
		this.output = new PrintWriter(output);
	}

	public void run() {
		UnquoteConsole unquote = null;
		MosmlHolConsole hol = null;
		try {
			unquote = new UnquoteConsole(holParam.getUnquoteCommandPath());
			hol = new MosmlHolConsole(holParam.getMosmlBinaryPath(), holParam.getHolDir());
			hol.removeConsoleHeader();

			pipeText(unquote, hol);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			closeInput();
			closeOutput();
			terminateMosmlHolConsole(unquote, hol);
			terminateUnquoteConsole(unquote);
		}
	}

	private void terminateUnquoteConsole(UnquoteConsole unquote) {
		try {
			unquote.terminate();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		unquote.destroy();
	}

	private void terminateMosmlHolConsole(UnquoteConsole unquote, MosmlHolConsole hol) {
		try {
			hol.terminate();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void closeOutput() {
		output.flush();
		output.close();
	}

	private void closeInput() {
		try {
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void pipeText(UnquoteConsole unquote, MosmlHolConsole hol)
			throws IOException {
		String line = "";
		while((line = input.readLine()) != null) {
			hol.writeLine(unquote.writeAndReadLine(line));
			output.println(hol.readOutputBlock());
		}
	}

	public boolean isQuit() {
		return quit;
	}

	public void quit() {
		this.quit = true;
	}

}
