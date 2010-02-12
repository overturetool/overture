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
		NO_ERROR_INTERPRETER, NO_ERROR_TYPE_CHECK, NO_ERROR_SYNTAX, NO_CHECK
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

	private Release languageVersion = Release.DEFAULT;
	private Boolean invChecks = true;
	private Boolean postChecks = true;
	private Boolean preChecks = true;
	private Boolean dynamicTypeChecks = true;
	private Boolean suppressWarnings = false;
	private final List<String> entryPoints = new Vector<String>();
	private ResultStatus expectedResult = ResultStatus.NO_ERROR_TYPE_CHECK;
	private String name = "";
	private Dialect dialect = Dialect.VDM_PP;
	private boolean settingsParsed=false;
	private String content="";

	public VdmReadme(File file, String name,Dialect dialect,boolean autoInitialize) {
		this.file = file;
		this.name = name;
		this.dialect = dialect;
		if(autoInitialize)
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
					if(!line.startsWith("#"))
						sb.append("\n"+line);
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
		
		if(!settingsParsed)
			appendReadme();

	}

	private void processLine(String line)
	{
		String[] data = line.split("=");
		if (data.length > 1)
		{
			settingsParsed=true;
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
		}
	}

	public void writeProjectFile(File outputFolder)
	{
		FileWriter outputFileReader;

		try
		{
			outputFileReader = new FileWriter(new File(outputFolder, ".project"));
			BufferedWriter outputStream = new BufferedWriter(outputFileReader);

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

			String builderArguments = getBuilderArguments();

			outputStream.write(OvertureProject.EclipseProject.replace(OvertureProject.NATURE_SPACEHOLDER,
					projectNature)
					.replace(OvertureProject.NAME_PLACEHOLDER, name)
					.replace(OvertureProject.ARGUMENTS_PLACEHOLDER,
							builderArguments));
			outputStream.flush();
			outputStream.close();
			outputFileReader.close();

		} catch (IOException e)
		{
			e.printStackTrace();

		}
	}
	public void writeReadmeContentFile(File outputFolder,String fileName)
	{
		FileWriter outputFileReader;

		try
		{
		outputFileReader = new FileWriter(new File(outputFolder,fileName),false);
		BufferedWriter outputStream = new BufferedWriter(outputFileReader);
		outputStream.write(content);
		outputStream.write("\n\nLanguage Version: "+ getLanguageVersion());
		if(getEntryPoints().size()>0)
			for(String entrypoint: entryPoints)
			{
				outputStream.write("\nEntry point     : "+ entrypoint);
			}
		outputStream.flush();
		outputStream.close();
		outputFileReader.close();
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
		
//		if(newReadMeFile.exists())
//			return;
		
		FileWriter outputFileReader;
		try
		{
			outputFileReader = new FileWriter(newReadMeFile,false);
			BufferedWriter outputStream = new BufferedWriter(outputFileReader);
			
			outputStream.write(toString());
			
			outputStream.flush();
			outputStream.close();
			outputFileReader.close();
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
						sb.append("\n"+line);
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
			outputFileReader = new FileWriter(file,false);
			BufferedWriter outputStream = new BufferedWriter(outputFileReader);
			
			outputStream.write(sb.toString()+"\n\n"+toString());
			
			outputStream.flush();
			outputStream.close();
			outputFileReader.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\n#******************************************************");
		sb.append("\n#  AUTOMATED TEST SETTINGS");
		sb.append("\n#------------------------------------------------------");
		
		sb.append("\n#"+LANGUAGE_VERSION+"="+languageVersion);
		sb.append("\n#"+INV_CHECKS+"="+invChecks);
		sb.append("\n#"+POST_CHECKS+"="+postChecks);
		sb.append("\n#"+PRE_CHECKS+"="+preChecks);
		sb.append("\n#"+DYNAMIC_TYPE_CHECKS+"="+dynamicTypeChecks);
		sb.append("\n#"+SUPPRESS_WARNINGS+"="+suppressWarnings);
		//sb.append("\n#"+ENTRY_POINT+"="+entryPoints);
		if(getEntryPoints().size()>0)
			for(String entrypoint: entryPoints)
			{
				sb.append("\n#"+ENTRY_POINT+"="+ entrypoint);
			}
		sb.append("\n#"+EXPECTED_RESULT+"="+expectedResult);
		
//		sb.append("\n#LANGUAGE_VERSION=vdm10");
//		sb.append("\n#INV_CHECKS=true");
//		sb.append("\n#POST_CHECKS=true");
//		sb.append("\n#PRE_CHECKS=true");
//		sb.append("\n#DYNAMIC_TYPE_CHECKS=true");
//		sb.append("\n#SUPPRESS_WARNINGS=false");
//		sb.append("\n#ENTRY_POINT=new Test1().Run()");
//		sb.append("\n#EXPECTED_RESULT=NO_ERROR_INTERPRETER");
		sb.append("\n#******************************************************");
		return sb.toString();
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
		this.entryPoints.add( entryPoint);
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
}
