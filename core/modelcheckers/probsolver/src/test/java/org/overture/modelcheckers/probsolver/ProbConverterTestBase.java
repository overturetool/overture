package org.overture.modelcheckers.probsolver;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.modelcheckers.probsolver.ProbSolverUtil.SolverException;
import org.overture.modelcheckers.probsolver.visitors.VdmToBConverter;
import org.overture.test.framework.ConditionalIgnoreMethodRule;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

import de.be4.classicalb.core.parser.exceptions.BException;

public class ProbConverterTestBase
{
	private File file;

	public ProbConverterTestBase(File file)
	{
		this.file = file;
		Assert.assertTrue("Input file does not exist", file.exists());
	}

	@Before
	public void setup() throws BException
	{
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
		VdmToBConverter.USE_INITIAL_FIXED_STATE = true;
	}

	@Rule
	public ConditionalIgnoreMethodRule rule = new ConditionalIgnoreMethodRule();

	protected void testMethod(String name) throws IOException,
			AnalysisException, SolverException
	{
		try
		{
			INode result = null;
			PDefinition def = findFunctionOrOperation(name);

			if (def instanceof AImplicitOperationDefinition)
			{
				HashMap<String, String> emptyMap = new HashMap<String, String>();
				result = ProbSolverUtil.solve(def.getName().getName(), (AImplicitOperationDefinition) def, emptyMap, emptyMap, getArgTypes(def), new SolverConsole());

			} else
			{
				AImplicitFunctionDefinition funDef = (AImplicitFunctionDefinition) def;
				HashMap<String, String> emptyMap = new HashMap<String, String>();
				result = ProbSolverUtil.solve(def.getName().getName(), funDef.getPostcondition(), funDef.getResult(), emptyMap, emptyMap, getArgTypes(def), new SolverConsole());
			}
			System.out.println("Result=" + result);

		} catch (SolverException e)
		{
			if (e.getCause() instanceof UnsupportedTranslationException)
			{
				Assert.fail(e.getCause().getMessage());
				// } else if(e.getCause() instanceof ProvisionException && e.getCause().getCause() instanceof
				// NullPointerException)
				// {
				// Assume.assumeFalse("ProB not installed", false);
			} else
			{
				throw e;
			}
		}
	}

	public static Map<String, PType> getArgTypes(PDefinition def)
	{
		Map<String, PType> argTypes = new HashMap<String, PType>();

		if (def instanceof AImplicitOperationDefinition)
		{
			final AImplicitOperationDefinition op = (AImplicitOperationDefinition) def;
			for (APatternListTypePair pl : op.getParameterPatterns())
			{
				for (PPattern p : pl.getPatterns())
				{
					argTypes.put(p.toString(), pl.getType());
				}
			}
			if (op.getResult() != null)
			{
				argTypes.put(op.getResult().getPattern().toString(), op.getResult().getType());
			}
		} else if (def instanceof AImplicitFunctionDefinition)
		{
			AImplicitFunctionDefinition fun = (AImplicitFunctionDefinition) def;
			for (APatternListTypePair pl : fun.getParamPatterns())
			{
				for (PPattern p : pl.getPatterns())
				{
					argTypes.put(p.toString(), pl.getType());
				}
			}

			if (fun.getResult() != null)
			{
				argTypes.put(fun.getResult().getPattern().toString(), fun.getResult().getType());
			}
		}

		return argTypes;
	}

	protected PDefinition findFunctionOrOperation(String name)
			throws AnalysisException
	{

		List<PDefinition> defs = null;

		if (Settings.dialect == Dialect.VDM_SL)
		{
			List<AModuleModules> modules = parseSL(file);
			defs = modules.get(0).getDefs();
		} else
		{
			defs = parsePP(file).get(0).getDefinitions();
		}

		PDefinition opDef = null;

		for (PDefinition d : defs)
		{
			if ((d instanceof AImplicitOperationDefinition || d instanceof AImplicitFunctionDefinition)
					&& d.getName().getName().equals(name))
			{
				opDef = d;
				break;
			}
		}
		return opDef;
	}

	private List<SClassDefinition> parsePP(File file2) throws AnalysisException
	{
		if (file == null || !file.exists())
		{
			throw new AnalysisException("No expression to generate from");
		}

		TypeCheckResult<List<SClassDefinition>> typeCheckResult = null;
		try
		{
			typeCheckResult = TypeCheckerUtil.typeCheckPp(file);
		} catch (Exception e)
		{
			throw new AnalysisException("Unable to type check expression: "
					+ file + ". Message: " + e.getMessage());
		}

		if (!typeCheckResult.errors.isEmpty()
				|| !typeCheckResult.parserResult.errors.isEmpty())
		{
			throw new AnalysisException("Unable to type check expression: "
					+ file);
		}

		return typeCheckResult.result;
	}

	private List<AModuleModules> parseSL(File file) throws AnalysisException
	{
		if (file == null || !file.exists())
		{
			throw new AnalysisException("No expression to generate from");
		}

		TypeCheckResult<List<AModuleModules>> typeCheckResult = null;
		try
		{
			typeCheckResult = TypeCheckerUtil.typeCheckSl(file);
		} catch (Exception e)
		{
			throw new AnalysisException("Unable to type check expression: "
					+ file + ". Message: " + e.getMessage());
		}

		if (!typeCheckResult.errors.isEmpty()
				|| !typeCheckResult.parserResult.errors.isEmpty())
		{
			throw new AnalysisException("Unable to type check expression: "
					+ file);
		}

		return typeCheckResult.result;
	}
}
