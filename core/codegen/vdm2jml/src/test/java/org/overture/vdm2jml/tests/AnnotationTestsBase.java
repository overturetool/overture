package org.overture.vdm2jml.tests;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.util.ClonableString;
import org.overture.codegen.ir.PIR;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.vdm2java.JavaFormat;
import org.overture.codegen.vdm2java.JavaSettings;
import org.overture.codegen.vdm2jml.JmlGenerator;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

abstract public class AnnotationTestsBase
{
	private static final String MODULE_STATE_NAME = "St";
	private static final String MODULE_NAME = "M";

	public static final String TEST_RESOURCES_ROOT = "src" + File.separatorChar
			+ "test" + File.separatorChar + "resources" + File.separatorChar;

	public static final String TEST_RES_STATIC_ANALYSIS_ROOT = TEST_RESOURCES_ROOT
			+ "static_analysis" + File.separatorChar;

	public static final String SPEC_PUBLIC_ANNOTATION = "/*@ spec_public @*/";
	public static final String PURE_ANNOTATION = "/*@ pure @*/";
	public static final String HELPER_ANNOTATION = "/*@ helper @*/";
	
	private static final boolean VERBOSE = false;
	
	// The IR class that the input module generates to
	protected static ADefaultClassDeclIR genModule;
	
	// The IR class that is used to represent the type of the module state
	protected static ADefaultClassDeclIR genStateType;
	
	@BeforeClass
	public static void prepareVdmTypeChecker()
	{
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
	}
	
	public static void init(String fileName) throws AnalysisException
	{
		List<ADefaultClassDeclIR> classes = getClasses(fileName);

		for (ADefaultClassDeclIR clazz : classes)
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
		Assert.assertEquals("Expected generated module to be in different package", JmlGenerator.DEFAULT_JAVA_ROOT_PACKAGE, genModule.getPackage());
	}
	
	public static void validateGenModuleAndStateType()
	{
		validGeneratedModule();
		
		Assert.assertTrue("State type was not generated", genStateType != null);
		String stateClassPackage = JmlGenerator.DEFAULT_JAVA_ROOT_PACKAGE + "."
				+ genModule.getName() + JavaFormat.TYPE_DECL_PACKAGE_SUFFIX;
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

	public static List<ADefaultClassDeclIR> getClasses(GeneratedData data)
	{
		List<ADefaultClassDeclIR> classes = new LinkedList<ADefaultClassDeclIR>();

		for (GeneratedModule node : data.getClasses())
		{
			if(VERBOSE)
			{
				Logger.getLog().println(node.getContent());
				Logger.getLog().println("*******************");
			}
			
			if (node.getIrNode() instanceof ADefaultClassDeclIR)
			{
				classes.add((ADefaultClassDeclIR) node.getIrNode());
			}
		}

		return classes;
	}

	public static List<AMethodDeclIR> getGenFunctions(List<AMethodDeclIR> methods)
	{
		List<AMethodDeclIR> genFuncs = new LinkedList<AMethodDeclIR>();

		for (AMethodDeclIR m : methods)
		{
			if (m.getSourceNode() != null
					&& m.getSourceNode().getVdmNode() instanceof SFunctionDefinition)
			{
				genFuncs.add(m);
			}
		}

		return genFuncs;
	}
	
	public static AMethodDeclIR getMethod(List<AMethodDeclIR> methods, String name)
	{
		for(AMethodDeclIR m : methods)
		{
			if(m.getName().equals(name))
			{
				return m;
			}
		}
		
		return null;
	}

	public static List<AMethodDeclIR> getGenMethods(List<AMethodDeclIR> methods)
	{
		List<AMethodDeclIR> genOps = new LinkedList<AMethodDeclIR>();

		for (AMethodDeclIR m : methods)
		{
			if (m.getSourceNode() != null
					&& m.getSourceNode().getVdmNode() instanceof SOperationDefinition)
			{
				genOps.add(m);
			}
		}

		return genOps;
	}
	
	public static List<ADefaultClassDeclIR> getClasses(String fileName)
			throws AnalysisException
	{
		List<File> files = new LinkedList<>();
		files.add(new File(TEST_RES_STATIC_ANALYSIS_ROOT + fileName));

		
		TypeCheckResult<List<AModuleModules>> tcResult = TypeCheckerUtil.typeCheckSl(files);

		if(GeneralCodeGenUtils.hasErrors(tcResult))
		{
			Assert.fail("Could not parse/type check VDM model:\n" + GeneralCodeGenUtils.errorStr(tcResult));
		}

		JmlGenerator jmlGen = new JmlGenerator();
		initJmlGen(jmlGen);

		GeneratedData data = jmlGen.generateJml(tcResult.result);

		return getClasses(data);
	}

	public static String getLastAnnotation(PIR node)
	{
		if (node.getMetaData() != null)
		{
			return getAnnotation(node, node.getMetaData().size() - 1);
		} else
		{
			return null;
		}
	}
	
	public static String getAnnotation(PIR node, int idx)
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
		AMethodDeclIR preCondFunc = getMethod(genModule.getMethods(), funcName);

		Assert.assertTrue("Expected only a @pure annotaton for the pre condition function",
				preCondFunc.getMetaData().size() == 1);

		Assert.assertEquals("Expected pre condition function to be pure",
				PURE_ANNOTATION,
				getLastAnnotation(preCondFunc));
	}

	public static void assertHelper(PIR node, String msg)
	{
		for(ClonableString m : node.getMetaData())
		{
			if(m.value.equals(HELPER_ANNOTATION))
			{
				return;
			}
		}
		
		Assert.assertTrue(msg, false);
	}
	
	public static void assertPure(List<AMethodDeclIR> methods)
	{
		Assert.assertTrue("Expected functions to be defined", methods != null && !methods.isEmpty());

		for (AMethodDeclIR func : methods)
		{
			if (!func.getIsConstructor())
			{
				assertPureMethod(func);
			}
		}
	}

	public static void assertPureMethod(AMethodDeclIR method)
	{
		String failureMsg = "Expected method " + method.getName()
				+ " to be pure";

		List<? extends ClonableString> metaData = method.getMetaData();

		Assert.assertTrue(failureMsg, metaData != null
				&& !metaData.isEmpty());
		
		for(ClonableString m : method.getMetaData())
		{
			if(m.value.equals(PURE_ANNOTATION))
			{
				return;
			}
		}
		
		Assert.assertTrue(failureMsg, false);
	}
	
	public static void assertNotPureMethod(AMethodDeclIR method)
	{
		String failureMsg = "Expected method " + method.getName()
				+ " not to be pure";

		for(ClonableString m : method.getMetaData())
		{
			if(m.value.equals(PURE_ANNOTATION))
			{
				Assert.fail(failureMsg);
			}
		}
	}
	
	public static void assertRecMethodsPurity(List<AMethodDeclIR> stateMethods)
	{
		for (AMethodDeclIR m : stateMethods)
		{
			if (m.getName().equals("hashCode") || m.getName().equals("equals")
					|| m.getName().equals("toString")
					|| m.getName().equals("copy")
					|| m.getName().startsWith("get_")
					|| m.getName().equals("valid"))
			{
				assertPureMethod(m);
			} else
			{
				assertNotPureMethod(m);
			}
		}
	}
}
