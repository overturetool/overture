package org.overture.ide.plugins.uml2.vdm2uml;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.ui.PartInitException;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.LiteralString;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.TemplateParameter;
import org.eclipse.uml2.uml.TemplateSignature;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
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
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.PType;
import org.overture.ide.plugins.uml2.UmlConsole;
import org.overture.interpreter.assistant.pattern.PPatternAssistantInterpreter;
import org.overture.interpreter.assistant.type.PTypeAssistantInterpreter;
import org.overture.typechecker.assistant.definition.PAccessSpecifierAssistantTC;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;

public class Vdm2Uml
{
	private UmlConsole console = new UmlConsole();
	UmlTypeCreator utc = new UmlTypeCreator(new UmlTypeCreator.ClassTypeLookup()
	{
		public Class lookup(AClassType type)
		{
			return lookup(type.getName().name);
		}

		@Override
		public Class lookup(String className)
		{
			return classes.get(className);
		}
	}, console);
	private Model modelWorkingCopy = null;
	private Map<String, Class> classes = new HashMap<String, Class>();
	private boolean extendedAssociationMapping = false;
	private boolean deployArtifactsOutsideNodes = false;

	public Vdm2Uml(boolean preferAssociations,boolean deployArtifactsOutsideNodes)
	{
		extendedAssociationMapping = preferAssociations;
		this.deployArtifactsOutsideNodes = deployArtifactsOutsideNodes;
	}
	
	public  Model getModel()
	{
		return this.modelWorkingCopy;
	}

	public Model convert(String name, List<SClassDefinition> classes)
	{
		try
		{
			console.show();
		} catch (PartInitException e)
		{
		}
		console.out.println("#\n# Starting translation of project: " + name
				+ "\n#");
		// console.out.println("# Into: "+outputDir+"\n#");
		console.out.println("-------------------------------------------------------------------------");

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

		new UmlDeploymentCreator(modelWorkingCopy, console,deployArtifactsOutsideNodes).buildDeployment(classes);

		return modelWorkingCopy;
	}

	public void save(URI uri) throws IOException
	{
		console.out.println("Saving UML model to: " + uri);
		Resource resource = new ResourceSetImpl().createResource(uri.appendFileExtension(UMLResource.FILE_EXTENSION));
		resource.getContents().add(modelWorkingCopy);

		resource.save(null);

		// TODO: fix for modelio not supporting version 4.0.0 of EMF UML
		try
		{
			FileReader fr = new FileReader(uri.toFileString() + ".uml");
			BufferedReader br = new BufferedReader(fr);

			String line = null;
			List<String> buffer = new Vector<String>();
			while ((line = br.readLine()) != null)
			{
				line = line.replace("\"http://www.eclipse.org/uml2/4.0.0/UML\"", "\"http://www.eclipse.org/uml2/3.0.0/UML\"");
				buffer.add(line);
			}
			br.close();

			FileWriter fw = new FileWriter(uri.toFileString() + ".uml");
			PrintWriter out = new PrintWriter(fw);
			for (Iterator<String> iterator = buffer.iterator(); iterator.hasNext();)
			{
				out.write(iterator.next());
				if (iterator.hasNext())
				{
					out.println();
				}
			}
			out.close();

		} catch (IOException e)
		{
		}

	}

