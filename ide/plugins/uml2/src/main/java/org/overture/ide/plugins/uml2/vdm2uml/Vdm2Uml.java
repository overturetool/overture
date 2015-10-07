/*
 * #%~
 * UML2 Translator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
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
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptorQuestion;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ide.plugins.uml2.UmlConsole;
import org.overture.interpreter.assistant.pattern.PPatternAssistantInterpreter;

public class Vdm2Uml
{
	private static class ClassesMap extends HashMap<String, Class>
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();

			for (java.util.Map.Entry<String, Class> entry : entrySet())
			{
				String prefix = entry.getKey();
				while(prefix.length()<20)
				{
					prefix+=" ";
				}
				final Class value = entry.getValue();
				sb.append(prefix+" -> "+ formatClass(value)+"\n");
				for (Classifier nestedValue : value.getNestedClassifiers())
				{
					prefix="";
					while(prefix.length()<20)
					{
						prefix+=" ";
					}
					sb.append(prefix+ "   -> "+formatClass(nestedValue)+"\n");
				}
			}
			
			return sb.toString();
		}
		
		private static String formatClass(Classifier nestedValue)
		{
			 String name = nestedValue.getName();
				while(name.length()<20)
				{
					name+=" ";
				}
				
			return 	name+"\t"+(nestedValue+"").substring(nestedValue.toString().indexOf('('));
				
				
		}
	}

	private UmlConsole console = new UmlConsole();
	UmlTypeCreator utc = new UmlTypeCreator(new UmlTypeCreator.ClassTypeLookup()
	{
		public Class lookup(AClassType type)
		{
			return lookup(type.getName().getName());
		}

		@Override
		public Class lookup(String className)
		{
			return classes.get(className);
		}
	}, console);
	private Model modelWorkingCopy = null;
	private Map<String, Class> classes = new ClassesMap();
	private boolean extendedAssociationMapping = false;
	private boolean deployArtifactsOutsideNodes = false;

	public Vdm2Uml(boolean preferAssociations,
			boolean deployArtifactsOutsideNodes)
	{
		extendedAssociationMapping = preferAssociations;
		this.deployArtifactsOutsideNodes = deployArtifactsOutsideNodes;
	}

	public Model getModel()
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
		console.out.println("# Properties:");
		console.out.println("# \tPrefer associations: "
				+ (extendedAssociationMapping ? "yes" : "no"));
		console.out.println("# \tDisable nested artifacts in deployment diagrams: "
				+ (deployArtifactsOutsideNodes ? "yes" : "no"));
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

		new UmlDeploymentCreator(modelWorkingCopy, console, deployArtifactsOutsideNodes, utc).buildDeployment(classes);

		return modelWorkingCopy;
	}

	public void save(URI uri) throws IOException
	{
		console.out.println("Saving UML model to: " + uri.toFileString()+".uml");
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
			String className = sClass.getName().getName();
			Class class_ = buildClass(sClass);
			this.classes.put(className, class_);
		}

		// build inheritance relationship
		for (SClassDefinition sClass : classes)
		{
			Class thisClass = this.classes.get(sClass.getName().getName());
			for (ILexNameToken superToken : sClass.getSupernames())
			{
				console.out.println("Adding generalization between: "
						+ thisClass.getName() + " -> "
						+ superToken.getFullName());
				Class superClass = this.classes.get(superToken.getName());
				thisClass.createGeneralization(superClass);
			}
		}

		console.out.println("Converting types");
		// Create types embedded in VDM classes
		for (SClassDefinition sClass : classes)
		{
			String className = sClass.getName().getName();
			Class class_ = this.classes.get(className);
			addTypes(class_, sClass);
		}

		console.out.println("Converting remaining class definitions");
		// Build operations, functions, instance variables and values
		for (SClassDefinition sClass : classes)
		{
			String className = sClass.getName().getName();
			Class class_ = this.classes.get(className);
			addAttributesToClass(class_, sClass);
		}

	}

	private void addTypes(Class class_, SClassDefinition sClass)
	{
		console.out.println("Converting types for class: "
				+ sClass.getName().getName());
		for (PDefinition def : sClass.getDefinitions())
		{
			if (def instanceof ATypeDefinition)
			{
				PType type = Vdm2UmlUtil.assistantFactory.createPDefinitionAssistant().getType(def);
				console.out.println("\tConverting type: " + type);
				utc.create(class_, type);
			} else
			{
			}
		}

	}

	private void addAttributesToClass(Class class_, SClassDefinition sClass)
	{
		console.out.println("Converting definitions for class: "
				+ sClass.getName().getName());
		for (PDefinition def : sClass.getDefinitions())
		{
			if (def instanceof AInstanceVariableDefinition)
			{
				addInstanceVariableToClass(class_, (AInstanceVariableDefinition) def);
			} else if (def instanceof AExplicitOperationDefinition)
			{
				addExplicitOperationToClass(class_, (AExplicitOperationDefinition) def);
			} else if (def instanceof AExplicitFunctionDefinition)
			{
				addExplicitFunctionToClass(class_, (AExplicitFunctionDefinition) def);
			} else if (def instanceof AValueDefinition)
			{
				addValueToClass(class_, (AValueDefinition) def);
			} else
			{
			}
		}

	}

	private void addValueToClass(Class class_, AValueDefinition def)
	{
		String name = getDefName(def);
		PType defType = Vdm2UmlUtil.assistantFactory.createPDefinitionAssistant().getType(def);
		utc.create(class_, defType);
		Type umlType = utc.getUmlType(defType);

		if (Vdm2UmlUtil.assistantFactory.createPTypeAssistant().isClass(defType, null)
				&& !(defType instanceof AUnknownType)
				&& !extendedAssociationMapping
				|| Vdm2UmlAssociationUtil.validType(defType)
				&& extendedAssociationMapping)
		{
			console.out.println("\tAdding association for value: " + name);

			Vdm2UmlAssociationUtil.createAssociation(name, defType, def.getAccess(), def.getExpression(), classes, class_, true, utc);
		} else
		{
			console.out.println("\tAdding property for value: " + name);
			Property s = class_.createOwnedAttribute(name, umlType);
			s.setIsStatic(Vdm2UmlUtil.assistantFactory.createPAccessSpecifierAssistant().isStatic(def.getAccess()));
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
		if (def instanceof AValueDefinition)
		{
			AValueDefinition valueDef = (AValueDefinition) def;
			PPattern expression = valueDef.getPattern();
			if (expression instanceof AIdentifierPattern)
			{
				return ((AIdentifierPattern) expression).getName().getName();
			}
		} else
		{
			return def.getName().getName();
		}
		return "null";
	}

	private static class TemplateParameterTypeCreator
			extends
			DepthFirstAnalysisAdaptorQuestion<TemplateParameterTypeCreator.OperationContext>
	{
		protected static class OperationContext
		{
			Class class_;
			Operation operation;

			public OperationContext(Class class_, Operation operation)
			{
				this.class_ = class_;
				this.operation = operation;
			}

		}

		TemplateSignature sig = null;
		Map<String, Classifier> templateParameters = new HashMap<String, Classifier>();

		@Override
		public void caseAParameterType(AParameterType t,
				OperationContext question) throws AnalysisException
		{
			if (sig == null)
			{
				sig = question.operation.createOwnedTemplateSignature(UMLPackage.Literals.TEMPLATE_SIGNATURE);
			}

			/**
			 * Modelio doesnt support Classifier template parameters so the lines: <br/>
			 * TemplateParameter tp = sig.createOwnedParameter(UMLPackage.Literals.CLASSIFIER_TEMPLATE_PARAMETER);<br/>
			 * Class sss = (Class) tp.createOwnedParameteredElement(UMLPackage.Literals.CLASS);<br/>
			 * have been replaced with an alternative solution that it can import.<br/>
			 * The lines:<br/>
			 * LiteralString literalStringDefault =(LiteralString)
			 * tp.createOwnedDefault(UMLPackage.Literals.LITERAL_STRING);
			 * literalStringDefault.setName(UmlTypeCreatorBase.getName(t));<br/>
			 * are also not needed but makes it look better in ModelioThe proper solution is described here:
			 * http://www.eclipse.org/modeling/mdt/uml2/docs/ articles/Defining_Generics_with_UML_Templates/article.html
			 */

			String pName = UmlTypeCreatorBase.getName(t);

			if (!templateParameters.containsKey(pName))
			{
				TemplateParameter tp = sig.createOwnedParameter(UMLPackage.Literals.TEMPLATE_PARAMETER);

				LiteralString literalStringDefault = (LiteralString) tp.createOwnedDefault(UMLPackage.Literals.LITERAL_STRING);
				literalStringDefault.setName(UmlTypeCreatorBase.getName(t));

				Class sss = null;
				Object c = question.class_.getNestedClassifier(pName);
				if (c instanceof Class)
				{
					sss = (Class) c;
				} else
				{

					sss = (Class) question.class_.createNestedClassifier(pName, UMLPackage.Literals.CLASS);
				}

				sss.setName(pName);
				templateParameters.put(pName, sss);
			}
			// else sorry we only support unique template parameter names
		}

		@Override
		public void caseAClassClassDefinition(AClassClassDefinition node,
				OperationContext question) throws AnalysisException
		{
			// stop visiting childreb
		}

		public Map<String, Classifier> apply(List<PType> nodes,
				OperationContext question) throws AnalysisException
		{
			for (PType pType : nodes)
			{
				pType.apply(this, question);
			}
			return templateParameters;
		}

		public Map<String, Classifier> apply(PType node,
				OperationContext question) throws AnalysisException
		{
			Vector<PType> nodes = new Vector<PType>();
			nodes.add(node);
			return apply(nodes, question);
		}
	}

	private void addExplicitFunctionToClass(Class class_,
			AExplicitFunctionDefinition def)
	{
		console.out.println("\tAdding function: " + def.getName().getName());
		EList<String> names = new BasicEList<String>();
		for (PPattern p : def.getParamPatternList().get(0))
		{
			// HERE SEE: Downcast the assistantFactory here. Narrowing it to interpreter assistant.
			List<AIdentifierPattern> ids = Vdm2UmlUtil.assistantFactory.createPPatternAssistant().findIdentifiers(p);
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

		Operation operation = class_.createOwnedOperation(def.getName().getName(), null, null, null);

		// Map<String, Classifier> templateParameters = new HashMap<String, Classifier>();
		// TemplateSignature sig = null;
		// for (PType t : type.getParameters())
		// {
		// if (t instanceof AParameterType)
		// {
		// if (sig == null)
		// {
		// sig = operation.createOwnedTemplateSignature(UMLPackage.Literals.TEMPLATE_SIGNATURE);
		// }
		//
		// /*
		// * Modelio doesnt support Classifier template parameters so the lines:
		// * <br/>TemplateParameter tp =
		// * sig.createOwnedParameter(UMLPackage.Literals.CLASSIFIER_TEMPLATE_PARAMETER);<br/>Class sss = (Class)
		// * tp.createOwnedParameteredElement(UMLPackage.Literals.CLASS);<br/>have been replaced with an alternative
		// * solution that it can import.<br/>The lines:<br/>LiteralString literalStringDefault =(LiteralString)
		// * tp.createOwnedDefault(UMLPackage.Literals.LITERAL_STRING);
		// * literalStringDefault.setName(UmlTypeCreatorBase.getName(t));<br/>are also not needed but makes it look
		// * better in ModelioThe proper solution is described here:
		// * http://www.eclipse.org/modeling/mdt/uml2/docs/
		// * articles/Defining_Generics_with_UML_Templates/article.html
		// */
		// TemplateParameter tp = sig.createOwnedParameter(UMLPackage.Literals.TEMPLATE_PARAMETER);
		// String pName = UmlTypeCreatorBase.getName(t);
		//
		// LiteralString literalStringDefault = (LiteralString)
		// tp.createOwnedDefault(UMLPackage.Literals.LITERAL_STRING);
		// literalStringDefault.setName(UmlTypeCreatorBase.getName(t));
		//
		// Class sss = (Class) class_.createNestedClassifier(pName, UMLPackage.Literals.CLASS);
		// sss.setName(pName);
		// templateParameters.put(pName, sss);
		// }
		// }

		Map<String, Classifier> templateParameters = null;
		try
		{
			TemplateParameterTypeCreator tpCreator = new TemplateParameterTypeCreator();
			TemplateParameterTypeCreator.OperationContext oCtxt = new TemplateParameterTypeCreator.OperationContext(class_, operation);
			templateParameters = tpCreator.apply(type.getParameters(), oCtxt);
			templateParameters = tpCreator.apply(type.getResult(), oCtxt);
		} catch (AnalysisException e)
		{
			console.err.println("An error occured during template parameter creation in: "
					+ operation.getName());
			e.printStackTrace(console.err);
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

		operation.setIsStatic(Vdm2UmlUtil.assistantFactory.createPAccessSpecifierAssistant().isStatic(def.getAccess()));
		operation.setIsQuery(true);
	}

	private void addExplicitOperationToClass(Class class_,
			AExplicitOperationDefinition def)
	{
		console.out.println("\tAdding operation: " + def.getName().getName());
		EList<String> names = new BasicEList<String>();
		EList<Type> types = new BasicEList<Type>();

		for (PPattern p : def.getParameterPatterns())
		{
			// Downcast the assistantFactory here. Narrowing it to interpreter assistant.
			List<AIdentifierPattern> ids = ((PPatternAssistantInterpreter) Vdm2UmlUtil.assistantFactory.createPPatternAssistant()).findIdentifiers(p);
			if (!ids.isEmpty())
			{
				String name = ids.get(0).toString();
				names.add(name);

				// now find the type
				for (PDefinition d : def.getParamDefinitions())
				{
					if (d.getName().getName().equals("self")
							|| !d.getName().getName().equals(name))
					{
						continue;
					}
					PType type = Vdm2UmlUtil.assistantFactory.createPDefinitionAssistant().getType(d);
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

		PType returnType = ((AOperationType) Vdm2UmlUtil.assistantFactory.createPDefinitionAssistant().getType(def)).getResult();
		utc.create(class_, returnType);
		Type returnUmlType = utc.getUmlType(returnType);

		Operation operation = class_.createOwnedOperation(def.getName().getName(), names, types, returnUmlType);
		operation.setVisibility(Vdm2UmlUtil.convertAccessSpecifierToVisibility(def.getAccess()));

		operation.setIsStatic(Vdm2UmlUtil.assistantFactory.createPAccessSpecifierAssistant().isStatic(def.getAccess()));
		operation.setIsQuery(false);
	}

	private void addInstanceVariableToClass(Class class_,
			AInstanceVariableDefinition def)
	{

		String name = def.getName().getName();
		PType defType = Vdm2UmlUtil.assistantFactory.createPDefinitionAssistant().getType(def);

		utc.create(class_, defType);
		Type type = utc.getUmlType(defType);

		if (Vdm2UmlUtil.assistantFactory.createPTypeAssistant().isClass(defType, null)
				&& !(defType instanceof AUnknownType)
				&& !extendedAssociationMapping
				|| Vdm2UmlAssociationUtil.validType(defType)
				&& extendedAssociationMapping)
		{
			console.out.println("\tAdding association for instance variable: "
					+ def.getName().getName());

			Vdm2UmlAssociationUtil.createAssociation(name, defType, def.getAccess(), def.getExpression(), classes, class_, false, utc);

		} else
		{
			console.out.println("\tAdding property for instance variable: "
					+ def.getName().getName());
			Property attribute = class_.createOwnedAttribute(name, type);
			attribute.setIsStatic(Vdm2UmlUtil.assistantFactory.createPAccessSpecifierAssistant().isStatic(def.getAccess()));
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
		String name = sClass.getName().getName();
		boolean isAbstract = Vdm2UmlUtil.hasSubclassResponsabilityDefinition(sClass.getDefinitions());
		Class class_ = modelWorkingCopy.createOwnedClass(name, isAbstract);

		boolean isActive = Vdm2UmlUtil.isClassActive(sClass);
		class_.setIsActive(isActive);

		class_.setVisibility(VisibilityKind.PUBLIC_LITERAL);

		return class_;
	}

}
