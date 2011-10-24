package org.overturetool.test.framework.examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;

public class VdmReadme
{
	public enum ResultStatus
	{
		NO_ERROR_SYNTAX, NO_ERROR_TYPE_CHECK, NO_CHECK, NO_ERROR_PO, NO_ERROR_INTERPRETER
	}

	File file;
	private final String LANGUAGE_VERSION = "LANGUAGE_VERSION";
	private final String INV_CHECKS = "INV_CHECKS";
	private final String POST_CHECKS = "POST_CHECKS";
	private final String PRE_CHECKS = "PRE_CHECKS";
	private final String DYNAMIC_TYPE_CHECKS = "DYNAMIC_TYPE_CHECKS";
	private final String SUPPRESS_WARNINGS = "SUPPRESS_WARNINGS";
	private final String ENTRY_POINT = "ENTRY_POINT";
	private final String EXPECTED_RESULT = "EXPECTED_RESULT";
	private final String TEX_DOCUMENT = "DOCUMENT";
	private final String LIB = "LIB";
	private final String TEX_AUTHOR = "AUTHOR";
	private final String ENCODING = "ENCODING";
	private final String VM_ARGUMENTS = "VM_ARGUMENTS";

	private Boolean invChecks = true;
	private Boolean postChecks = true;
	private Boolean preChecks = true;
	private Boolean dynamicTypeChecks = true;
	private Boolean suppressWarnings = false;
	private final List<String> entryPoints = new Vector<String>();
	private ResultStatus expectedResult = ResultStatus.NO_ERROR_TYPE_CHECK;
	private String texDocument = "";
	private String texAuthor = "";
	private String encoding = "";
	private List<String> libs = new Vector<String>();
	private List<String> vmArguments = new Vector<String>();
	private String name = "";
	private boolean settingsParsed = false;
	private String content = "";
	private String release;

	public VdmReadme(File file, String name, 
			boolean autoInitialize)
	{
		this.file = file;
		this.name = name;
		if (autoInitialize)
			initialize();
	}

	public void initialize()
	{
		try
		{

			BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			try
			{
				String line = null;
				StringBuilder sb = new StringBuilder();
				while ((line = input.readLine()) != null)
				{
					if (line.startsWith("#") && line.contains("="))
						processLine(line.substring(1).trim());
					if (!line.startsWith("#"))
						sb.append("\n" + line);
				}
				content = sb.toString();
			} finally
			{
				input.close();
			}
		} catch (IOException ex)
		{
			ex.printStackTrace();
		}

//		if (!settingsParsed)
//			appendReadme();

	}

	private void processLine(String line)
	{
		String[] data = line.split("=");
		if (data.length > 1)
		{
			settingsParsed = true;
			if (data[0].equals(LANGUAGE_VERSION))
				setLanguageVersion(data[1]);
			else if (data[0].equals(INV_CHECKS))
				setInvChecks(new Boolean(data[1]));
			else if (data[0].equals(POST_CHECKS))
				setPostChecks(new Boolean(data[1]));
			else if (data[0].equals(PRE_CHECKS))
				setPreChecks(new Boolean(data[1]));
			else if (data[0].equals(DYNAMIC_TYPE_CHECKS))
				setDynamicTypeChecks(new Boolean(data[1]));
			else if (data[0].equals(SUPPRESS_WARNINGS))
				setSuppressWarnings(new Boolean(data[1]));
			else if (data[0].equals(ENTRY_POINT))
				setEntryPoint(data[1].trim());
			else if (data[0].equals(EXPECTED_RESULT))
				setExpectedResult(ResultStatus.valueOf(data[1]));
			else if (data[0].equals(TEX_DOCUMENT))
				setTexDocument(data[1].trim());
			else if (data[0].equals(TEX_AUTHOR))
				setTexAuthor(data[1].trim());
			else if (data[0].equals(ENCODING))
				setEncoding(data[1].trim());
			else if (data[0].equals(LIB))
				setLibs(fixSemiSplit(data[1]));
			else if (data[0].equals(VM_ARGUMENTS))
				setVmArguments(fixSemiSplit(data[1]));
		}
	}

	private void setLanguageVersion(String string)
	{
		this.release = string;
	}

	public String getLanguageVersion()
	{
		return this.release ;
	}
	static String[] fixSemiSplit(String text)
	{
		String splitter = ",";
		if (text.contains(splitter))
		{
			return text.trim().split(splitter);
		} 
		
		splitter=";";
		if (text.contains(splitter))
		{
			return text.trim().split(splitter);
		}
		return new String[] { text };

	}




