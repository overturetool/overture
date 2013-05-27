package org.overture.pog;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.parser.messages.VDMError;
import org.overture.parser.messages.VDMWarning;
import org.overture.pog.POContextStack;
import org.overture.pog.PogVisitor;
import org.overture.pog.ProofObligationList;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public class PogUtil
{
	public static class PogResult<T>
	{
		public final TypeCheckResult<T> typeCheckResult;
		public final List<VDMWarning> warnings;
		public final List<VDMError> errors;
		public final ProofObligationList result;

		public PogResult(TypeCheckResult<T> typeCheckResult, ProofObligationList result,
				List<VDMWarning> warnings, List<VDMError> errors)
		{
			this.typeCheckResult = typeCheckResult;
			this.result = result;
			this.warnings = warnings;
			this.errors = errors;
		}
	}
	
	
	public static PogResult<List<AModuleModules>> pogSl(File file) throws Exception
	{
		TypeCheckResult<List<AModuleModules>> result = TypeCheckerUtil.typeCheckSl(file);
		return pog(result);
	}
	
	public static PogResult<List<SClassDefinition>> pogPp(File file) throws Exception
	{
		TypeCheckResult<List<SClassDefinition>> result = TypeCheckerUtil.typeCheckPp(file);
		return pog(result);
	} 
	
	public static PogResult<List<SClassDefinition>> pogRt(File file) throws Exception
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
			for (INode aModule : typeCheckResult.result) {
				try
				{
					proofObligations.addAll(aModule.apply(new PogVisitor(), new POContextStack()));
				} catch (AnalysisException e)
				{
					throw new Exception("Internal error",e);
				}
			}
			return new PogResult<P>(typeCheckResult, proofObligations, new Vector<VDMWarning>(), new Vector<VDMError>());
		}
		throw new Exception("Failed to type check");
		//return new PogResult<P>(typeCheckResult, null, new Vector<VDMWarning>(), new Vector<VDMError>());
	}
	
	
	
}
