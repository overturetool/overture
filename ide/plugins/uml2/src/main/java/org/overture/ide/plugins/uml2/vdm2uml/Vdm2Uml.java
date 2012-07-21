package org.overture.ide.plugins.uml2.vdm2uml;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.VisibilityKind;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.PType;
import org.overture.interpreter.assistant.pattern.PPatternAssistantInterpreter;
import org.overture.typechecker.assistant.definition.PAccessSpecifierAssistantTC;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;

public class Vdm2Uml
{
	UmlTypeCreator utc = new UmlTypeCreator(new UmlTypeCreator.ClassTypeLookup()
	{
		public Class lookup(AClassType type)
		{
			return classes.get(type.getName().name);
		}
	});
	private Model modelWorkingCopy = null;
	private Map<String, Class> classes = new HashMap<String, Class>();

	public Model init(String name, List<SClassDefinition> classes)
	{
		modelWorkingCopy = UMLFactory.eINSTANCE.createModel();
		modelWorkingCopy.setName(name);

		utc.setModelWorkingCopy(modelWorkingCopy);

		List<SClassDefinition> onlyClasses = new Vector<SClassDefinition>();
		onlyClasses.addAll(classes);
		for (SClassDefinition sClassDefinition : classes)
		{
			if (sClassDefinition instanceof AClassClassDefinition)
			{
				continue;
			}
			onlyClasses.remove(sClassDefinition);
		}
		buildUml(onlyClasses);

		new UmlDeploymentCreator(modelWorkingCopy).buildDeployment(classes);

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

		// Build class container
		for (SClassDefinition sClass : classes)
		{
			String className = sClass.getName().name;
			Class class_ = buildClass(sClass);
			this.classes.put(className, class_);
		}

		// TODO: build inheritance relationship
		for (SClassDefinition sClass : classes)
		{
			Class thisClass = this.classes.get(sClass.getName().name);
			for (LexNameToken superToken : sClass.getSupernames())
			{
				Class superClass = this.classes.get(superToken.name);
				thisClass.createGeneralization(superClass);
			}
		}

		// addPrimitiveTypes();

		// Create types embedded in VDM classes
		for (SClassDefinition sClass : classes)
		{
			String className = sClass.getName().name;
			Class class_ = this.classes.get(className);
			addTypes(class_, sClass);
		}

		// Build operations, functions, instance variables and values
		for (SClassDefinition sClass : classes)
		{
			String className = sClass.getName().name;
			Class class_ = this.classes.get(className);
			addAttributesToClass(class_, sClass);
		}

	}

