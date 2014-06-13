package org.overture.pog.tests.old;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.parser.messages.VDMError;
import org.overture.parser.messages.VDMWarning;
import org.overture.pog.contexts.POContextStack;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.pub.IProofObligation;
import org.overture.pog.visitors.PogVisitor;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public class TestPogUtil
{
	public static class PogResult<T>
	{
		public final TypeCheckResult<T> typeCheckResult;
		public final List<VDMWarning> warnings;
		public final List<VDMError> errors;
		public final List<String> result;

		public PogResult(TypeCheckResult<T> typeCheckResult,
				List<String> result, List<VDMWarning> warnings,
				List<VDMError> errors)
		{
			this.typeCheckResult = typeCheckResult;
			this.result = result;
			this.warnings = warnings;
			this.errors = errors;
		}
	}

	public static PogResult<List<AModuleModules>> pogSl(File file)
			throws Exception
	{
		TypeCheckResult<List<AModuleModules>> result = TypeCheckerUtil.typeCheckSl(file);
		return pog(result);
	}

	public static PogResult<List<SClassDefinition>> pogPp(File file)
			throws Exception
	{
		TypeCheckResult<List<SClassDefinition>> result = TypeCheckerUtil.typeCheckPp(file);
		return pog(result);
	}

	public static PogResult<List<SClassDefinition>> pogRt(File file)
			throws Exception
	{
		TypeCheckResult<List<SClassDefinition>> result = TypeCheckerUtil.typeCheckRt(file);
		return pog(result);
	}

	public static <P extends List<? extends INode>> PogResult<P> pog(
			TypeCheckResult<P> typeCheckResult) throws Exception
	{
		if (typeCheckResult.errors.isEmpty())
		{
			ProofObligationList proofObligations = new ProofObligationList();
			List<String> stringPOs = new LinkedList<String>();
			for (INode aModule : typeCheckResult.result)
			{
				try
				{
					proofObligations.addAll(aModule.apply(new PogVisitor(), new POContextStack()));
					for (IProofObligation ipo : proofObligations){
						stringPOs.add(PogTestHelper.makePoString(ipo));
					}
				} catch (AnalysisException e)
				{
					e.printStackTrace();
					throw new Exception("Internal error", e);
				}
			}
			return new PogResult<P>(typeCheckResult, stringPOs, new Vector<VDMWarning>(), new Vector<VDMError>());
		}
		throw new Exception("Failed to type check: " + typeCheckResult);
		// return new PogResult<P>(typeCheckResult, null, new Vector<VDMWarning>(), new Vector<VDMError>());
	}

}
