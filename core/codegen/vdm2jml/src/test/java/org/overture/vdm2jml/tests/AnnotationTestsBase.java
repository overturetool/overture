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
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.vdm2java.JavaSettings;
import org.overture.codegen.vdm2jml.JmlGenerator;

abstract public class AnnotationTestsBase
{
	public static final String TEST_RESOURCES_ROOT = "src" + File.separatorChar
			+ "test" + File.separatorChar + "resources" + File.separatorChar;

	public static String SPEC_PUBLIC_ANNOTATION = "/*@ spec_public @*/";
	public static String PURE_ANNOTATION = "/*@ pure @*/";
	
	private static final boolean VERBOSE = false;
	
	// The IR class that the input module generates to
	protected static AClassDeclCG genModule;
	
	public static void init(String fileName) throws AnalysisException, UnsupportedModelingException
	{
		List<AClassDeclCG> classes = getClasses(fileName);

		if (classes.size() == 1)
		{
			genModule = classes.get(0);
		}
	}
	
	protected static void validGeneratedModule()
	{
		Assert.assertTrue("No module was generated", genModule != null);
		Assert.assertEquals("Expected generated module to be in the default package", null, genModule.getPackage());
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
		files.add(new File(AnnotationTestsBase.TEST_RESOURCES_ROOT + fileName));

		ModuleList modules = GeneralCodeGenUtils.consModuleList(files);

		JmlGenerator jmlGen = new JmlGenerator();
		AnnotationTestsBase.initJmlGen(jmlGen);

		GeneratedData data = jmlGen.generateJml(modules);

		return AnnotationTestsBase.getClasses(data);
	}

	public static String getLastAnnotation(List<? extends ClonableString> metaData)
	{
		if (metaData != null && !metaData.isEmpty())
		{
			return metaData.get(metaData.size() - 1).value;
		}

		return null;
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
			Assert.assertEquals(failureMsg, PURE_ANNOTATION, getLastAnnotation(metaData));
		}
	}
}
