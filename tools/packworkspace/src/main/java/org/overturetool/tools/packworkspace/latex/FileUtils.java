package org.overturetool.tools.packworkspace.latex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileUtils{

	  public static List<String> readTextFromJar(String s) {
	    InputStream is = null;
	    BufferedReader br = null;
	    String line;
	    List<String> list = new ArrayList<String>();

	    try { 
	      is = FileUtils.class.getResourceAsStream(s);
	      br = new BufferedReader(new InputStreamReader(is));
	      while (null != (line = br.readLine())) {
	         list.add(line);
	      }
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	    }
	    finally {
	      try {
	        if (br != null) br.close();
	        if (is != null) is.close();
	      }
	      catch (IOException e) {
	        e.printStackTrace();
	      }
	    }
	    return list;
	  }

	 
	}
