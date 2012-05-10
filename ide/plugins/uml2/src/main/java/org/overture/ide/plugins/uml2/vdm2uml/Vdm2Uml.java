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
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.VisibilityKind;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.EDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.assistants.PDefinitionAssistant;
import org.overture.ast.definitions.assistants.PDefinitionAssistantTC;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;

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
		
		addPrimitiveTypes();
		for (SClassDefinition sClass : classes) {
			
			String className = sClass.getName().name;
			if(!filteredClassNames.contains(className))
			{								
				Class class_ = this.classes.get(className);
				addRestOfTypes(class_,sClass);
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

	private void addRestOfTypes(Class class_, SClassDefinition sClass) {		
		
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
		
		createNewUmlType(type);
		
	}


	private void createNewUmlType(PType type) {
		switch (type.kindPType()) {		
		case BASIC:
		case BRACKET:
		case CLASS:
		case FUNCTION:
		case INVARIANT:
			createNewUmlInvariantType((SInvariantType) type);
			break;
		case MAP:
		case OPERATION:
		case OPTIONAL:
		case PARAMETER:
		case PRODUCT:
		case QUOTE:
		case SEQ:
		case SET:
		case UNDEFINED:
		case UNION:
		case UNKNOWN:
		case UNRESOLVED:
		case VOID:
		case VOIDRETURN:

		default:
			break;
		}
		
	}


	private void createNewUmlInvariantType(SInvariantType type) {
		switch (type.kindSInvariantType()) {
		case NAMED:
			PType ptype = ((ANamedInvariantType)type).getType();
			createNewUmlType(ptype);
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
			default:
				System.out.println(def);
				break;			
			}
		}
		
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
			//might be a class or other thing?!
			Class referencedClass = getClassName(defType);
			
			class_.createAssociation(true, AggregationKind.COMPOSITE_LITERAL, name, Vdm2UmlUtil.extractLower(defType), Vdm2UmlUtil.extractUpper(defType), referencedClass, true, AggregationKind.COMPOSITE_LITERAL, "dont know", 1, 1);
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
