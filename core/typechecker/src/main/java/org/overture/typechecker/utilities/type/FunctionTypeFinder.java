package org.overture.typechecker.utilities.type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.node.NodeList;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Used to get a Function type from a type
 * 
 * @author kel
 */
public class FunctionTypeFinder extends TypeUnwrapper<AFunctionType>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ITypeCheckerAssistantFactory af;

	public FunctionTypeFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public AFunctionType caseAFunctionType(AFunctionType type)
			throws AnalysisException
	{
		return type;
	}

	@Override
	public AFunctionType defaultSInvariantType(SInvariantType type)
			throws AnalysisException
	{
		if (type instanceof ANamedInvariantType)
		{
			return ((ANamedInvariantType) type).getType().apply(THIS);
		}
		else
		{
			return null;
		}
	}
	@Override
	public AFunctionType caseAUnionType(AUnionType type)
			throws AnalysisException
	{
		//return af.createAUnionTypeAssistant().getFunction(type);
		if (!type.getFuncDone())
		{
			type.setFuncDone(true);
			//type.setFuncType(PTypeAssistantTC.getFunction(AstFactory.newAUnknownType(type.getLocation())));
			type.setFuncType(af.createPTypeAssistant().getFunction(AstFactory.newAUnknownType(type.getLocation())));
			
			PTypeSet result = new PTypeSet();
			Map<Integer, PTypeSet> params = new HashMap<Integer, PTypeSet>();
			List<PDefinition> defs = new Vector<PDefinition>();

			for (PType t : type.getTypes())
			{
				if (af.createPTypeAssistant().isFunction(t))
				{
					if (t.getDefinitions() != null)
						defs.addAll(t.getDefinitions());
					AFunctionType f = t.apply(THIS); //PTypeAssistantTC.getFunction(t);
					result.add(f.getResult());

					for (int p = 0; p < f.getParameters().size(); p++)
					{
						PType pt = f.getParameters().get(p);
						PTypeSet pset = params.get(p);

						if (pset == null)
						{
							pset = new PTypeSet(pt);
							params.put(p, pset);
						} else
						{
							pset.add(pt);
						}
					}
				}
			}

			if (!result.isEmpty())
			{
				PType rtype = result.getType(type.getLocation());
				PTypeList plist = new PTypeList();

				for (int i = 0; i < params.size(); i++)
				{
					PType pt = params.get(i).getType(type.getLocation());
					plist.add(pt);
				}

				type.setFuncType(AstFactory.newAFunctionType(type.getLocation(), true, plist, rtype));
				type.getFuncType().setDefinitions(defs);
			} else
			{
				type.setFuncType(null);
			}
		}

		return (AFunctionType) type.getFuncType();
	}
	@Override
	public AFunctionType caseAUnknownType(AUnknownType type)
			throws AnalysisException
	{
		return AstFactory.newAFunctionType(type.getLocation(), true, new NodeList<PType>(null), AstFactory.newAUnknownType(type.getLocation()));
	}
	@Override
	public AFunctionType defaultPType(PType type) throws AnalysisException
	{
		assert false : "Can't getFunction of a non-function";
		return null;
	}
}
