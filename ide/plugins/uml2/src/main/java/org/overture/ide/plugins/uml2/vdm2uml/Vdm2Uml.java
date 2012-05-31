package org.overture.ide.plugins.uml2.vdm2uml;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.VisibilityKind;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.assistants.AValueDefinitionAssistantTC;
import org.overture.ast.definitions.assistants.PDefinitionAssistantTC;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SBasicType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.assistants.PTypeAssistantTC;
import org.overturetool.vdmj.lex.LexNameToken;

public class Vdm2Uml {

	private Vector<String> filteredClassNames = new Vector<String>();
	private Model modelWorkingCopy = null; 
	private Map<String,Class> classes = new HashMap<String, Class>();
	private Map<String,Type> types = new HashMap<String, Type>();	
	
	public Vdm2Uml() {
		
	}
	
	
	public Model init(List<SClassDefinition> classes)
	{
		modelWorkingCopy = UMLFactory.eINSTANCE.createModel();		
		
		buildUml(classes);
		
		return modelWorkingCopy;
	}

	public  void save(URI uri)
			throws IOException {

		Resource resource = new ResourceSetImpl().createResource(uri.appendFileExtension(UMLResource.FILE_EXTENSION));
		resource.getContents().add(modelWorkingCopy);

		resource.save(null);
	}

	private void buildUml(List<SClassDefinition> classes)  {
		
		
		
		for (SClassDefinition sClass : classes) {
			
			String className = sClass.getName().name;
			if(!filteredClassNames.contains(className))
			{
				Class class_ = buildClass(sClass);
				this.classes.put(className, class_);
			}			
		}
		
	//	addPrimitiveTypes();
		for (SClassDefinition sClass : classes) {
			
			String className = sClass.getName().name;
			if(!filteredClassNames.contains(className))
			{								
				Class class_ = this.classes.get(className);
				addTypes(class_,sClass);
			}			
		}
		
		for (SClassDefinition sClass : classes) {
			
			String className = sClass.getName().name;
			if(!filteredClassNames.contains(className))
			{								
				Class class_ = this.classes.get(className);
				addAttributesToClass(class_,sClass);
			}			
		}
	
		
	}

	private void addTypes(Class class_, SClassDefinition sClass) {		
		
		for (PDefinition def : sClass.getDefinitions()) {

			switch (def.kindPDefinition()) {
			case TYPE:
				addTypeToModel(class_,def);
				break;
			default:
				break;
			}
		}

	}

	private void addTypeToModel(Class class_, PDefinition def) {
		if(types.containsKey(def.getName().name))
		{
			return;
		}
		PType type = PDefinitionAssistantTC.getType(def);
		
		createNewUmlType(def.getName(),type);
		
	}


	private void createNewUmlType(LexNameToken name, PType type) {
		
		
		switch (type.kindPType()) {		
		case UNION:
			createNewUmlUnionType(name,(AUnionType) type);
			break;
		case INVARIANT:
			createNewUmlInvariantType(name,(SInvariantType) type);
			break;
		case BASIC:
			Vdm2UmlUtil.convertBasicType((SBasicType) type, modelWorkingCopy, types,name);
		case BRACKET:
		case CLASS:
		case FUNCTION:			
		case MAP:
		case OPERATION:
		case OPTIONAL:
		case PARAMETER:
		case PRODUCT:
		case QUOTE:
		case SEQ:
		case SET:
		case UNDEFINED:
		case UNKNOWN:
		case UNRESOLVED:
		case VOID:
		case VOIDRETURN:

		default:
			break;
		}
		
	}


	private void createNewUmlUnionType(LexNameToken name, AUnionType type) {
		
		if(Vdm2UmlUtil.isUnionOfQuotes(type))
		{
			Enumeration enumeration = modelWorkingCopy.createOwnedEnumeration(name.name);
			for (PType t : type.getTypes()) {
				if(t instanceof AQuoteType)
				{
					enumeration.createOwnedLiteral(((AQuoteType) t).getValue().value);
				}
			}
			
			types.put(name.name, enumeration);
		}
		else
		{
			//do the constraint XOR
			
		}
		
		
		
	}

	
	
	private void createNewUmlInvariantType(LexNameToken name, SInvariantType type) {
		switch (type.kindSInvariantType()) {
		case NAMED:
			PType ptype = ((ANamedInvariantType)type).getType();
			createNewUmlType(name,ptype);
			break;
		case RECORD:
			break;
		}
		
	}


	private void addAttributesToClass(Class class_, SClassDefinition sClass) {

		for (PDefinition def : sClass.getDefinitions()) {
			
			switch (def.kindPDefinition()) {
			
			case INSTANCEVARIABLE:
				addInstanceVariableToClass(class_,(AInstanceVariableDefinition)def);
				break;
			case EXPLICITOPERATION:
				addExplicitOperationToClass(class_,(AExplicitOperationDefinition) def);
				break;
			case VALUE:
				addValueToClass(class_,(AValueDefinition)def);
			default:
				break;			
			}
		}
		
	}


