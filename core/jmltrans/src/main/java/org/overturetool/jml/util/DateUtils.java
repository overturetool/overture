package org.overturetool.jml.util;

import java.util.Calendar;
import java.text.SimpleDateFormat;


public class DateUtils {

	
	public static String now(String dateFormat) {
		
	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
	    return sdf.format(cal.getTime());

	  }
	
	public static String giveTime() {
		
		String res = DateUtils.now("yyyy.MM.dd G 'at' hh:mm:ss z").toString();
		
		return res;

	}
	
	
}
