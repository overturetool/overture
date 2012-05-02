package org.overture.umlMapper;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.EDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.assistants.AExplicitFunctionDefinitionAssistantTC;
import org.overture.ast.definitions.assistants.AInheritedDefinitionAssistantTC;
import org.overture.ast.definitions.assistants.PAccessSpecifierAssistantTC;
import org.overture.ast.definitions.assistants.PDefinitionAssistant;
import org.overture.ast.definitions.assistants.PDefinitionAssistantTC;
import org.overture.ast.expressions.EExp;
import org.overture.ast.statements.EStm;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overturetool.umltrans.StatusLog;
import org.overturetool.umltrans.uml.IUmlClass;
import org.overturetool.umltrans.uml.IUmlClassNameType;
import org.overturetool.umltrans.uml.IUmlDefinitionBlock;
import org.overturetool.umltrans.uml.IUmlModel;
import org.overturetool.umltrans.uml.IUmlOwnedProperties;
import org.overturetool.umltrans.uml.IUmlProperty;
import org.overturetool.umltrans.uml.IUmlVisibilityKind;
import org.overturetool.umltrans.uml.UmlClass;
import org.overturetool.umltrans.uml.UmlClassNameType;
import org.overturetool.umltrans.uml.UmlModel;
import org.overturetool.umltrans.uml.UmlProperty;
import org.overturetool.umltrans.uml.UmlVisibilityKind;
import org.overturetool.umltrans.uml.UmlVisibilityKindQuotes;
import org.overturetool.vdmj.lex.LexNameToken;

public class Vdm2Uml {

	private StatusLog log = null;	
	private Vector<String> filteredClassNames = null;
	private Set<IUmlClass> nestedClasses = new HashSet<IUmlClass>();
	
	public Vdm2Uml() {
		
		try {
			log = new StatusLog();
		} catch (CGException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		filteredClassNames = new Vector<String>();
	}
	
	public IUmlModel init(List<SClassDefinition> classes)
	{
		IUmlModel model = null;
		
		try {
			model = buildUml(classes);
		} catch (CGException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return model;
	}

	private IUmlModel buildUml(List<SClassDefinition> classes) throws CGException {
		
		HashSet<IUmlClass> umlClasses = new HashSet<IUmlClass>();
		
		for (SClassDefinition sClass : classes) {
			
			if(!filteredClassNames.contains(sClass.getName().name))
			{
				umlClasses.add(buildClass(sClass));	
			}
			
		}
		
		umlClasses.addAll(nestedClasses);
		return new UmlModel("Root", umlClasses);
	}

	private IUmlClass buildClass(SClassDefinition sClass) throws CGException {
		
		String name = sClass.getName().name;
		log.addNewClassInfo(name);
		
		
		
		boolean isStatic = false;
		boolean isActive = isClassActive(sClass);
		boolean isAbstract = hasSubclassResponsabilityDefinition(sClass.getDefinitions());
		HashSet<IUmlDefinitionBlock> dBlocks = buildDefinitionBlocks(sClass.getDefinitions(),name);
		Vector<IUmlClassNameType> supers = getSuperClasses(sClass);
		IUmlVisibilityKind visibility = new UmlVisibilityKind(UmlVisibilityKindQuotes.IQPUBLIC);
		
		
		return new UmlClass(name,dBlocks,isAbstract,supers,visibility,isStatic,isActive,null);
	}

	private Vector<IUmlClassNameType> getSuperClasses(SClassDefinition sClass) throws CGException {
		Vector<IUmlClassNameType> result = new Vector<IUmlClassNameType>();
		List<LexNameToken> superNames = sClass.getSupernames();
		
		for (LexNameToken superName : superNames) {
			result.add(new UmlClassNameType(superName.name));
		}
		
		return result;
	}

	private HashSet<IUmlDefinitionBlock> buildDefinitionBlocks(
			LinkedList<PDefinition> definitions, String owner) throws CGException {

		HashSet<IUmlDefinitionBlock> result = new HashSet<IUmlDefinitionBlock>();
		HashSet<IUmlProperty> instanceVariables = new HashSet<IUmlProperty>();
		
		
		for (PDefinition pDefinition : definitions) {
			switch (pDefinition.kindPDefinition()) {
			case EXPLICITFUNCTION:
				break;
			case EXPLICITOPERATION:
				break;
			case IMPLICITFUNCTION:
				break;
			case IMPLICITOPERATION:
				break;
			case INSTANCEVARIABLE:
				instanceVariables.add(buildDefinitionBlock((AInstanceVariableDefinition)pDefinition,owner));
				break;
			case TYPE:
				break;
			case VALUE:
				break;
			default:
				break;
			}
		}
		
		return result;
	}

	private IUmlProperty buildDefinitionBlock(AInstanceVariableDefinition variable,
			String owner) throws CGException {
		
		AAccessSpecifierAccessSpecifier accessSpecifier = variable.getAccess();
		String name = variable.getName().name;
		IUmlVisibilityKind visibility = convertAccessSpecifierToVisibility(accessSpecifier);
		boolean isReadOnly = false;
		boolean isStatic = PAccessSpecifierAssistantTC.isStatic(accessSpecifier);
		
		
		
		//IUmlProperty result = new UmlProperty(name, visibility , p3, p4, p5, p6, p7, p8, p9, p10, p11)
		return null ;
	}

	private IUmlVisibilityKind convertAccessSpecifierToVisibility(
			AAccessSpecifierAccessSpecifier accessSpecifier) throws CGException {
		
		if(PAccessSpecifierAssistantTC.isPrivate(accessSpecifier))
		{
			return new UmlVisibilityKind(UmlVisibilityKindQuotes.IQPRIVATE);
		}
		else if(PAccessSpecifierAssistantTC.isProtected(accessSpecifier))
		{
			return new UmlVisibilityKind(UmlVisibilityKindQuotes.IQPROTECTED);
		}
		
		return new UmlVisibilityKind(UmlVisibilityKindQuotes.IQPUBLIC);
		
	}

	private boolean hasSubclassResponsabilityDefinition(
			LinkedList<PDefinition> definitions) {
		
		for (PDefinition pDefinition : definitions) {
			if(isSubclassResponsability(pDefinition))
				return true;
		}
		
		return false;
	}

	private boolean isSubclassResponsability(PDefinition pDefinition) {
		
		if(PDefinitionAssistantTC.isOperation(pDefinition))
		{
			if(pDefinition instanceof AExplicitOperationDefinition)
			{
				if(((AExplicitOperationDefinition)pDefinition).getBody().kindPStm() == EStm.SUBCLASSRESPONSIBILITY)
				{
					return true;
				}
			}
			else if(pDefinition instanceof AImplicitOperationDefinition)
			{				
				PStm body = ((AImplicitOperationDefinition)pDefinition).getBody();
				//implicit operations may or may not have a body
				if(body == null)
				{
					return true;
				}
				else
				{
					if(body.kindPStm() == EStm.SUBCLASSRESPONSIBILITY)
					{
						return true;
					}
				}
			}
		}
		
		return false;
	}

	private boolean isClassActive(SClassDefinition sClass) {
		
		for (PDefinition def : sClass.getDefinitions()) {
			if(def.kindPDefinition() == EDefinition.THREAD)
				return true;
		}
		return false;
	}

	
	
}
