package org.overture.typechecker.utilities.type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

/**
 * Used to determine if a type is a operation type
 * 
 * @author kel
 */
public class OperationBasisChecker extends TypeUnwrapper<Boolean>
{

	protected ITypeCheckerAssistantFactory af;

	public OperationBasisChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public Boolean defaultSInvariantType(SInvariantType type)
			throws AnalysisException
	{
		if (type instanceof ANamedInvariantType)
		{
			if (type.getOpaque())
			{
				return false;
			}
			return ((ANamedInvariantType) type).getType().apply(THIS);// PTypeAssistantTC.isOperation(type.getType());
		} else
		{
			return false;
		}

	}

	@Override
	public Boolean caseAOperationType(AOperationType node)
			throws AnalysisException
	{
		return true;
	}

	@Override
	public Boolean caseAUnionType(AUnionType type) throws AnalysisException
	{
		//return af.createAUnionTypeAssistant().getOperation(type) != null;
		
//		if (!type.getOpDone())
//		{
//			type.setOpDone(true);
//			type.setOpType(PTypeAssistantTC.getOperation(AstFactory.newAUnknownType(type.getLocation())));
//
//			PTypeSet result = new PTypeSet();
//			Map<Integer, PTypeSet> params = new HashMap<Integer, PTypeSet>();
//			List<PDefinition> defs = new Vector<PDefinition>();
//
//			for (PType t : type.getTypes())
//			{
//				if (PTypeAssistantTC.isOperation(t))
//				{
//					if (t.getDefinitions() != null)
//					{
//						defs.addAll(t.getDefinitions());
//					}
//					AOperationType op = PTypeAssistantTC.getOperation(t);
//					result.add(op.getResult());
//
//					for (int p = 0; p < op.getParameters().size(); p++)
//					{
//						PType pt = op.getParameters().get(p);
//						PTypeSet pset = params.get(p);
//
//						if (pset == null)
//						{
//							pset = new PTypeSet(pt);
//							params.put(p, pset);
//						} else
//						{
//							pset.add(pt);
//						}
//					}
//				}
//			}
//
//			if (!result.isEmpty())
//			{
//				PType rtype = result.getType(type.getLocation());
//				PTypeList plist = new PTypeList();
//
//				for (int i = 0; i < params.size(); i++)
//				{
//					PType pt = params.get(i).getType(type.getLocation());
//					plist.add(pt);
//				}
//
//				type.setOpType(AstFactory.newAOperationType(type.getLocation(), plist, rtype));
//				type.getOpType().setDefinitions(defs);
//			} else
//			{
//				type.setOpType(null);
//			}
//		}
//
//		return (AOperationType) type.getOpType() != null;
		return type.apply(af.getOperationTypeFinder()) !=null;
	}

	@Override
	public Boolean caseAUnknownType(AUnknownType type) throws AnalysisException
	{
		return true;
	}

	@Override
	public Boolean defaultPType(PType type) throws AnalysisException
	{
		return false;
	}

}