	private void buildUml(List<SClassDefinition> classes)
	{

		// Build class container
		for (SClassDefinition sClass : classes)
		{
			console.out.println("Converting class: " + sClass.getName());
			String className = sClass.getName().name;
			Class class_ = buildClass(sClass);
			this.classes.put(className, class_);
		}

		// build inheritance relationship
		for (SClassDefinition sClass : classes)
		{
			Class thisClass = this.classes.get(sClass.getName().name);
			for (LexNameToken superToken : sClass.getSupernames())
			{
				console.out.println("Adding generalization between: "
						+ thisClass.getName() + " -> " + superToken.getName());
				Class superClass = this.classes.get(superToken.name);
				thisClass.createGeneralization(superClass);
			}
		}

		console.out.println("Converting types");
		// Create types embedded in VDM classes
		for (SClassDefinition sClass : classes)
		{
			String className = sClass.getName().name;
			Class class_ = this.classes.get(className);
			addTypes(class_, sClass);
		}

		console.out.println("Converting remaining class definitions");
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
		console.out.println("Converting types for class: "
				+ sClass.getName().name);
		for (PDefinition def : sClass.getDefinitions())
		{

			switch (def.kindPDefinition())
			{
				case TYPE:
				{
					PType type = PDefinitionAssistantTC.getType(def);
					console.out.println("\tConverting type: " + type);
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
		console.out.println("Converting definitions for class: "
				+ sClass.getName().name);
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

		if ((PTypeAssistantInterpreter.isClass(defType) && !extendedAssociationMapping)
				|| (Vdm2UmlAssociationUtil.validType(defType) && extendedAssociationMapping))
		{
			console.out.println("\tAdding association for value: " + name);

			Vdm2UmlAssociationUtil.createAssociation(name, defType, def.getAccess(), def.getExpression(), classes, class_, true,utc);
		} else
		{
			console.out.println("\tAdding property for value: " + name);
			Property s = class_.createOwnedAttribute(name, umlType);
			s.setIsStatic(PAccessSpecifierAssistantTC.isStatic(def.getAccess()));
			s.setVisibility(Vdm2UmlUtil.convertAccessSpecifierToVisibility(def.getAccess()));
			s.setIsReadOnly(true);

			if (Vdm2UmlUtil.isOptional(defType))
			{
				s.setLower(0);
			}

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
		console.out.println("\tAdding function: " + def.getName().name);
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
				console.err.println("Some argument is in multiple parts: "
						+ ids);
			}
		}

		EList<Type> types = new BasicEList<Type>();

		AFunctionType type = (AFunctionType) def.getType();

		Operation operation = class_.createOwnedOperation(def.getName().name, null, null, null);

		Map<String, Classifier> templateParameters = new HashMap<String, Classifier>();
		TemplateSignature sig = null;
		for (PType t : type.getParameters())
		{
			if (t instanceof AParameterType)
			{
				if (sig == null)
				{
					sig = operation.createOwnedTemplateSignature(UMLPackage.Literals.TEMPLATE_SIGNATURE);
				}

				/*
				 * Modelio doesnt support Classifier template parameters so the lines:
				 * <br/>TemplateParameter tp =
				 * sig.createOwnedParameter(UMLPackage.Literals.CLASSIFIER_TEMPLATE_PARAMETER);<br/>Class sss = (Class)
				 * tp.createOwnedParameteredElement(UMLPackage.Literals.CLASS);<br/>have been replaced with an alternative
				 * solution that it can import.<br/>The lines:<br/>LiteralString literalStringDefault =(LiteralString)
				 * tp.createOwnedDefault(UMLPackage.Literals.LITERAL_STRING);
				 * literalStringDefault.setName(UmlTypeCreatorBase.getName(t));<br/>are also not needed but makes it look
				 * better in ModelioThe proper solution is described here:
				 * http://www.eclipse.org/modeling/mdt/uml2/docs/
				 * articles/Defining_Generics_with_UML_Templates/article.html
				 */
				TemplateParameter tp = sig.createOwnedParameter(UMLPackage.Literals.TEMPLATE_PARAMETER);
				String pName = UmlTypeCreatorBase.getName(t);

				LiteralString literalStringDefault = (LiteralString) tp.createOwnedDefault(UMLPackage.Literals.LITERAL_STRING);
				literalStringDefault.setName(UmlTypeCreatorBase.getName(t));

				Class sss = (Class) class_.createNestedClassifier(pName, UMLPackage.Literals.CLASS);
				sss.setName(pName);
				templateParameters.put(pName, sss);
			}
		}

		utc.addTemplateTypes(templateParameters);
		for (PType t : type.getParameters())
		{
			utc.create(class_, t);
			types.add(utc.getUmlType(t));
		}

		utc.create(class_, type.getResult());
		Type returnUmlType = utc.getUmlType(type.getResult());
		utc.removeTemplateTypees();

		operation.setType(returnUmlType);

		for (int i = 0; i < names.size(); i++)
		{
			operation.createOwnedParameter(names.get(i), types.get(i));
		}

		// Operation operation = class_.createOwnedOperation(def.getName().name, names, types, returnUmlType);
		operation.setVisibility(Vdm2UmlUtil.convertAccessSpecifierToVisibility(def.getAccess()));

		operation.setIsStatic(PAccessSpecifierAssistantTC.isStatic(def.getAccess()));
		operation.setIsQuery(true);
	}

	private void addExplicitOperationToClass(Class class_,
			AExplicitOperationDefinition def)
	{
		console.out.println("\tAdding operation: " + def.getName().name);
		EList<String> names = new BasicEList<String>();
		EList<Type> types = new BasicEList<Type>();

		for (PPattern p : def.getParameterPatterns())
		{
			List<AIdentifierPattern> ids = PPatternAssistantInterpreter.findIdentifiers(p);
			if (!ids.isEmpty())
			{
				String name = ids.get(0).toString();
				names.add(name);

				// now find the type
				for (PDefinition d : def.getParamDefinitions())
				{
					if (d.getName().name.equals("self")
							|| !d.getName().name.equals(name))
					{
						continue;
					}
					PType type = PDefinitionAssistantTC.getType(d);
					utc.create(class_, type);
					types.add(utc.getUmlType(type));
				}

				if (names.size() != types.size())
				{
					console.err.println("Missing type for argument \"" + name
							+ "\" in " + def.getName());
				}
			}

			if (ids.size() > 1)
			{
				console.err.println("Some argument is in multiple parts: "
						+ ids);
			}
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

		if ((PTypeAssistantInterpreter.isClass(defType) && !extendedAssociationMapping)
				|| (Vdm2UmlAssociationUtil.validType(defType) && extendedAssociationMapping))
		{
			console.out.println("\tAdding association for instance variable: "
					+ def.getName().name);

			Vdm2UmlAssociationUtil.createAssociation(name, defType, def.getAccess(), def.getExpression(), classes, class_, false,utc);

		} else
		{
			console.out.println("\tAdding property for instance variable: "
					+ def.getName().name);
			Property attribute = class_.createOwnedAttribute(name, type);
			attribute.setIsStatic(PAccessSpecifierAssistantTC.isStatic(def.getAccess()));
			attribute.setVisibility(Vdm2UmlUtil.convertAccessSpecifierToVisibility(def.getAccess()));

			if (Vdm2UmlUtil.isOptional(defType))
			{
				attribute.setLower(0);
			}

			if (def.getExpression() != null)
			{
				attribute.setDefault(def.getExpression().toString());
			}
		}

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

}
