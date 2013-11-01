package org.overture.typechecker.utilities.type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.PAccessSpecifierAssistantTC;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;
import org.overture.typechecker.util.LexNameTokenMap;

/**
 * Used to get a class type from a type
 * 
 * @author kel
 */
public class ClassTypeFinder extends TypeUnwrapper<AClassType>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ITypeCheckerAssistantFactory af;

	public ClassTypeFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public AClassType caseAClassType(AClassType type) throws AnalysisException
	{
		return type;
	}
	@Override
	public AClassType defaultSInvariantType(SInvariantType type)
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
	public AClassType caseAUnionType(AUnionType type) throws AnalysisException
	{
		
		if (!type.getClassDone())
		{
			type.setClassDone(true); // Mark early to avoid recursion.
			//type.setClassType(PTypeAssistantTC.getClassType(AstFactory.newAUnknownType(type.getLocation())));
			//Rewritten in non static way.
			type.setClassType(af.createPTypeAssistant().getClassType(AstFactory.newAUnknownType(type.getLocation())));
			// Build a class type with the common fields of the contained
			// class types, making the field types the union of the original
			// fields' types...

			Map<ILexNameToken, PTypeSet> common = new HashMap<ILexNameToken, PTypeSet>();
			Map<ILexNameToken, AAccessSpecifierAccessSpecifier> access = new LexNameTokenMap<AAccessSpecifierAccessSpecifier>();
			ILexNameToken classname = null;

			for (PType t : type.getTypes())
			{
				if (af.createPTypeAssistant().isClass(t))
				{
					AClassType ct = t.apply(THIS);//PTypeAssistantTC.getClassType(t);

					if (classname == null)
					{
						classname = ct.getClassdef().getName();
					}

					for (PDefinition f : PDefinitionAssistantTC.getDefinitions(ct.getClassdef()))
					{
						// TypeSet current = common.get(f.name);
						ILexNameToken synthname = f.getName().getModifiedName(classname.getName());
						PTypeSet current = null;

						for (ILexNameToken n : common.keySet())
						{
							if (n.getName().equals(synthname.getName()))
							{
								current = common.get(n);
								break;
							}
						}

						PType ftype = af.createPDefinitionAssistant().getType(f);

						if (current == null)
						{
							common.put(synthname, new PTypeSet(ftype));
						} else
						{
							current.add(ftype);
						}

						AAccessSpecifierAccessSpecifier curracc = access.get(synthname);

						if (curracc == null)
						{
							access.put(synthname, f.getAccess());
						} else
						{
							if (PAccessSpecifierAssistantTC.narrowerThan(curracc, f.getAccess()))
							{
								access.put(synthname, f.getAccess());
							}
						}
					}
				}
			}

			List<PDefinition> newdefs = new Vector<PDefinition>();

			// Note that the pseudo-class is named after one arbitrary
			// member of the union, even though it has all the distinct
			// fields of the set of classes within the union.

			for (ILexNameToken synthname : common.keySet())
			{
				PDefinition def = 
						AstFactory.newALocalDefinition(synthname.getLocation(), synthname, NameScope.GLOBAL, common.get(synthname).getType(type.getLocation()));

				def.setAccess(access.get(synthname).clone());
				newdefs.add(def);
			}

			type.setClassType((classname == null) ? null : AstFactory
					.newAClassType(type.getLocation(), AstFactory
							.newAClassClassDefinition(classname.clone(),
									new LexNameList(), newdefs)));
			
		}

		return type.getClassType();
	}

	@Override
	public AClassType caseAUnknownType(AUnknownType type)
			throws AnalysisException
	{
		
		return AstFactory.newAClassType(type.getLocation(), AstFactory.newAClassClassDefinition());
	}
	
	@Override
	public AClassType defaultPType(PType tyoe) throws AnalysisException
	{
		assert false : "Can't getClass of a non-class";
		return null;
	}
}
