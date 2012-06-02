package org.overture.pog.util;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.pog.obligations.POContextStack;
import org.overture.pog.obligations.ProofObligationList;
import org.overture.pog.visitors.PogVisitor;
import org.overture.typecheck.util.TypeCheckerUtil;
import org.overture.typecheck.util.TypeCheckerUtil.TypeCheckResult;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;
import org.overturetool.vdmj.syntax.ParserException;

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
	
	
	public static PogResult<List<AModuleModules>> pogSl(File file)
	{
		TypeCheckResult<List<AModuleModules>> result = TypeCheckerUtil.typeCheckSl(file);
		return pog(result);
	}
	
	public static PogResult<List<SClassDefinition>> pogPp(File file)
	{
		TypeCheckResult<List<SClassDefinition>> result = TypeCheckerUtil.typeCheckPp(file);
		return pog(result);
	}
	
	public static PogResult<List<SClassDefinition>> pogRt(File file) throws ParserException, LexException
	{
		TypeCheckResult<List<SClassDefinition>> result = TypeCheckerUtil.typeCheckRt(file);
		return pog(result);
	}
	
	public static <P extends List<? extends INode>> PogResult<P> pog(
			TypeCheckResult<P> typeCheckResult)
	{
		if (typeCheckResult.errors.isEmpty())
		{
			ProofObligationList proofObligations = new ProofObligationList();
			for (INode aModule : typeCheckResult.result) {
				proofObligations.addAll(aModule.apply(new PogVisitor(), new POContextStack()));
			}
			return new PogResult<P>(typeCheckResult, proofObligations, new Vector<VDMWarning>(), new Vector<VDMError>());
		}
		return new PogResult<P>(typeCheckResult, null, new Vector<VDMWarning>(), new Vector<VDMError>());
	}
	
	
	
}
