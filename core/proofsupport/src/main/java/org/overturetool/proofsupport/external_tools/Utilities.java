package org.overturetool.proofsupport.external_tools;

import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import org.overturetool.proofsupport.external_tools.hol.HolCodeReader;

public abstract class Utilities {

	public final static String PATH_SEPARATOR = System.getProperty("path.separator");
	public final static String FILE_SEPARATOR = System.getProperty("file.separator");
	public final static String LINE_SEPARATOR = System.getProperty("line.separator");
	public final static String NEW_CHARACTER = "\n";
	
	public final static String getFormatedDate(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
		String formatedDate = dateFormat.format(date);
		return formatedDate;
	}
	
	public final static String readHolCodeFile(String fileName) throws IOException {
		StringBuffer sb = new StringBuffer();
		HolCodeReader reader = new HolCodeReader(new FileReader(fileName));
		String lineBuffer = "";
		while((lineBuffer = reader.readLine()) != null)
			sb.append(lineBuffer).append(LINE_SEPARATOR);
		reader.close();
		return sb.toString();
	}
	
	public final static String[] splitString(String input, String regExp) {
		return splitString(input, regExp, false);
	}
	
	public final static String[] splitString(String input, String regExp, boolean removeEmpty) {
		String[] parts = input.split(regExp);
		if(removeEmpty) {
			return filterNonEmptyStirngs(parts).toArray(new String[] {});
		}
		else {
			return parts;
		}
	}

	private static LinkedList<String> filterNonEmptyStirngs(String[] parts) {
		LinkedList<String> filtered = new LinkedList<String>();
		for(String part : parts)
			if(!isEmptyString(part))
				filtered.add(part);
		return filtered;
	}
	
	public final static boolean isEmptyString(String s) {
		return s == null || s.equals("");
	}
}
