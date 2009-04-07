package org.overture.tools.vdmt.VDMToolsProxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class VdmJavaFile {
	final String IMPORT_TAG_START = "// ***** VDMTOOLS START Name=imports";
	final String IMPORT_TAG_END = "// ***** VDMTOOLS END Name=imports";

	/*
	 * 
	 * // VDMTOOLS START Name=imports KEEP=NO
	 * 
	 * import jp.co.csk.vdm.toolbox.VDM.; import java.util.; // VDMTOOLS END
	 * Name=imports
	 */
	public VdmJavaFile(File file) {
		this.file = file;
	}

	private File file;

	public void AddPackages(List<String> packages) {

		ArrayList<String> packagesInFile = new ArrayList<String>();
		try {
			FileReader inputFileReader = new FileReader(file);
		

			// Create Buffered/PrintWriter Objects
			BufferedReader inputStream = new BufferedReader(inputFileReader);
			StringBuilder sb = new StringBuilder();

			String inLine = null;
			Boolean tagFound = false;

			while ((inLine = inputStream.readLine()) != null) {
				if (inLine.trim().startsWith(IMPORT_TAG_START)) {
					sb.append("\n"+inLine);
					tagFound = true;
					continue;

				} else if (inLine.trim().startsWith(IMPORT_TAG_END)) {
					
					for (String packageName : packages) {
						if (!packagesInFile.contains(packageName)) {
							sb.append("\n"+CreateImportLine(packageName));
						}
					}
					sb.append("\n"+inLine);
					tagFound = false;
					continue;
				}
				if (tagFound && inLine.length()>0) {
					packagesInFile.add(ParseImport(inLine));
				}

				sb.append("\n"+inLine);

			}
			inputStream.close();
			FileWriter outputFileReader = new FileWriter(file);
			BufferedWriter outputStream = new BufferedWriter(outputFileReader);
			outputStream.write(sb.toString());
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private String ParseImport(String line) {
		String packageName = line.replace("import ", "").trim();
		int lastLetter=0;
		for (int i = packageName.length()-1; i >0; i--) {
			if(Character.isLetter( packageName.charAt(i)))
			{
				lastLetter = i+1;
				break;
			}
		}
		packageName = packageName.substring(0, lastLetter);
		return packageName;
	}

	private String CreateImportLine(String packageName) {
		return "import " + packageName.trim() + ".*;";
	}
}
