package org.overture.vdm2jml.tests;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.util.ClonableString;
import org.overture.ast.util.modules.ModuleList;
import org.overture.codegen.analysis.violations.UnsupportedModelingException;
import org.overture.codegen.cgast.PCG;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.vdm2java.JavaFormat;
import org.overture.codegen.vdm2java.JavaSettings;
import org.overture.codegen.vdm2jml.JmlGenerator;

abstract public class AnnotationTestsBase
{
	private static final String MODULE_STATE_NAME = "St";
	private static final String MODULE_NAME = "M";

	public static final String TEST_RESOURCES_ROOT = "src" + File.separatorChar
			+ "test" + File.separatorChar + "resources" + File.separatorChar
			+ "static_analysis" + File.separatorChar;

	public static final String SPEC_PUBLIC_ANNOTATION = "/*@ spec_public @*/";
	public static final String PURE_ANNOTATION = "/*@ pure @*/";
	public static final String HELPER_ANNOTATION = "/*@ helper @*/";
	
	private static final boolean VERBOSE = false;
	
	// The IR class that the input module generates to
	protected static AClassDeclCG genModule;
	
	// The IR class that is used to represent the type of the module state
	protected static AClassDeclCG genStateType;
	
	public static void init(String fileName) throws AnalysisException, UnsupportedModelingException
	{
		List<AClassDeclCG> classes = getClasses(fileName);

		for (AClassDeclCG clazz : classes)
		{
			if (clazz.getName().equals(MODULE_NAME))
			{
				genModule = clazz;
			} else if (clazz.getName().equals(MODULE_STATE_NAME))
			{
				genStateType = clazz;
			}
		}
	}
	
	protected static void validGeneratedModule()
	{
		Assert.assertTrue("No module was generated", genModule != null);
		Assert.assertEquals("Expected generated module to be in the default package", null, genModule.getPackage());
	}
	
	public static void validateGenModuleAndStateType()
	{
		validGeneratedModule();
		
		Assert.assertTrue("State type was not generated", genStateType != null);
		String stateClassPackage = genModule.getName()
				+ JavaFormat.TYPE_DECL_PACKAGE_SUFFIX;
		Assert.assertEquals("Generated state type is located in a wrong package", stateClassPackage, genStateType.getPackage());
	}
	
	public static void initJmlGen(JmlGenerator jmlGen)
	{
		IRSettings irSettings = jmlGen.getIrSettings();
		irSettings.setCharSeqAsString(true);
		irSettings.setGeneratePreConds(true);
		irSettings.setGeneratePreCondChecks(false);
		irSettings.setGeneratePostConds(true);
		irSettings.setGeneratePostCondChecks(false);

		JavaSettings javaSettings = jmlGen.getJavaSettings();
		javaSettings.setDisableCloning(false);
		javaSettings.setJavaRootPackage(null); // Default package
		javaSettings.setGenRecsAsInnerClasses(false);
	}

	public static List<AClassDeclCG> getClasses(GeneratedData data)
	{
		List<AClassDeclCG> classes = new LinkedList<AClassDeclCG>();

		for (GeneratedModule node : data.getClasses())
		{
			if(VERBOSE)
			{
				Logger.getLog().println(node.getContent());
				Logger.getLog().println("*******************");
			}
			
			if (node.getIrNode() instanceof AClassDeclCG)
			{
				classes.add((AClassDeclCG) node.getIrNode());
			}
		}

		return classes;
	}

	public static List<AMethodDeclCG> getGenFunctions(List<AMethodDeclCG> methods)
	{
		List<AMethodDeclCG> genFuncs = new LinkedList<AMethodDeclCG>();

		for (AMethodDeclCG m : methods)
		{
			if (m.getSourceNode() != null
					&& m.getSourceNode().getVdmNode() instanceof SFunctionDefinition)
			{
				genFuncs.add(m);
			}
		}

		return genFuncs;
	}
	
	public static AMethodDeclCG getMethod(List<AMethodDeclCG> methods, String name)
	{
		for(AMethodDeclCG m : methods)
		{
			if(m.getName().equals(name))
			{
				return m;
			}
		}
		
		return null;
	}

	public static List<AMethodDeclCG> getGenMethods(List<AMethodDeclCG> methods)
	{
		List<AMethodDeclCG> genOps = new LinkedList<AMethodDeclCG>();

		for (AMethodDeclCG m : methods)
		{
			if (m.getSourceNode() != null
					&& m.getSourceNode().getVdmNode() instanceof SOperationDefinition)
			{
				genOps.add(m);
			}
		}

		return genOps;
	}
	
	public static List<AClassDeclCG> getClasses(String fileName)
			throws AnalysisException, UnsupportedModelingException
	{
		List<File> files = new LinkedList<File>();
		files.add(new File(TEST_RESOURCES_ROOT + fileName));

		ModuleList modules = GeneralCodeGenUtils.consModuleList(files);

		JmlGenerator jmlGen = new JmlGenerator();
		initJmlGen(jmlGen);

		GeneratedData data = jmlGen.generateJml(modules);

		return getClasses(data);
	}

	public static String getLastAnnotation(PCG node)
	{
		if (node.getMetaData() != null)
		{
			return getAnnotation(node, node.getMetaData().size() - 1);
		} else
		{
			return null;
		}
	}
	
	public static String getAnnotation(PCG node, int idx)
	{
		List<? extends ClonableString> metaData = node.getMetaData();
		
		if (metaData != null && idx >= 0 && idx < metaData.size())
		{
			return metaData.get(idx).value;
		}

		return null;
	}
	
	public void assertFuncIsPureOnly(String funcName)
	{
		AMethodDeclCG preCondFunc = getMethod(genModule.getMethods(), funcName);

		Assert.assertTrue("Expected only a @pure annotaton for the pre condition function",
				preCondFunc.getMetaData().size() == 1);

		Assert.assertEquals("Expected pre condition function to be pure",
				PURE_ANNOTATION,
				getLastAnnotation(preCondFunc));
	}

	public static void assertGenFuncsPure(List<AMethodDeclCG> genFuncs)
	{
		Assert.assertTrue("Expected functions to be defined", genFuncs != null && !genFuncs.isEmpty());

		for (AMethodDeclCG func : genFuncs)
		{
			// Since @pure is a JML modifier so this annotation should go last
			String failureMsg = "Expected the last annotation to be @pure of function "
					+ func.getName();

			List<? extends ClonableString> metaData = func.getMetaData();

			Assert.assertTrue(failureMsg, metaData != null
					&& !metaData.isEmpty());
			Assert.assertEquals(failureMsg, PURE_ANNOTATION, getLastAnnotation(func));
		}
	}
}
