package org.overture.codegen.vdm2python;

import org.apache.log4j.Logger;
import org.overture.ast.analysis.AnalysisException;
import org.overture.codegen.ir.*;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class PythonGen extends CodeGenBase
{
	private Logger log = Logger.getLogger(this.getClass().getName());

	private PythonFormat pythonFormat;

	private String PYTHON_TEMPLATES_ROOT_FOLDER = "PythonTemplates";

	public PythonGen()
	{
		this.pythonFormat = new PythonFormat(PYTHON_TEMPLATES_ROOT_FOLDER);
	}

	@Override protected GeneratedData genVdmToTargetLang(
			List<IRStatus<PIR>> statuses) throws AnalysisException
	{

		List<GeneratedModule> genModules = new LinkedList<>();

		AdditionOptimiserTrans addTrans = new AdditionOptimiserTrans(transAssistant);

		for (IRStatus<PIR> status : statuses)
		{
			try
			{
				generator.applyPartialTransformation(status, addTrans);

			} catch (org.overture.codegen.ir.analysis.AnalysisException e)
			{
				log.error(
						"Error when applying partial transformation for module "
								+ status.getIrNodeName() + ": "
								+ e.getMessage());
				log.error("Skipping module..");
				e.printStackTrace();
			}
		}

		for (IRStatus<PIR> status : statuses)
		{
			try
			{
				genModules.add(generate(status));
			} catch (org.overture.codegen.ir.analysis.AnalysisException e)
			{

				log.error("Error generating code for class "
						+ status.getIrNodeName() + ": " + e.getMessage());
				log.error("Skipping class..");
				e.printStackTrace();
			}
		}

		GeneratedData data = new GeneratedData();
		data.setClasses(genModules);

		return data;
	}

	private GeneratedModule generate(IRStatus<PIR> status)
			throws org.overture.codegen.ir.analysis.AnalysisException
	{
		MergeVisitor codeEmitter = pythonFormat.getCodeEmitter();

		if (status.canBeGenerated())
		{
			codeEmitter.init();
			StringWriter writer = new StringWriter();
			status.getIrNode().apply(codeEmitter, writer);

			boolean isTestCase = isTestCase(status);

			if (codeEmitter.hasMergeErrors())
			{
				return new GeneratedModule(status.getIrNodeName(), status.getIrNode(), codeEmitter.getMergeErrors(), isTestCase);
			} else if (codeEmitter.hasUnsupportedTargLangNodes())
			{
				return new GeneratedModule(status.getIrNodeName(), new HashSet<VdmNodeInfo>(), codeEmitter.getUnsupportedInTargLang(), isTestCase);
			} else
			{
				// TODO: override formatCode and use dedicated code formatter
				GeneratedModule generatedModule = new GeneratedModule(status.getIrNodeName(), status.getIrNode(), formatCode(writer), isTestCase);
				generatedModule.setTransformationWarnings(status.getTransformationWarnings());
				return generatedModule;
			}
		} else
		{
			return new GeneratedModule(status.getIrNodeName(), status.getUnsupportedInIr(), new HashSet<IrNodeInfo>(), isTestCase(status));
		}
	}
}