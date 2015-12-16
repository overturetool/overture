package org.overture.vdm2jml.tests;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.tests.output.util.SlSpecificationTest;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.vdm2jml.JmlGenerator;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.core.tests.PathsProvider;

@RunWith(Parameterized.class)
public class JmlSlTraceOutputTest extends SlSpecificationTest
{
	public static final String JML_SL_TRACE_UPDATE_PROPERTY = "tests.vdm2jml.override.";
	public static final String ROOT = "src" + File.separatorChar + "test" + File.separatorChar + "resources"
			+ File.separatorChar + "traces_specs";

	public JmlSlTraceOutputTest(String nameParameter, String inputParameter, String resultParameter)
	{
		super(nameParameter, inputParameter, resultParameter);
	}
	
	@Override
	public GeneratedData genCode(List<INode> ast) throws AnalysisException
	{
		List<AModuleModules> modules = buildModulesList(ast);
		
		JmlGenerator jmlGen = new JmlGenerator(getJavaGen());
		
		return jmlGen.generateJml(modules);
	}
	
	@Before
	public void init()
	{
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
	}
	
	@Override
	public IRSettings getIrSettings()
	{
		IRSettings irSettings = super.getIrSettings();
		irSettings.setGenerateTraces(true);
		irSettings.setGeneratePostConds(true);
		irSettings.setGeneratePreConds(true);
		
		return irSettings;
	}

	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData()
	{
		return PathsProvider.computePaths(ROOT);
	}
	
	@Override
	protected boolean updateCheck()
	{
		if (frameworkUpdateCheck())
		{
			return true;
		}
		
		if (System.getProperty(JML_SL_TRACE_UPDATE_PROPERTY + "all") != null)
		{
			return true;
		}

		return false;
	}
	
	@Override
	protected String getUpdatePropertyString()
	{
		return JML_SL_TRACE_UPDATE_PROPERTY + "traces";
	}
}
