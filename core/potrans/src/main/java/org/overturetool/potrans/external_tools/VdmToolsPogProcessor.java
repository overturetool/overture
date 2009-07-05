package org.overturetool.potrans.external_tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class VdmToolsPogProcessor implements PogProcessor {

	protected String lineBuffer = "";
	protected List<String> poBuffer = new LinkedList<String>();
	protected List<String[]> poLines = new LinkedList<String[]>();;

	public VdmToolsPogProcessor() {
	}

	private void initObjectState() {
		initBuffers();
		poLines = new LinkedList<String[]>();
	}

	public List<String[]> extractPosFromFile(String pogFileName) throws PogProcessorException {
		initObjectState();

		BufferedReader fin;
		try {
			fin = new BufferedReader(new FileReader(pogFileName));
			readPos(fin);
		} catch (FileNotFoundException e) {
			throw new PogProcessorException("Can't find proof obligations file '" + pogFileName + "'.", e);
		} catch (IOException e) {
			throw new PogProcessorException("IO error while reading proof obligations file '" + pogFileName + "'.", e);
		}

		return poLines;
	}

	protected void initBuffers() {
		lineBuffer = "";
		poBuffer = new LinkedList<String>();
	}

	protected void readPos(BufferedReader fin) throws IOException {
		while (readNextLine(fin)) {
			handleLine();
		}
		finalizeRead();
	}

	private void finalizeRead() {
		if (!isBufferEmpty()) {
			poLines.add(buildPoArray());
		}
	}

	protected String[] buildPoArray() {
		return poBuffer.toArray(new String[poBuffer.size()]);
	}

	protected boolean isBufferEmpty() {
		return poBuffer.size() == 0;
	}

	protected void handleLine() {
		if (!lineBuffer.equals("")) {
			poBuffer.add(lineBuffer);
		} else {
			poLines.add(buildPoArray());
			initBuffers();
		}
	}

	private boolean readNextLine(BufferedReader fin) throws IOException {
		return (lineBuffer = fin.readLine()) != null;
	}

	public String extractPoExpression(String[] poLines) {
		StringBuffer poExpression = new StringBuffer();
		// records that loop index is now pointing to lines
		// that are part of the PO expression.
		boolean inPoExpression = false;

		for (int i = 0; i < poLines.length; i++) {
			String line = poLines[i];
			if (inPoExpression)
				poExpression.append(line).append(Utilities.LINE_SEPARATOR);
			if (line.startsWith("----"))
				inPoExpression = true;
		}
		poExpression.deleteCharAt(poExpression.length() - 1);

		return poExpression.toString();
	}

	public List<String> extractPoExpressions(List<String[]> poList) {
		List<String> result = new ArrayList<String>(poList.size());
		for (String[] poLines : poList)
			result.add(extractPoExpression(poLines));
		return result;
	}
}