	private void addTypes(Class class_, SClassDefinition sClass)
	{
		for (PDefinition def : sClass.getDefinitions())
		{
			switch (def.kindPDefinition())
			{
				case TYPE:
				{
					PType type = PDefinitionAssistantTC.getType(def);
					utc.create(class_, type);
					break;
				}
				default:
					break;
			}
		}

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
				case EXPLICITFUNCTION:
					addExplicitFunctionToClass(class_, (AExplicitFunctionDefinition) def);
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
		String name = getDefName(def);
		PType defType = PDefinitionAssistantTC.getType(def);
		utc.create(class_, defType);
		Type umlType = utc.getUmlType(defType);

		if (defType instanceof AClassType)
		{
			// TODO static
			Class referencedClass = getClassName(defType);
			Association association = class_.createAssociation(false, AggregationKind.NONE_LITERAL, name, Vdm2UmlUtil.extractLower(defType), Vdm2UmlUtil.extractUpper(defType), referencedClass, false, AggregationKind.NONE_LITERAL, "", 1, 1);
			association.setVisibility(Vdm2UmlUtil.convertAccessSpecifierToVisibility(def.getAccess()));
		} else
		{
			Property s = class_.createOwnedAttribute(name, umlType);
			// s.setIsStatic(true);
			s.setIsStatic(PAccessSpecifierAssistantTC.isStatic(def.getAccess()));
			s.setVisibility(Vdm2UmlUtil.convertAccessSpecifierToVisibility(def.getAccess()));
			s.setIsReadOnly(true);

			if (def.getExpression() != null)
			{
				s.setDefault(def.getExpression().toString());
			}
		}

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

	private void addExplicitFunctionToClass(Class class_,
			AExplicitFunctionDefinition def)
	{
		EList<String> names = new BasicEList<String>();
		for (PPattern p : def.getParamPatternList().get(0))
		{
			List<AIdentifierPattern> ids = PPatternAssistantInterpreter.findIdentifiers(p);
			if (!ids.isEmpty())
			{
				names.add(ids.get(0).toString());
			}

			if (ids.size() > 1)
			{
				System.err.println("Some argument is in multiple parts: " + ids);
			}
		}

		EList<Type> types = new BasicEList<Type>();
		for (PDefinition d : def.getParamDefinitionList())
		{
			if (d.getName().name.equals("self"))
			{
				continue;
			}
			PType type = PDefinitionAssistantTC.getType(d);
			utc.create(class_, type);
			types.add(utc.getUmlType(type));
		}

		PType returnType = ((AFunctionType) PDefinitionAssistantTC.getType(def)).getResult();
		utc.create(class_, returnType);
		Type returnUmlType = utc.getUmlType(returnType);

		Operation operation = class_.createOwnedOperation(def.getName().name, names, types, returnUmlType);
		operation.setVisibility(Vdm2UmlUtil.convertAccessSpecifierToVisibility(def.getAccess()));

		operation.setIsStatic(PAccessSpecifierAssistantTC.isStatic(def.getAccess()));
		operation.setIsQuery(true);

	}

	private void addExplicitOperationToClass(Class class_,
			AExplicitOperationDefinition def)
	{
		EList<String> names = new BasicEList<String>();
		for (PPattern p : def.getParameterPatterns())
		{
			List<AIdentifierPattern> ids = PPatternAssistantInterpreter.findIdentifiers(p);
			if (!ids.isEmpty())
			{
				names.add(ids.get(0).toString());
			}

			if (ids.size() > 1)
			{
				System.err.println("Some argument is in multiple parts: " + ids);
			}
		}

		EList<Type> types = new BasicEList<Type>();
		for (PDefinition d : def.getParamDefinitions())
		{
			if (d.getName().name.equals("self"))
			{
				continue;
			}
			PType type = PDefinitionAssistantTC.getType(d);
			utc.create(class_, type);
			types.add(utc.getUmlType(type));
		}

		PType returnType = ((AOperationType) PDefinitionAssistantTC.getType(def)).getResult();
		utc.create(class_, returnType);
		Type returnUmlType = utc.getUmlType(returnType);

		Operation operation = class_.createOwnedOperation(def.getName().name, names, types, returnUmlType);
		operation.setVisibility(Vdm2UmlUtil.convertAccessSpecifierToVisibility(def.getAccess()));

		operation.setIsStatic(PAccessSpecifierAssistantTC.isStatic(def.getAccess()));
		operation.setIsQuery(false);
	}

	private void addInstanceVariableToClass(Class class_,
			AInstanceVariableDefinition def)
	{

		String name = def.getName().name;
		PType defType = PDefinitionAssistantTC.getType(def);

		utc.create(class_, defType);
		Type type = utc.getUmlType(defType);

		if (defType instanceof AClassType)
		{
			// TODO static
			Class referencedClass = getClassName(defType);
			Association association = class_.createAssociation(false, AggregationKind.NONE_LITERAL, name, Vdm2UmlUtil.extractLower(defType), Vdm2UmlUtil.extractUpper(defType), referencedClass, false, AggregationKind.NONE_LITERAL, "", 1, 1);
			association.setVisibility(Vdm2UmlUtil.convertAccessSpecifierToVisibility(def.getAccess()));
		} else
		{
			Property attribute = class_.createOwnedAttribute(name, type);
			attribute.setIsStatic(PAccessSpecifierAssistantTC.isStatic(def.getAccess()));
			attribute.setVisibility(Vdm2UmlUtil.convertAccessSpecifierToVisibility(def.getAccess()));
			if (def.getExpression() != null)
			{
				attribute.setDefault(def.getExpression().toString());
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

	// private Type convertTypeInvariant(SInvariantType definitionType)
	// {
	// Type result = null;
	//
	// switch (definitionType.kindSInvariantType())
	// {
	// case NAMED:
	// String name = ((ANamedInvariantType) definitionType).getName().name;
	// result = types.get(name);
	// break;
	// case RECORD:
	// break;
	//
	// }
	// System.out.println();
	//
	// return result;
	// }

}
