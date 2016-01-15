package org.overture.vdm2jml.tests.exec;

import java.io.File;

import org.junit.Assume;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.tests.exec.util.ExecutionResult;
import org.overture.codegen.tests.exec.util.testhandlers.TraceHandler;
import org.overture.codegen.vdm2java.JavaToolsUtils;
import org.overture.codegen.vdm2jml.JmlGenerator;
import org.overture.config.Release;

public class JmlTraceTestHandler extends TraceHandler
{
	public JmlTraceTestHandler(Release release, Dialect dialect)
	{
		super(release, dialect);
	}
	
	@Override
	public String getMainClassAnnotation()
	{
		return JmlGenerator.JML_NULLABLE_BY_DEFAULT;
	}
	
	@Override
	public ExecutionResult runJava(File folder)
	{
		String openJmlDir = System.getenv(JmlExecTestBase.OPENJML_ENV_VAR);
		Assume.assumeTrue("Could not find OpenJML installation directory", openJmlDir != null);

		File cgRuntime = new File(JmlExecTestBase.CODEGEN_RUNTIME);
		
		File openJml = new File(openJmlDir, JmlExecTestBase.OPEN_JML);
		JmlExecTestBase.assumeFile(JmlExecTestBase.OPEN_JML, openJml);
		
		File vdm2jmlRuntime = new File(JmlExecTestBase.VDM_TO_JML_RUNTIME);
		JmlExecTestBase.assumeFile(JmlExecTestBase.VDM_TO_JML_RUNTIME, vdm2jmlRuntime);
		
		File jmlRuntime = new File(openJmlDir, JmlExecTestBase.JML_RUNTIME);
		JmlExecTestBase.assumeFile(JmlExecTestBase.JML_RUNTIME, jmlRuntime);
		
		ExecutionResult produceExecResult = produceExecResult(folder, new String[] { JavaToolsUtils.ENABLE_ASSERTIONS_ARG }, new File[] { jmlRuntime,
				openJml, cgRuntime, vdm2jmlRuntime });
		
		return processTraceResult(produceExecResult);
	}
}
