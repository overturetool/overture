package org.overture.codegen.mojocg;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.mojocg.util.DelegateTrans;
import org.overture.codegen.rt2rmi.RmiGenerator;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaCodeGenMain;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.codegen.vdm2java.JavaSettings;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;
//import org.overture.codegen.

/**
 * VDM-to-Java code generator mojo
 * 
 * @goal generate
 * @phase generate-sources
 * @requiresDependencyResolution compile
 */
public class Vdm2JavaMojo extends Vdm2JavaBaseMojo
{
	public static final String VDM_PP = "pp";
	public static final String VDM_SL = "sl";
	public static final String VDM_RT = "rt";
	
	public static final String VDM_10 = "vdm10";
	public static final String VDM_CLASSIC = "classic";

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		if (outputDirectory != null && outputDirectory.exists())
		{
			getLog().info(String.format("Skipping Java code generation. Output folder '%s' already exists.", outputDirectory.getAbsolutePath()));
			return;
		}

		getLog().info("Starting the VDM-to-Java code generator...Miran");
		
		// Let's make sure that maven knows to look in the output directory
		project.addCompileSourceRoot(outputDirectory.getPath());

		IRSettings irSettings = new IRSettings();
		irSettings.setCharSeqAsString(true);
		irSettings.setGeneratePreConds(false);
		irSettings.setGeneratePreCondChecks(false);
		irSettings.setGeneratePostConds(false);
		irSettings.setGeneratePostCondChecks(false);

		JavaSettings javaSettings = new JavaSettings();
		javaSettings.setDisableCloning(false);
		javaSettings.setFormatCode(formatCode);
		javaSettings.setGenJUnit4tests(genJUnit4Tests);

		if (JavaCodeGenUtil.isValidJavaPackage(packageName))
		{
			javaSettings.setJavaRootPackage(packageName);
		} else
		{
			if(packageName != null)
			{
				getLog().warn(String.format("%s is not a valid Java package.", packageName));
			}
		}

		List<File> files = new LinkedList<File>();
		
		if (specificationDir != null && specificationDir.isDirectory())
		{
			findVdmSources(files, specificationDir);
		}

		if (files == null || files.isEmpty())
		{
			getLog().info("Nothing to generate, no specification files.");
			return;
		}

		outputDirectory.mkdirs();

		List<File> tmp = new Vector<File>();
		tmp.addAll(files);
		
		if(release.equals(VDM_10))
		{
			Settings.release = Release.VDM_10;
		}
		else if(release.equals(VDM_CLASSIC))
		{
			Settings.release = Release.CLASSIC;
		}
		else
		{
			throw new MojoFailureException(String.format("Expected VDM version to be '%s' or '%s'", VDM_10, VDM_CLASSIC)); 
		}
		
		JavaCodeGen javaCodeGen = new JavaCodeGen();
		javaCodeGen.setSettings(irSettings);
		javaCodeGen.setJavaSettings(javaSettings);
		
		addDelegateTrans(javaCodeGen);
		
		GeneratedData genData = null;

