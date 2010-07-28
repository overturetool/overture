package org.overturetool.tools.packworkspace;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.lex.Dialect;

public class VdmReadme
{
	public enum ResultStatus {
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
	private final String VM_ARGUMENTS="VM_ARGUMENTS";

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
			boolean autoInitialize) {
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

			BufferedReader input = new BufferedReader(new FileReader(file));
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
				setLibs(data[1].trim().split(";"));
			else if (data[0].equals(VM_ARGUMENTS))
				setVmArguments(data[1].trim().split(";"));
		}
	}

	public void writeProjectFile(File outputFolder)
	{
		FileWriter outputFileReader;

		try
		{
			outputFileReader = new FileWriter(new File(outputFolder, ".project"));
			BufferedWriter outputStream = new BufferedWriter(outputFileReader);

			String projectNature = getNature();

			String builderArguments = getBuilderArguments();

			outputStream.write(OvertureProject.EclipseProject.replace(OvertureProject.NATURE_SPACEHOLDER,
					projectNature)
					.replace(OvertureProject.NAME_PLACEHOLDER, name)
					.replace(OvertureProject.ARGUMENTS_PLACEHOLDER,
							builderArguments)
					.replace(OvertureProject.TEX_DOCUMENT,
							getTexDocument().trim()));
			outputStream.flush();
			outputStream.close();

		} catch (IOException e)
		{
			e.printStackTrace();

		}
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
		FileWriter outputFileReader;

		try
		{
			outputFileReader = new FileWriter(new File(outputFolder, fileName),
					false);
			BufferedWriter outputStream = new BufferedWriter(outputFileReader);
			outputStream.write(content);
			outputStream.write("\n\nLanguage Version: " + getLanguageVersion());
			if (getEntryPoints().size() > 0)
				for (String entrypoint : entryPoints)
				{
					outputStream.write("\nEntry point     : " + entrypoint);
				}
			outputStream.flush();
			outputStream.close();

		} catch (IOException e)
		{
			e.printStackTrace();

		}
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

		// if(newReadMeFile.exists())
		// return;

		FileWriter outputFileReader;
		try
		{
			outputFileReader = new FileWriter(newReadMeFile, false);
			BufferedWriter outputStream = new BufferedWriter(outputFileReader);

			outputStream.write(toString());

			outputStream.flush();
			outputStream.close();

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

		FileWriter outputFileReader;
		try
		{
			outputFileReader = new FileWriter(file, false);
			BufferedWriter outputStream = new BufferedWriter(outputFileReader);

			outputStream.write(sb.toString() + "\n\n" + toString());

			outputStream.flush();
			outputStream.close();

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void writeLaunchFile(File folder)
	{
		File launch = new File(folder, name + ".launch");
		StringBuilder sb = new StringBuilder();
		String method = "";
		String module = "";

		if (entryPoints.isEmpty())
			return;

		String entryPoint = entryPoints.get(0);
		if (entryPoint.contains("`"))
		{
			module = entryPoint.substring(0, entryPoint.indexOf('`')).trim();
			method = entryPoint.substring(entryPoint.indexOf('`') + 1).trim();
		} else if (entryPoint.contains("."))
		{
			module = entryPoint.substring(0, entryPoint.indexOf(").") + 1)
					.trim()
					.replace("new ", "");
			method = entryPoint.substring(entryPoint.indexOf(").") + 2).trim();
		}

		String launchConfigarationId = "org.overture.ide.vdmpp.debug.core.launchConfigurationTypeVDMJ";
		switch (dialect)
		{
		case VDM_SL:
			launchConfigarationId = launchConfigarationId.replace("vdmpp",
					"vdmsl");
			break;
		case VDM_PP:

			break;
		case VDM_RT:
			launchConfigarationId = launchConfigarationId.replace("vdmrt",
					"vdmrt");
			break;
		}

		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
		sb.append("\n<launchConfiguration type=\""
				+ escapeXml(launchConfigarationId) + "\">");
		sb.append("\n<booleanAttribute key=\"create_coverage\" value=\"true\"/>");
		sb.append("\n<booleanAttribute key=\"dbgp_break_on_first_line\" value=\"false\"/>");
		sb.append("\n<intAttribute key=\"dbgp_connection_timeout\" value=\"5000\"/>");
		sb.append("\n<booleanAttribute key=\"dbgp_enable_logging\" value=\"true\"/>");
		sb.append("\n<booleanAttribute key=\"enableBreakOnFirstLine\" value=\"false\"/>");
		sb.append("\n<booleanAttribute key=\"enableDbgpLogging\" value=\"false\"/>");
		sb.append("\n<stringAttribute key=\"mainScript\" value=\".project\"/>");
		sb.append("\n<stringAttribute key=\"nature\" value=\""
				+ escapeXml(getNature()) + "\"/>");
		// sb.append("\n<listAttribute key=\"org.eclipse.debug.core.MAPPED_RESOURCE_PATHS\">");
		// sb.append("\n<listEntry value=\"/dansk/.project\"/>");
		// sb.append("\n</listAttribute>");
		// sb.append("\n<listAttribute key=\"org.eclipse.debug.core.MAPPED_RESOURCE_TYPES\">");
		// sb.append("\n<listEntry value=\"1\"/>");
		// sb.append("\n</listAttribute>");
		sb.append("\n<stringAttribute key=\"project\" value=\""
				+ escapeXml(name) + "\"/>");
		sb.append("\n<booleanAttribute key=\"remote_debug\" value=\"false\"/>");
		sb.append("\n<stringAttribute key=\"vdmDebuggingMethod\" value=\""
				+ escapeXml(method) + "\"/>");
		sb.append("\n<stringAttribute key=\"vdmDebuggingModule\" value=\""
				+ escapeXml(module) + "\"/>");
		sb.append("\n<stringAttribute key=\"vdmDebuggingRemoteControlClass\" value=\"\"/>");
		sb.append("\n</launchConfiguration>");

		FileWriter outputFileReader;
		try
		{
			outputFileReader = new FileWriter(launch, false);
			BufferedWriter outputStream = new BufferedWriter(outputFileReader);

			outputStream.write(sb.toString());

			outputStream.flush();
			outputStream.close();

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static String escapeXml(String data)
	{
		return data.replace("&", "&amp;")
				.replace("\"", "&quot;")
				.replace("<", "&lt;")
				.replace(">", " &gt;")
				.replace("'", "&apos;")
				.replace(" ", "");
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
		sb.append("\n#" + LIB + "=" );
		for (int i = 0; i < libs.size(); i++)
		{
			if(i!=0)
				sb.append(";");
			sb.append(libs.get(i).substring(0,libs.get(i).indexOf('.')));
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
				this.libs.add(lib.trim() +"."+ getSpecFileExtension());
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
