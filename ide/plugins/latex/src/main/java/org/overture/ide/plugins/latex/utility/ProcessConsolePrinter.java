package org.overture.ide.plugins.latex.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.overture.ide.ui.internal.util.ConsoleWriter;



public class ProcessConsolePrinter extends Thread
{
	ConsoleWriter cw = null;
	InputStream stream = null;
	List<String> fails = new ArrayList<String>();
	PdfLatex pdfLatex = null;

	public ProcessConsolePrinter(ConsoleWriter cw, InputStream inputStream, PdfLatex pdfLatex) {
		this.cw = cw;
		this.stream = inputStream;
		this.pdfLatex = pdfLatex;
	}

	@Override
	public void run()
	{

		String line = null;
		BufferedReader input = new BufferedReader(new InputStreamReader(stream));
		try
		{
			while ((line = input.readLine()) != null)
			{
				detectFail(line);
				if (cw != null)
					cw.println(line);
				else
					System.out.println(line);
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<String> getFails(){
		return this.fails;
	}
	
	private void detectFail(String line) {
		if(line.contains("Emergency stop") || line.contains("LaTeX Error")){
			fails.add(line);
			pdfLatex.setLatexFail(true);
		}
		
	}
}