		if (dialect.equals(VDM_PP))
		{
			Settings.dialect = Dialect.VDM_PP;
			TypeCheckResult<List<SClassDefinition>> tcResult = TypeCheckerUtil.typeCheckPp(files);
			
			validateTcResult(tcResult);
			
			try
			{
				genData = javaCodeGen.generate(CodeGenBase.getNodes(tcResult.result));
			} catch (AnalysisException e)
			{
				e.printStackTrace();
				throw new MojoExecutionException("Got unexpected error when trying to code generate VDM++ model: "
						+ e.getMessage());
			}
		} else if (dialect.equals(VDM_SL))
		{
			Settings.dialect = Dialect.VDM_SL;
			TypeCheckResult<List<AModuleModules>> tcResult = TypeCheckerUtil.typeCheckSl(files);
			
			validateTcResult(tcResult);
			
			try
			{
				genData = javaCodeGen.generate(CodeGenBase.getNodes(tcResult.result));
				
			} catch (AnalysisException e)
			{
				e.printStackTrace();
				throw new MojoExecutionException("Got unexpected error when trying to code generate VDM-SL model: "
						+ e.getMessage());
			}
			
		} else if(dialect.equals(VDM_RT))
		{
			try
			{
				Settings.dialect = Dialect.VDM_RT;
				TypeCheckResult<List<SClassDefinition>> tcResult = TypeCheckerUtil.typeCheckRt(files);

				RmiGenerator rmiGen = new RmiGenerator();
				addDelegateTrans(rmiGen.getJavaGen());
				
				try {
//					System.out.println("The current path is: " + );
					
					if((new File("../distcg")).exists()){
					String current = new java.io.File( "." ).getCanonicalPath();
			        System.out.println("Current dir:"+current);
			        String currentDir = System.getProperty("user.dir");
			        System.out.println("Current dir using System:" + currentDir);
					rmiGen.generate(tcResult.result,  "../distcg/");
					}
					else System.out.println("distcg directory does not exist");
				} catch (org.overture.codegen.ir.analysis.AnalysisException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				validateTcResult(tcResult);

				javaSettings.setMakeClassesSerializable(true);
				
				genData = javaCodeGen.generate(CodeGenBase.getNodes(tcResult.result));
				
				System.out.println("Hello From RT parts.. New update ;-)");
				
				/* Start the Java RMI generator part */
				

				
				
			} catch (AnalysisException e)
			{
				e.printStackTrace();
				throw new MojoExecutionException("Got unexpected error when trying to code generate VDM-RT model: "
						+ e.getMessage());
			}
		}
		else
		{
			throw new MojoExecutionException(String.format("Expected dialect to be '%s' or '%s'", VDM_SL, VDM_PP));
		}
		
		if(genData != null)
		{
			JavaCodeGenMain.processData(false, outputDirectory, javaCodeGen, genData, separateTestCode);
			
			if(genData.hasErrors())
			{
				throw new MojoExecutionException("Could not code generate model.");
			}
		}
		
		getLog().info("Code generation completed.");
	}

	private Map<String,String> buidDelegateMap()
	{
		if(delegates == null)
		{
			return new HashMap<>();
		}
		
		Map<String, String> map = new HashMap<String, String>();
		for (String key : delegates.stringPropertyNames()) {
		    map.put(key, delegates.getProperty(key));
		}
		
		return map;
	}
	
	private void addDelegateTrans(JavaCodeGen javaCodeGen)
	{
		if(delegates != null && !delegates.isEmpty())
		{
			Map<String, String> delegateMap = buidDelegateMap();
			
			getLog().info("Found following bridge/delegate pairs:");
			
			for(String entry : delegateMap.keySet())
			{
				getLog().info("  Bridge class: " + entry + ". Delegate class: " + delegateMap.get(entry));
			}
			
			javaCodeGen.getTransSeries().getSeries().add(new DelegateTrans(delegateMap, javaCodeGen.getTransAssistant(), getLog()));
		}
	}

	private void findVdmSources(List<File> files, File specificationRoot)
	{
		for(File f : GeneralUtils.getFilesRecursively(specificationRoot))
		{
			if(GeneralCodeGenUtils.isVdmSourceFile(f))
			{
				files.add(f);
			}
		}
	}

	private void validateTcResult(
			TypeCheckResult<?> tcResult) throws MojoExecutionException
	{
		if(!tcResult.parserResult.errors.isEmpty() || !tcResult.errors.isEmpty())
		{
			String PARSE_TYPE_CHECK_ERR_MSG = "Could not parse or type check VDM model";
			getLog().error(PARSE_TYPE_CHECK_ERR_MSG + ":\n" + GeneralCodeGenUtils.errorStr(tcResult));
			
			throw new MojoExecutionException(PARSE_TYPE_CHECK_ERR_MSG);
		}
		
		// No type errors
	}
}
