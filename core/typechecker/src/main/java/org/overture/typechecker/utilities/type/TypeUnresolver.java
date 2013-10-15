package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisAdaptor;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFieldField;
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
import org.overture.typechecker.assistant.type.AFieldFieldAssistantTC;
import org.overture.typechecker.assistant.type.AFunctionTypeAssistantTC;
import org.overture.typechecker.assistant.type.ANamedInvariantTypeAssistantTC;
import org.overture.typechecker.assistant.type.AOperationTypeAssistantTC;
import org.overture.typechecker.assistant.type.AOptionalTypeAssistantTC;
import org.overture.typechecker.assistant.type.AProductTypeAssistantTC;
import org.overture.typechecker.assistant.type.ARecordInvariantTypeAssistantTC;
import org.overture.typechecker.assistant.type.ASetTypeAssistantTC;
import org.overture.typechecker.assistant.type.AUnionTypeAssistantTC;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;
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
		if (!type.getResolved()) return; else { type.setResolved(false); }
		type.apply(THIS);
	}

	@Override
	public void caseAClassType(AClassType type) throws AnalysisException
	{
		if (type.getResolved())
		{
    		type.setResolved(false);

    		for (PDefinition d: type.getClassdef().getDefinitions())
    		{
    			//PTypeAssistantTC.unResolve(af.createPDefinitionAssistant().getType(d));
    			af.createPTypeAssistant().unResolve(af.createPDefinitionAssistant().getType(d));
    		}
		}
	}

	@Override
	public void caseAFunctionType(AFunctionType type) throws AnalysisException
	{
		if (!type.getResolved()) return; else { type.setResolved(false); }

		for (PType ft: type.getParameters())
		{
			//PTypeAssistantTC.unResolve(ft);
			ft.apply(THIS);
		}

		//PTypeAssistantTC.unResolve(type.getResult());
		type.getResult().apply(THIS);
	}
	@Override
	public void caseANamedInvariantType(ANamedInvariantType type)
			throws AnalysisException
	{
		if (!type.getResolved()) return; else { type.setResolved(false); }
		//PTypeAssistantTC.unResolve(type.getType());
		type.getType().apply(THIS);
	}
	
	@Override
	public void caseARecordInvariantType(ARecordInvariantType type)
			throws AnalysisException
	{
		if (!type.getResolved()) return; else { type.setResolved(false); }

		for (AFieldField f: type.getFields())
		{
			AFieldFieldAssistantTC.unResolve(f);
		}
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
		if (!type.getResolved()) return; else { type.setResolved(false); }

		if (!type.getEmpty())
		{
			//PTypeAssistantTC.unResolve(type.getFrom());
			type.getFrom().apply(THIS);
			//PTypeAssistantTC.unResolve(type.getTo());
			type.getTo().apply(THIS);
		}
	}
	
	@Override
	public void caseAOperationType(AOperationType type)
			throws AnalysisException
	{
		if (!type.getResolved()) return; else { type.setResolved(false); }

		for (PType ot: type.getParameters())
		{
			//PTypeAssistantTC.unResolve(ot);
			ot.apply(THIS);
		}

		//PTypeAssistantTC.unResolve(type.getResult());
		type.getResult().apply(THIS);
	}
	@Override
	public void caseAOptionalType(AOptionalType type) throws AnalysisException
	{
		if (!type.getResolved()) return; else { type.setResolved(false); }
		//PTypeAssistantTC.unResolve(type.getType());
		type.getType().apply(THIS);
	}
	@Override
	public void caseAProductType(AProductType type) throws AnalysisException
	{
		if (!type.getResolved()) return; else { type.setResolved(false); }

		for (PType t: type.getTypes())
		{
			//PTypeAssistantTC.unResolve(t);
			t.apply(THIS);
		}
	}
	@Override
	public void defaultSSeqType(SSeqType type) throws AnalysisException
	{
		if (!type.getResolved()) return; else { type.setResolved(false); }
		//PTypeAssistantTC.unResolve(type.getSeqof());
		type.getSeqof().apply(THIS);
	}
	@Override
	public void caseASetType(ASetType type) throws AnalysisException
	{
		if (!type.getResolved()) return; else { type.setResolved(false); }
		//PTypeAssistantTC.unResolve(type.getSetof()) ;
		type.getSetof().apply(THIS);
	}
	
	@Override
	public void caseAUnionType(AUnionType type) throws AnalysisException
	{
		if (!type.getResolved())
			return;
		else
		{
			type.setResolved(false);
		}

		for (PType t : type.getTypes())
		{
			//PTypeAssistantTC.unResolve(t);
			t.apply(THIS);
		}
	}
	@Override
	public void defaultPType(PType type) throws AnalysisException
	{
		type.setResolved(false);
	}

}