	private String getBuilderArguments()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\n"
				+ getArgument(this.DYNAMIC_TYPE_CHECKS, dynamicTypeChecks));
		sb.append("\n" + getArgument(this.INV_CHECKS, invChecks));
		sb.append("\n" + getArgument(this.POST_CHECKS, postChecks));
		sb.append("\n" + getArgument(this.PRE_CHECKS, preChecks));
		sb.append("\n" + getArgument(this.SUPPRESS_WARNINGS, suppressWarnings));
		return sb.toString();
	}

	private String getArgument(String key, Object value)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\n\t\t\t\t<dictionary>");
		sb.append("\n\t\t\t\t\t<key>" + key + "</key>");
		sb.append("\n\t\t\t\t\t<value>" + value + "</value>");
		sb.append("\n\t\t\t\t</dictionary>");
		return sb.toString();
	}




	private static String escapeXml(String data)
	{
		return data.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", " &gt;").replace("'", "&apos;").replace(" ", "");
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\n#******************************************************");
		sb.append("\n#  AUTOMATED TEST SETTINGS");
		sb.append("\n#------------------------------------------------------");

//		sb.append("\n#" + LANGUAGE_VERSION + "=" + languageVersion);
		sb.append("\n#" + INV_CHECKS + "=" + invChecks);
		sb.append("\n#" + POST_CHECKS + "=" + postChecks);
		sb.append("\n#" + PRE_CHECKS + "=" + preChecks);
		sb.append("\n#" + DYNAMIC_TYPE_CHECKS + "=" + dynamicTypeChecks);
		sb.append("\n#" + SUPPRESS_WARNINGS + "=" + suppressWarnings);
		// sb.append("\n#"+ENTRY_POINT+"="+entryPoints);
		if (getEntryPoints().size() > 0)
			for (String entrypoint : entryPoints)
			{
				sb.append("\n#" + ENTRY_POINT + "=" + entrypoint);
			}
		sb.append("\n#" + EXPECTED_RESULT + "=" + expectedResult);
		sb.append("\n#" + ENCODING + "=" + encoding);

		sb.append("\n#" + TEX_DOCUMENT + "=" + texDocument);
		sb.append("\n#" + LIB + "=");
		for (int i = 0; i < libs.size(); i++)
		{
			if (i != 0)
				sb.append(";");
			sb.append(libs.get(i));
		}
		sb.append("\n#" + TEX_AUTHOR + "=" + encoding);

		// sb.append("\n#LANGUAGE_VERSION=vdm10");
		// sb.append("\n#INV_CHECKS=true");
		// sb.append("\n#POST_CHECKS=true");
		// sb.append("\n#PRE_CHECKS=true");
		// sb.append("\n#DYNAMIC_TYPE_CHECKS=true");
		// sb.append("\n#SUPPRESS_WARNINGS=false");
		// sb.append("\n#ENTRY_POINT=new Test1().Run()");
		// sb.append("\n#EXPECTED_RESULT=NO_ERROR_INTERPRETER");
		sb.append("\n#******************************************************");
		return sb.toString();
	}



	public void setInvChecks(Boolean invChecks)
	{
		this.invChecks = invChecks;
	}

	public Boolean getInvChecks()
	{
		return invChecks;
	}

	public void setPostChecks(Boolean postChecks)
	{
		this.postChecks = postChecks;
	}

	public Boolean getPostChecks()
	{
		return postChecks;
	}

	public void setPreChecks(Boolean preChecks)
	{
		this.preChecks = preChecks;
	}

	public Boolean getPreChecks()
	{
		return preChecks;
	}

	public void setDynamicTypeChecks(Boolean dynamicTypeChecks)
	{
		this.dynamicTypeChecks = dynamicTypeChecks;
	}

	public Boolean getDynamicTypeChecks()
	{
		return dynamicTypeChecks;
	}

	public void setSuppressWarnings(Boolean suppressWarnings)
	{
		this.suppressWarnings = suppressWarnings;
	}

	public Boolean getSuppressWarnings()
	{
		return suppressWarnings;
	}

	public void setEntryPoint(String entryPoint)
	{
		this.entryPoints.add(entryPoint);
	}

	public List<String> getEntryPoints()
	{
		return entryPoints;
	}

	public void setExpectedResult(ResultStatus expectedResult)
	{
		this.expectedResult = expectedResult;
	}

	public ResultStatus getExpectedResult()
	{
		return expectedResult;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}


	public void setTexDocument(String texDocument)
	{
		this.texDocument = texDocument;
	}

	public String getTexDocument()
	{
		return texDocument;
	}

	public File getWorkingDirectory()
	{
		return file.getParentFile();
	}

	public void setTexAuthor(String texAuthor)
	{
		this.texAuthor = texAuthor;
	}

	public String getTexAuthor()
	{
		return texAuthor;
	}

	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}

	public String getEncoding()
	{
		return encoding;
	}

	public void setLibs(String[] argumentLibs)
	{
		for (String lib : argumentLibs)
		{
			if (lib.trim().length() > 0)
				this.libs.add(lib.trim());
		}

	}

	public List<String> getLibs()
	{
		return libs;
	}

	public void setVmArguments(String[] argumentLibs)
	{
		for (String arg : argumentLibs)
		{
			if (arg.trim().length() > 0)
				this.vmArguments.add(arg.trim());
		}

	}

	public List<String> getVmArguments()
	{
		return vmArguments;
	}

	public String getContent()
	{
		return content;
	}
}
