package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisAdaptor;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.type.ABracketTypeAssistantTC;
import org.overture.typechecker.assistant.type.AClassTypeAssistantTC;
import org.overture.typechecker.assistant.type.AFunctionTypeAssistantTC;
import org.overture.typechecker.assistant.type.ANamedInvariantTypeAssistantTC;
import org.overture.typechecker.assistant.type.AOperationTypeAssistantTC;
import org.overture.typechecker.assistant.type.AOptionalTypeAssistantTC;
import org.overture.typechecker.assistant.type.AProductTypeAssistantTC;
import org.overture.typechecker.assistant.type.ARecordInvariantTypeAssistantTC;
import org.overture.typechecker.assistant.type.ASetTypeAssistantTC;
import org.overture.typechecker.assistant.type.AUnionTypeAssistantTC;
import org.overture.typechecker.assistant.type.SMapTypeAssistantTC;
import org.overture.typechecker.assistant.type.SSeqTypeAssistantTC;

/**
 * This class set a type to unresolved.
 * 
 * @author kel
 */
public class TypeUnresolver extends AnalysisAdaptor
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ITypeCheckerAssistantFactory af;

	public TypeUnresolver(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	@Override
	public void caseABracketType(ABracketType type) throws AnalysisException
	{
		ABracketTypeAssistantTC.unResolve(type);
	}

	@Override
	public void caseAClassType(AClassType type) throws AnalysisException
	{
		AClassTypeAssistantTC.unResolve(type);
	}

	@Override
	public void caseAFunctionType(AFunctionType type) throws AnalysisException
	{
		AFunctionTypeAssistantTC.unResolve(type);
	}
	@Override
	public void caseANamedInvariantType(ANamedInvariantType type)
			throws AnalysisException
	{
		ANamedInvariantTypeAssistantTC.unResolve(type);
	}
	
	@Override
	public void caseARecordInvariantType(ARecordInvariantType type)
			throws AnalysisException
	{
		ARecordInvariantTypeAssistantTC.unResolve(type);
	}
	@Override
	public void defaultSInvariantType(SInvariantType type)
			throws AnalysisException
	{
		type.setResolved(false);
	}
	
	@Override
	public void defaultSMapType(SMapType type) throws AnalysisException
	{
		SMapTypeAssistantTC.unResolve(type);
	}
	
	@Override
	public void caseAOperationType(AOperationType type)
			throws AnalysisException
	{
		AOperationTypeAssistantTC.unResolve(type);
	}
	@Override
	public void caseAOptionalType(AOptionalType type) throws AnalysisException
	{
		AOptionalTypeAssistantTC.unResolve(type);
	}
	@Override
	public void caseAProductType(AProductType type) throws AnalysisException
	{
		AProductTypeAssistantTC.unResolve(type);
	}
	@Override
	public void defaultSSeqType(SSeqType type) throws AnalysisException
	{
		SSeqTypeAssistantTC.unResolve(type);
	}
	@Override
	public void caseASetType(ASetType type) throws AnalysisException
	{
		ASetTypeAssistantTC.unResolve(type);
	}
	
	@Override
	public void caseAUnionType(AUnionType type) throws AnalysisException
	{
		AUnionTypeAssistantTC.unResolve(type);
	}
	@Override
	public void defaultPType(PType type) throws AnalysisException
	{
		type.setResolved(false);
	}

}
