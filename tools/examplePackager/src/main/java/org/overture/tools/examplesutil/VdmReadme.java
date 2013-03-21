/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.tools.examplesutil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;

import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.tools.examplesutil.html.EscapeChars;
import org.overture.tools.examplesutil.util.FileUtils;

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

	private Release languageVersion = Release.DEFAULT;
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
	private Dialect dialect = Dialect.VDM_PP;
	private boolean settingsParsed = false;
	private String content = "";

	public VdmReadme(File file, String name, Dialect dialect,
			boolean autoInitialize)
	{
		this.file = file;
		this.name = name;
		this.dialect = dialect;
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

		if (!settingsParsed)
			appendReadme();

	}

	private void processLine(String line)
	{
		String[] data = line.split("=");
		if (data.length > 1)
		{
			settingsParsed = true;
			if (data[0].equals(LANGUAGE_VERSION))
				setLanguageVersion(Release.lookup(data[1]));
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

	public void writeProjectFile(File outputFolder)
	{

		File projectFile = new File(outputFolder, ".project");

		FileUtils.writeFile(getEclipseProject(), projectFile);
	}

	public String getEclipseProject()
	{
		StringBuilder sb = new StringBuilder();
		String projectNature = getNature();

		String builderArguments = getBuilderArguments();

		sb.append(OvertureProject.EclipseProject.replace(OvertureProject.NATURE_SPACEHOLDER, projectNature).replace(OvertureProject.NAME_PLACEHOLDER, name).replace(OvertureProject.ARGUMENTS_PLACEHOLDER, builderArguments).replace(OvertureProject.TEX_DOCUMENT, getTexDocument().trim()));
		return sb.toString();

	}

	private String getNature()
	{
		String projectNature = "";
		switch (dialect)
		{
			case VDM_PP:
				projectNature = OvertureProject.VDMPP_NATURE;
				break;
			case VDM_SL:
				projectNature = OvertureProject.VDMSL_NATURE;
				break;
			case VDM_RT:
				projectNature = OvertureProject.VDMRT_NATURE;
				break;

		}
		return projectNature;
	}

	public void writeReadmeContentFile(File outputFolder, String fileName)
	{
		File file = new File(outputFolder, fileName);
		FileUtils.writeFile(getReadmeContent(), file, false);
	}

	public String getReadmeContent()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(content);

		sb.append("\n\nLanguage Version: " + getLanguageVersion());
		if (getEntryPoints().size() > 0)
		{
			for (String entrypoint : entryPoints)
			{
				sb.append("\nEntry point     : " + entrypoint);
			}
		}
		return sb.toString();
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
		sb.append("\n" + getArgument(this.LANGUAGE_VERSION, languageVersion));
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

	public void createReadme()
	{
		createReadme(file);
	}

	public void createReadme(File newReadMeFile)
	{
		FileUtils.writeFile(toString(), newReadMeFile);
	}

	public void appendReadme()
	{
		StringBuilder sb = new StringBuilder();
		try
		{
			BufferedReader input = new BufferedReader(new FileReader(file));
			try
			{
				String line = null;
				while ((line = input.readLine()) != null)
				{
					if (line.startsWith("#") && line.contains("="))
						continue;
					else
						sb.append("\n" + line);
				}
			} finally
			{
				input.close();
			}
		} catch (IOException ex)
		{
			ex.printStackTrace();
		}

		FileUtils.writeFile(sb.toString() + "\n\n" + toString(), file);

	}

	public void writeLaunchFile(File folder)
	{
		File launch = new File(folder, name + ".launch");
		StringBuilder sb = new StringBuilder();
		String method = "";
		String module = "";
		String defaultModule = "";
		Boolean staticAccess = false;

		if (entryPoints.isEmpty())
			return;

		String entryPoint = entryPoints.get(0);

		if (entryPoint.contains("`") && !entryPoint.startsWith("new "))
		{
			staticAccess = true;
			module = entryPoint.substring(0, entryPoint.indexOf('`')).trim();
			method = entryPoint.substring(entryPoint.indexOf('`') + 1).trim();
			defaultModule = module;

		} else if (entryPoint.startsWith("new "))
		{

		}

		if (entryPoint.contains("`"))
		{
			module = entryPoint.substring(0, entryPoint.indexOf('`')).trim();
			method = entryPoint.substring(entryPoint.indexOf('`') + 1).trim();
		} else if (entryPoint.contains("."))
		{
			module = entryPoint.substring(4, entryPoint.indexOf(").") + 1).trim();
			defaultModule = entryPoint.substring(4, entryPoint.indexOf("(")).trim();
			method = entryPoint.substring(entryPoint.indexOf(").") + 2).trim();
		}

		String launchConfigarationId = "org.overture.ide.vdmpp.debug.launchConfigurationType";
		switch (dialect)
		{
			case VDM_SL:
				launchConfigarationId = launchConfigarationId.replace("vdmpp", "vdmsl");
				break;
			case VDM_PP:

				break;
			case VDM_RT:
				launchConfigarationId = launchConfigarationId.replace("vdmpp", "vdmrt");
				break;
		}

		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
		sb.append("\n<launchConfiguration type=\""
				+ escapeXml(launchConfigarationId) + "\">");

		sb.append("\n<intAttribute key=\"vdm_debug_session_id\" value=\"1\"/>");
		sb.append("\n<booleanAttribute key=\"vdm_launch_config_create_coverage\" value=\"true\"/>");
		sb.append("\n<booleanAttribute key=\"vdm_launch_config_enable_logging\" value=\"false\"/>");
		sb.append("\n<stringAttribute key=\"vdm_launch_config_memory_option\" value=\"\"/>");
		sb.append("\n<stringAttribute key=\"vdm_launch_config_remote_control_class\" value=\"\"/>");
		sb.append("\n<booleanAttribute key=\"vdm_launch_config_remote_debug\" value=\"false\"/>");
		sb.append("\n<stringAttribute key=\"vdm_launch_config_project\" value=\""
				+ EscapeChars.forXML(name) + "\"/>");
		sb.append("\n<stringAttribute key=\"vdm_launch_config_expression\" value=\""
				+ EscapeChars.forXML(entryPoint) + "\"/>");
		sb.append("\n<booleanAttribute key=\"vdm_launch_config_static_method\" value=\""
				+ staticAccess + "\"/>");
		sb.append("\n<stringAttribute key=\"vdm_launch_config_default\" value=\""
				+ EscapeChars.forXML(defaultModule) + "\"/>");
		sb.append("\n<stringAttribute key=\"vdm_launch_config_method\" value=\""
				+ EscapeChars.forXML(method) + "\"/>");
		sb.append("\n<stringAttribute key=\"vdm_launch_config_module\" value=\""
				+ EscapeChars.forXML(module) + "\"/>");

		sb.append("\n</launchConfiguration>");

		FileUtils.writeFile(sb.toString(), launch);

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

		sb.append("\n#" + LANGUAGE_VERSION + "=" + languageVersion);
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
			sb.append(libs.get(i).substring(0, libs.get(i).indexOf('.')));
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

	public String getSpecFileExtension()
	{
		switch (dialect)
		{
			case VDM_PP:
				return "vdmpp";
			case VDM_RT:
				return "vdmrt";
			case VDM_SL:
				return "vdmsl";

			default:
				return "vdmpp";
		}
	}

	public void setLanguageVersion(Release languageVersion)
	{
		this.languageVersion = languageVersion;
	}

	public Release getLanguageVersion()
	{
		return languageVersion;
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

	public void setDialect(Dialect dialect)
	{
		this.dialect = dialect;
	}

	public Dialect getDialect()
	{
		return dialect;
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
				this.libs.add(lib.trim() + "." + getSpecFileExtension());
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
