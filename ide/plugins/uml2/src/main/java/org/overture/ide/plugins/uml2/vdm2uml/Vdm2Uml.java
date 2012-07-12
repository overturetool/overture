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
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;

public class Vdm2Uml
{
	UmlTypeCreator utc = new UmlTypeCreator();
	private Vector<String> filteredClassNames = new Vector<String>();
	private Model modelWorkingCopy = null;
	private Map<String, Class> classes = new HashMap<String, Class>();
	

	public Vdm2Uml()
	{

	}

	public Model init(List<SClassDefinition> classes)
	{
		modelWorkingCopy = UMLFactory.eINSTANCE.createModel();
		
		utc.setModelWorkingCopy(modelWorkingCopy);

		buildUml(classes);

		return modelWorkingCopy;
	}

	public void save(URI uri) throws IOException
	{

		Resource resource = new ResourceSetImpl().createResource(uri.appendFileExtension(UMLResource.FILE_EXTENSION));
		resource.getContents().add(modelWorkingCopy);

		resource.save(null);
	}

	private void buildUml(List<SClassDefinition> classes)
	{

		//Build class container
		for (SClassDefinition sClass : classes)
		{
			String className = sClass.getName().name;
			if (!filteredClassNames.contains(className))
			{
				Class class_ = buildClass(sClass);
				this.classes.put(className, class_);
			}
		}
		
		//TODO: build inheritance relationship

		// addPrimitiveTypes();
		
		
		//Create types embedded in VDM classes
		for (SClassDefinition sClass : classes)
		{
			String className = sClass.getName().name;
			if (!filteredClassNames.contains(className))
			{
				Class class_ = this.classes.get(className);
				addTypes(class_, sClass);
			}
		}

		//Build operations, functions, instance variables and values
		for (SClassDefinition sClass : classes)
		{
			String className = sClass.getName().name;
			if (!filteredClassNames.contains(className))
			{
				Class class_ = this.classes.get(className);
				addAttributesToClass(class_, sClass);
			}
		}

	}

	private void addTypes(Class class_, SClassDefinition sClass)
	{
		for (PDefinition def : sClass.getDefinitions())
		{
			switch (def.kindPDefinition())
			{
				case TYPE:
					addTypeToModel(class_, def);
					break;
				default:
					break;
			}
		}

	}

	private void addTypeToModel(Class class_, PDefinition def)
	{
		if (utc.types.containsKey(def.getName().name))
		{
			return;
		}
		PType type = PDefinitionAssistantTC.getType(def);

		utc.create(def.getName(), type);

	}


	

	private void addAttributesToClass(Class class_, SClassDefinition sClass)
	{

		for (PDefinition def : sClass.getDefinitions())
		{

			switch (def.kindPDefinition())
			{

				case INSTANCEVARIABLE:
					addInstanceVariableToClass(class_, (AInstanceVariableDefinition) def);
					break;
				case EXPLICITOPERATION:
					addExplicitOperationToClass(class_, (AExplicitOperationDefinition) def);
					break;
				case VALUE:
					addValueToClass(class_, (AValueDefinition) def);
				default:
					break;
			}
		}

	}

	private void addValueToClass(Class class_, AValueDefinition def)
	{

		PType type = PDefinitionAssistantTC.getType(def);
		Type umlType = utc.getUmlType(type);

		Property s = class_.createOwnedAttribute(getDefName(def), umlType);

		System.out.println(s);

	}

	private String getDefName(PDefinition def)
	{
		switch (def.kindPDefinition())
		{
			case VALUE:
				AValueDefinition valueDef = (AValueDefinition) def;
				PPattern expression = valueDef.getPattern();
				if (expression instanceof AIdentifierPattern)
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
			AExplicitOperationDefinition def)
	{

		class_.createOwnedOperation(def.getName().name, null, null, null);

	}

	private void addInstanceVariableToClass(Class class_,
			AInstanceVariableDefinition def)
	{

		String name = def.getName().name;
		PType defType = PDefinitionAssistantTC.getType(def);

		Type type = utc.getUmlType(defType);
		if (type != null)
		{
			class_.createOwnedAttribute(name, type);

		} else
		{

			if (defType instanceof AClassType)
			{
				Class referencedClass = getClassName(defType);
				class_.createAssociation(false, AggregationKind.NONE_LITERAL, name, Vdm2UmlUtil.extractLower(defType), Vdm2UmlUtil.extractUpper(defType), referencedClass, false, AggregationKind.NONE_LITERAL, "", 1, 1);
			}
		}

	}

	private Class getClassName(PType defType)
	{
		switch (defType.kindPType())
		{
			case CLASS:
				return classes.get(((AClassType) defType).getName().name);
			default:
				break;
		}

		return null;
	}

	

	private Class buildClass(SClassDefinition sClass)
	{
		String name = sClass.getName().name;
		boolean isAbstract = Vdm2UmlUtil.hasSubclassResponsabilityDefinition(sClass.getDefinitions());
		Class class_ = modelWorkingCopy.createOwnedClass(name, isAbstract);

		boolean isActive = Vdm2UmlUtil.isClassActive(sClass);
		class_.setIsActive(isActive);

		class_.setVisibility(VisibilityKind.PUBLIC_LITERAL);

		return class_;
	}

	

//	private Type convertTypeInvariant(SInvariantType definitionType)
//	{
//		Type result = null;
//
//		switch (definitionType.kindSInvariantType())
//		{
//			case NAMED:
//				String name = ((ANamedInvariantType) definitionType).getName().name;
//				result = types.get(name);
//				break;
//			case RECORD:
//				break;
//
//		}
//		System.out.println();
//
//		return result;
//	}

}
