package org.overture.tools.vdmt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Util {
	
	private static final String CLASS_START = "class ";
	private static final String CLASS_END =  "end ";
	
	private static final String VDM_START = "\\begin{vdm_al}";
	private static final String VDM_END =  "\\end{vdm_al}";
	
	public static File[] GetFiles(File dir) {

		if (!dir.exists())
			return new File[0];

		ArrayList<File> allVdmFiles = new ArrayList<File>();

		for (File file : dir.listFiles()) {
			if (file.isFile())
				allVdmFiles.add(file);
			else if (!file.getName().equals(".svn"))
				for (File file2 : GetFiles(file)) {
					allVdmFiles.add(file2);
				}
		}
		File[] files = new File[allVdmFiles.size()];
		allVdmFiles.toArray(files);
		return files;
	}

	public static File[] GetFiles(File dir, String extension) {

		if (!dir.exists())
			return new File[0];

		ArrayList<File> allVdmFiles = new ArrayList<File>();

		for (File file : dir.listFiles()) {
			if (file.isFile() && file.getName().endsWith(extension))
				allVdmFiles.add(file);
			else if (file != null && file.isDirectory()
					&& !file.getName().equals(".svn"))
				for (File file2 : GetFiles(file)) {
					if (file2.getName().endsWith(extension))
						allVdmFiles.add(file2);
				}
		}
		File[] files = new File[allVdmFiles.size()];
		allVdmFiles.toArray(files);
		return files;
	}

	public static ArrayList<String> GetClasses(final String file) {
		ArrayList<String> classNames = new ArrayList<String>();
		try {
			FileReader inputFileReader = new FileReader(file);

			// Create Buffered/PrintWriter Objects
			BufferedReader inputStream = new BufferedReader(inputFileReader);

			String inLine = null;

			String currentClass = "";
			while ((inLine = inputStream.readLine()) != null) {
							
				if (inLine.trim().startsWith(CLASS_START))
				{
					String classString = inLine.trim().substring(CLASS_START.length()).trim();
					int indexOfInh = classString.indexOf(':');
					int indexOfSpace = classString.indexOf(' ');
					if(indexOfInh >= 0 || indexOfSpace >=0)
					{
						
						if(indexOfInh >= 0)
							currentClass = classString.substring(0,indexOfInh);
						else
							currentClass = classString.substring(0,indexOfSpace);
					
					}else
						currentClass = classString;
					
					classNames.add(currentClass.trim());
					//continue;
				}
				else if (inLine.trim().startsWith(CLASS_END+currentClass))
				{
					continue;
					
				}
				if (inLine.trim().startsWith(VDM_START))
				{
				    // outputStream.println("");
				    continue;
				    
				}
				if (inLine.trim().startsWith(VDM_END))
				{
					//outputStream.println("");
				continue;
				
				}
				
			
				//if (enabled)
					//outputStream.println(inLine);
				//	classNames.add(currentClass.trim());
				//else
					//outputStream.println("");
				
				
				
				
				
				
				
				
				
				
				
//				if (inLine.startsWith("class")) {
//					String className = inLine.substring(inLine.indexOf("class") + 6);
//					int end = className.indexOf(' ');
//					if (end > 0)
//						className = className.substring(0, end).trim();
//
//					classNames.add(className.trim());
//
//				}

			}

			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return classNames;
	}

	public static String GetPackage(final File sourceBase, final File file) {
		if (sourceBase.getAbsolutePath().length() < file.getParentFile().getAbsolutePath().length())
			{
			String tmp=file.getParentFile().getAbsolutePath().substring(sourceBase.getAbsolutePath().length()+1).replace('/', '.').replace('\\', '.');
			if(tmp==null || (tmp!=null && tmp.length()==0))
				return "";
			else
				return tmp;
			}else
			return "";
	}

	public static String GetPackageAsPathPart(final String sourceBase, final String file) {
		if (sourceBase.length() < file.length())
			return file.substring(sourceBase.length());
		else
			return "";
	}
}
