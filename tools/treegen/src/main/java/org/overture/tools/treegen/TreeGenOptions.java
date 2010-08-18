package org.overture.tools.treegen;

// import the HashMap prototype
import java.util.HashMap;

public class TreeGenOptions {
	// constant definitions
	static private final String JOPT = new String ("javadir");
	static private final String VOPT = new String ("vppdir");
	static private final String POPT = new String ("package");
	static private final String TOPT = new String ("toplevel");
	static private final String SOPT = new String ("split");
	
	// placeholder for the file name
	private String fname;
	
	// placeholder for the file options
	private HashMap<String,String> options = new HashMap<String,String>();
	
	// default constructor (file name only)
	public TreeGenOptions (String pfname)
	{
		// consistency checks
		assert (pfname != null);
		assert (pfname.length() > 0);
		
		// remember the file name
		fname = pfname;
		
		// initialize the options (default set to empty string)
		options.put(JOPT, new String());
		options.put(VOPT, new String());
		options.put(POPT, new String());
		options.put(TOPT, new String());
		options.put(SOPT, new String("false"));
	}
	
	// auxiliary constructor (file name and all options)
	public TreeGenOptions (
			String pfname,
			String pjdir,
			String pvdir,
			String ppack,
			String ptplvl,
			boolean psplit
	) {
		// consistency checks
		assert (pfname != null);
		assert (pfname.length() > 0);
		assert (pjdir.length() > 0);
		assert (pvdir.length() > 0);
		assert (ppack.length() > 0);
		assert (ptplvl.length() > 0);
		
		// remember the file name
		fname = pfname;
		
		// initialize the options (default set to empty string)
		options.put(JOPT, new String(pjdir));
		options.put(VOPT, new String(pvdir));
		options.put(POPT, new String(ppack));
		options.put(TOPT, new String(ptplvl));
		options.put(SOPT, (psplit?new String("true"):new String("false")));
	}
	
	// get file name
	public String getFilename()
	{
		// return the file name
		return fname;
	}
		
	// set the java directory option
	public void setJavaDirectory(String pdir)
	{
		// consistency check
		if (pdir == null) return;
		if (pdir.length() == 0) return;
		
		// update the option (store a copy)
		options.put(JOPT, new String(pdir));
	}
	
	// get the java directory option
	public String getJavaDirectory()
	{
		return options.get(JOPT);
	}
	
	// set the java directory option
	public void setVppDirectory(String pdir)
	{
		// consistency check
		if (pdir == null) return;
		if (pdir.length() == 0) return;
		
		// update the option (store a copy)
		options.put(VOPT, new String(pdir));
	}
	
	// get the java directory option
	public String getVppDirectory()
	{
		return options.get(VOPT);
	}
	
	// set the directory option
	public void setPackage(String ppack)
	{
		// consistency check
		if (ppack == null) return;
		if (ppack.length() == 0) return;
		
		// update the option (store a copy)
		options.put(POPT, new String(ppack));
	}
	
	// get the directory option
	public String getPackage()
	{
		return options.get(POPT);
	}

	// set the directory option
	public void setToplevel(String ptop)
	{
		// consistency check
		if (ptop == null) return;
		if (ptop.length() == 0) return;
		
		// update the option (store a copy)
		options.put(TOPT, new String(ptop));
	}
	
	// get the directory option
	public String getToplevel()
	{
		return options.get(TOPT);
	}
	
	// set the option the split the VDM files
	public void setSplitVpp(boolean psplit)
	{
		options.put(SOPT, (psplit?new String("true"):new String("false")));		
	}
	
	// get the option to split the VDM files
	public boolean getSplitVpp()
	{
		return (options.get(SOPT).compareTo("true") == 0);
	}
}
