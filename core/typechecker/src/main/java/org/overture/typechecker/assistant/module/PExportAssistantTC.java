package org.overture.typechecker.assistant.module;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AUntypedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.modules.AFunctionExport;
import org.overture.ast.modules.AOperationExport;
import org.overture.ast.modules.ATypeExport;
import org.overture.ast.modules.AValueExport;
import org.overture.ast.modules.PExport;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.PDefinitionListAssistantTC;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class PExportAssistantTC
{

	public static Collection<? extends PDefinition> getDefinition(PExport exp,
			LinkedList<PDefinition> actualDefs)
	{
		switch(exp.kindPExport())
		{
			case ALL:
				return actualDefs;		// The lot!
			case FUNCTION:
			{
				List<PDefinition> list = new Vector<PDefinition>();

				for (LexNameToken name: ((AFunctionExport)exp).getNameList())
				{
					PDefinition def = PDefinitionListAssistantTC.findName(actualDefs,name, NameScope.NAMES);

					if (def == null)
					{
						TypeCheckerErrors.report(3183, "Exported function " + name + " not defined in module", name.getLocation(),exp);
					}
					else
					{
						PType act = PDefinitionAssistantTC.getType(def);
						PType type = ((AFunctionExport)exp).getExportType();

						if (act != null && !PTypeAssistantTC.equals(act, type))
						{
							TypeCheckerErrors.report(3184, "Exported " + name + " function type incorrect",name.location,exp);
							TypeCheckerErrors.detail2("Exported", type, "Actual", act);
						}

						list.add(def);
					}
				}

				return list;
			}
				
			case OPERATION:
				{
					List<PDefinition> list = new Vector<PDefinition>();

					for (LexNameToken name: ((AOperationExport)exp).getNameList())
					{
						PDefinition def = PDefinitionListAssistantTC.findName(actualDefs,name, NameScope.NAMES);

						if (def == null)
						{
							TypeCheckerErrors.report(3185, "Exported operation " + name + " not defined in module",name.location,exp);
						}
						else
						{
							PType act = def.getType();
							PType type = ((AOperationExport)exp).getExportType();

							if (act != null && !PTypeAssistantTC.equals(act, type))
							{
								TypeCheckerErrors.report(3186, "Exported operation type does not match actual type",name.location,exp);
								TypeCheckerErrors.detail2("Exported", type, "Actual", act);
							}

							list.add(def);
						}
					}

					return list;
				}
			case TYPE:
				{	
					LexNameToken name = ((ATypeExport)exp).getName();
					List<PDefinition> list = new Vector<PDefinition>();
					PDefinition def = PDefinitionListAssistantTC.findType(actualDefs, name, name.module);

					if (def == null)
					{
						TypeCheckerErrors.report(3187, "Exported type " + name + " not defined in module",name.location,exp);
					}
					else
					{
						if (((ATypeExport)exp).getStruct())
						{
							list.add(def);
						}
						else
						{
							PType type = PDefinitionAssistantTC.getType(def);

							if (type instanceof ANamedInvariantType)
							{
								ANamedInvariantType ntype = (ANamedInvariantType)type;
								SInvariantType copy = 
										AstFactory.newANamedInvariantType(ntype.getName().clone(), ntype.getType());
										//new ANamedInvariantType(ntype.getName().getLocation(),false,list, false, null, ntype.getName().clone(), ntype.getType());
								copy.setOpaque(true);
								copy.setInvDef(ntype.getInvDef());
								list.add(AstFactory.newATypeDefinition(def.getName(), copy, null, null));
								//list.add(new ATypeDefinition(def.getName().location, NameScope.TYPENAME,false,null,PAccessSpecifierAssistant.getDefault(),null, copy, null,null,null,false,def.getName()));
							}
							else if (type instanceof ARecordInvariantType)
							{
								ARecordInvariantType rtype = (ARecordInvariantType)type;
								@SuppressWarnings("unchecked")
								SInvariantType copy =
										AstFactory.newARecordInvariantType(rtype.getName().clone(), (List<AFieldField>) rtype.getFields().clone());
										//new ARecordInvariantType(rtype.getName().location,false, rtype.getName().clone(), (List<? extends AFieldField>) rtype.getFields().clone());
								copy.setOpaque(true);
								copy.setInvDef(rtype.getInvDef());
								list.add(
										AstFactory.newATypeDefinition(def.getName(), copy, null, null));
										//new ATypeDefinition(def.getName().location, NameScope.TYPENAME,false,null,PAccessSpecifierAssistant.getDefault(),null, copy,null,null,null,false,def.getName()));
							}
							else
							{
								TypeCheckerErrors.report(67, "Exported type " + name + " not structured",name.location,exp);
							}
						}
					}

					return list;
				}
			case VALUE:
			{
				List<PDefinition> list = new Vector<PDefinition>();

				for (LexNameToken name: ((AValueExport)exp).getNameList())
				{
					PDefinition def = PDefinitionListAssistantTC.findName(actualDefs,name, NameScope.NAMES);
					PType type = ((AValueExport)exp).getExportType().clone();

					if (def == null)
					{
						TypeCheckerErrors.report(3188, "Exported value " + name + " not defined in module",name.location,exp);
					}
					else if (def instanceof AUntypedDefinition)
					{
						AUntypedDefinition untyped = (AUntypedDefinition)def;
						list.add(
								AstFactory.newALocalDefinition(untyped.getLocation(), untyped.getName(), NameScope.GLOBAL, type));
								//new ALocalDefinition(untyped.getLocation(), NameScope.GLOBAL, false,null,PAccessSpecifierAssistant.getDefault(),type, false,untyped.getName()));
					}
					else
					{
						PType act = def.getType();
						

						if (act != null && !act.equals(type))
						{
							TypeCheckerErrors.report(3189, "Exported type does not match actual type",act.getLocation(),act);
							TypeCheckerErrors.detail2("Exported", type, "Actual", act);
						}

						list.add(def);
					}
				}

				return list;
			}
				
			
		}
		assert false;// "No match in switch";
		return null;
	}

	public static Collection<? extends PDefinition> getDefinition(PExport exp)
	{
		switch(exp.kindPExport())
		{
			case ALL:
				return new LinkedList<PDefinition>();	// Nothing <shrug>
			case FUNCTION:
			{
				List<PDefinition> list = new Vector<PDefinition>();
				//AAccessSpecifierAccessSpecifier
				for (LexNameToken name: ((AFunctionExport)exp).getNameList())
				{
					list.add(
							AstFactory.newALocalDefinition(name.location, name.clone(), NameScope.GLOBAL, ((AFunctionExport)exp).getExportType()));
//							new ALocalDefinition(name.location, NameScope.GLOBAL,true,null,
//							new AAccessSpecifierAccessSpecifier(new APublicAccess(),new TStatic(),null),
//							((AFunctionExport)exp).getExportType(),false,name.clone()));
				}

				return list;
			}
				
			case OPERATION:
				{
					List<PDefinition> list = new Vector<PDefinition>();

					for (LexNameToken name: ((AOperationExport)exp).getNameList())
					{
						list.add(
								AstFactory.newALocalDefinition(name.location, name.clone(), NameScope.GLOBAL, ((AOperationExport)exp).getExportType()));
//								new ALocalDefinition(name.location, NameScope.GLOBAL,true,null,
//								new AAccessSpecifierAccessSpecifier(new APublicAccess(),new TStatic(),null),
//								((AOperationExport)exp).getExportType(),false,name.clone()));
					}

					return list;
				}
			case TYPE:
				{	
					return new LinkedList<PDefinition>();
				}
			case VALUE:
			{
				List<PDefinition> list = new Vector<PDefinition>();
				
				for (LexNameToken name: ((AValueExport)exp).getNameList())
				{
					list.add(
							AstFactory.newALocalDefinition(name.location, name.clone(), NameScope.GLOBAL, ((AValueExport)exp).getExportType()));
//							new ALocalDefinition(name.location, NameScope.GLOBAL,true,null,
//							new AAccessSpecifierAccessSpecifier(new APublicAccess(),new TStatic(),null),
//							((AValueExport)exp).getExportType(),true,name.clone()));
				}

				return list;
			}
		}
		assert false;// "No match in switch";
		return null;
	}
}