	private void addValueToClass(Class class_, AValueDefinition def) {
		
		
		PType type = PDefinitionAssistantTC.getType(def);
		Type umlType = getUmlType(type);
		
		
		Property s = class_.createOwnedAttribute(getDefName(def), umlType);
		
		System.out.println(s);
		
		
	}


	private String getDefName(PDefinition def) {
		switch (def.kindPDefinition()) {
		case VALUE:
			AValueDefinition valueDef = (AValueDefinition) def;
			PPattern expression = valueDef.getPattern();
			if(expression instanceof AIdentifierPattern)
			{
				return ((AIdentifierPattern) expression).getName().name;
			}
			break;
		default:
			return def.getName().name;
		}
		return "null";
	}


	private void addExplicitOperationToClass(Class class_,
			AExplicitOperationDefinition def) {
		
		class_.createOwnedOperation(def.getName().name, null, null, null);
		
	}


	private void addInstanceVariableToClass(Class class_,
			AInstanceVariableDefinition def) {
		
		String name = def.getName().name;
		PType defType = PDefinitionAssistantTC.getType(def);
		
		Type type =  getUmlType(defType);
		if(type != null)
		{
			class_.createOwnedAttribute(name, type);
			
		}
		else
		{
			
			if(defType instanceof AClassType)
			{
				Class referencedClass = getClassName(defType);
				class_.createAssociation(false, AggregationKind.NONE_LITERAL, name, Vdm2UmlUtil.extractLower(defType), Vdm2UmlUtil.extractUpper(defType), referencedClass, false, AggregationKind.NONE_LITERAL, "", 1, 1);
			}
		}
		
		
	}



	private Class getClassName(PType defType) {
		switch (defType.kindPType()) {
		case CLASS:
			return classes.get(((AClassType)defType).getName().name);		
		default:
			break;			
		}
		
		return null;
	}


	private void addPrimitiveTypes() {
		
		types.put("int", modelWorkingCopy.createOwnedPrimitiveType("int"));
		types.put("bool",modelWorkingCopy.createOwnedPrimitiveType("bool"));
		types.put("nat", modelWorkingCopy.createOwnedPrimitiveType("nat"));
		types.put("nat1", modelWorkingCopy.createOwnedPrimitiveType("nat1"));
		types.put("real", modelWorkingCopy.createOwnedPrimitiveType("real"));
		types.put("char", modelWorkingCopy.createOwnedPrimitiveType("char"));
		types.put("token", modelWorkingCopy.createOwnedPrimitiveType("token"));
		types.put("String", modelWorkingCopy.createOwnedPrimitiveType("String"));
		
	}



	private Class buildClass(SClassDefinition sClass)  {
		
		String name = sClass.getName().name;
		boolean isAbstract = Vdm2UmlUtil.hasSubclassResponsabilityDefinition(sClass.getDefinitions());
		Class class_ = modelWorkingCopy.createOwnedClass(name, isAbstract);
		
		boolean isActive = Vdm2UmlUtil.isClassActive(sClass);
		class_.setIsActive(isActive);
		
		class_.setVisibility(VisibilityKind.PUBLIC_LITERAL);
		
		
		return class_;
		//buildDefinitionBlocks(class_,sClass.getDefinitions());
		
	}

	

	
	public  Type getUmlType(PType type) {
		Type result = null;
		
		switch (type.kindPType()) {
		case BASIC:
			result = Vdm2UmlUtil.convertBasicType((SBasicType) type,modelWorkingCopy,types);
			break;
		case BRACKET:
			break;
		case FUNCTION:
			break;
		case INVARIANT:
			result =  convertTypeInvariant((SInvariantType) type);
			break;
		case MAP:
			break;
		case OPERATION:
			break;
		case OPTIONAL:
			break;
		case PARAMETER:
			break;
		case PRODUCT:
			break;
		case QUOTE:
			break;
		case SEQ:
			//convertTypeSeq(model,(SSeqType) definitionType);
			break;
		case SET:
			break;
		case UNDEFINED:
			break;
		case UNION:
			break;
		case UNKNOWN:
			break;
		case UNRESOLVED:
			break;
		case VOID:
			break;
		case VOIDRETURN:
			break;
		
		}		
		return result;
	}

	
	

	private Type convertTypeInvariant(SInvariantType definitionType) {
		Type result = null;
		
		switch (definitionType.kindSInvariantType()) {
		case NAMED:
			String name = ((ANamedInvariantType) definitionType).getName().name;
			result = this.types.get(name);
			break;
		case RECORD:
			break;
		
		}
		System.out.println();

		return result;
	}

	

	
	
}
