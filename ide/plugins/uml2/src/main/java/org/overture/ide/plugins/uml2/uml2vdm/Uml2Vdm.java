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
package org.overture.ide.plugins.uml2.uml2vdm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.ui.PartInitException;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Stereotype;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.PType;
import org.overture.config.Settings;
import org.overture.ide.plugins.uml2.Activator;
import org.overture.ide.plugins.uml2.UmlConsole;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;
import org.overture.parser.util.ParserUtil;
import org.overture.parser.util.ParserUtil.ParserResult;
import org.overture.prettyprinter.PrettyPrinterEnv;
import org.overture.prettyprinter.PrettyPrinterVisitor;

public class Uml2Vdm
{
	final static LexLocation location = new LexLocation(new File("generated"), "generating", 0, 0, 0, 0, 0, 0);
	Model model;
	private String extension = "vdmpp";
	private VdmTypeCreator tc = null;
	private UmlConsole console;
	private PExp NEW_A_UNDEFINED_EXP = AstFactory.newAUndefinedExp(location);

	public Uml2Vdm()
	{
		console = new UmlConsole();
		tc = new VdmTypeCreator(console);
	}

	public boolean initialize(URI uri, String extension)
	{
		if (extension != null)
		{
			this.extension = extension;
		}
		Resource resource = new ResourceSetImpl().getResource(uri, true);
		for (EObject c : resource.getContents())
		{
			if (c instanceof Model)
			{
				model = (Model) c;
			}
		}

		return model != null;
	}

	public void convert(File outputDir)
	{
		if (model != null)
		{
			try
			{
				console.show();
			} catch (PartInitException e2)
			{
			}
			console.out.println("#\n# Starting translation of model: "
					+ model.getName() + "\n#");
			console.out.println("# Into: " + outputDir + "\n#");
			console.out.println("-------------------------------------------------------------------------");
			Map<String, AClassClassDefinition> classes = new HashMap<String, AClassClassDefinition>();
			for (Element e : model.getOwnedElements())
			{
				if (e instanceof Class)
				{
					Class class_ = (Class) e;
					console.out.println("Converting: " + class_.getName());
					classes.put(class_.getName(), createClass(class_));
				}
			}

			console.out.println("Writing source files");
			for (Entry<String, AClassClassDefinition> c : classes.entrySet())
			{
				writeClassFile(outputDir, c);
			}
			console.out.println("Conversion completed.");
		}
	}

	private void writeClassFile(File outputDir,
			Entry<String, AClassClassDefinition> c)
	{
		try
		{
			outputDir.mkdirs();
			FileWriter outFile = new FileWriter(new File(outputDir, c.getKey()
					+ "." + extension));
			PrintWriter out = new PrintWriter(outFile);

			out.println(c.getValue().apply(new PrettyPrinterVisitor(), new PrettyPrinterEnv()));
			out.close();
		} catch (IOException e)
		{
			Activator.log("Error in writeclassfile", e);
		} catch (AnalysisException e)
		{
			Activator.log("Error in writeclassfile", e);
		}
	}

	private AClassClassDefinition createClass(Class class_)
	{
		AClassClassDefinition c = AstFactory.newAClassClassDefinition();
		c.setName(new LexNameToken(class_.getName(), class_.getName(), null));

		elementLoop: for (Element elem : class_.getOwnedElements())
		{
			if (elem instanceof Class)
			{
				Class innerType = (Class) elem;
				String innerTypeName = innerType.getName();
				AAccessSpecifierAccessSpecifier access = Uml2VdmUtil.createAccessSpecifier(innerType.getVisibility());

				if (innerType.getNestedClassifier("record") != null
						&& innerType.getGeneralizations().isEmpty())
				{
					boolean createdType = false;
					for (EObject innerElem : innerType.getOwnedElements())
					{
						if (innerElem instanceof Stereotype)
						{
							Stereotype steriotype = (Stereotype) innerElem;
							if (steriotype.getName().equals("record"))
							{
								console.out.println("\tConverting inner record type= "
										+ innerTypeName);
								ATypeDefinition innerTypeDef = AstFactory.newATypeDefinition(new LexNameToken(class_.getName(), innerTypeName, location), null, null, null);
								innerTypeDef.setType(tc.createRecord(innerType));
								innerTypeDef.setAccess(access);
								c.getDefinitions().add(innerTypeDef);
								createdType = true;
								break;
							}
						}
					}

					if (!createdType)
					{
						console.err.println("\tFound type= " + innerTypeName
								+ " : " + "unknown type");
					}
				} else if (innerTypeName.equalsIgnoreCase("string")// FIX Modelio 2.2.1
						|| innerType.getGeneralizations().isEmpty())
				{

					for (PDefinition def : c.getDefinitions())
					{
						if (def.getName().getName().equals(innerTypeName))
						{
							continue elementLoop;
						}
					}
					String innerTypeTypeName = "Seq<Char>";
					console.out.println("\tConverting inner type= "
							+ innerTypeName
							+ " : "
							+ innerTypeTypeName
							+ " Warning the actual type of \""
							+ innerTypeName
							+ "\" is undecidable - inserting \"seq of char\" instead");
					ATypeDefinition innerTypeDef = AstFactory.newATypeDefinition(new LexNameToken(class_.getName(), innerTypeName, location), null, null, null);
					innerTypeDef.setType(AstFactory.newASeqSeqType(location, AstFactory.newACharBasicType(location)));
					innerTypeDef.setAccess(access);
					c.getDefinitions().add(innerTypeDef);

				} else
				{
					String innerTypeTypeName = innerType.getGeneralizations().get(0).getGeneral().getName();
					console.out.println("\tConverting inner type= "
							+ innerTypeName + " : " + innerTypeTypeName);
					ATypeDefinition innerTypeDef = AstFactory.newATypeDefinition(new LexNameToken(class_.getName(), innerTypeName, location), null, null, null);
					innerTypeDef.setType(tc.convert(innerTypeTypeName, null));
					innerTypeDef.setAccess(access);
					c.getDefinitions().add(innerTypeDef);
				}
			} else if (elem instanceof Enumeration)
			{
				String innerTypeName = ((Enumeration) elem).getName();
				console.out.println("\tConverting inner enumeration type= "
						+ innerTypeName);
				ATypeDefinition innerTypeDef = AstFactory.newATypeDefinition(new LexNameToken(class_.getName(), innerTypeName, location), null, null, null);
				innerTypeDef.setType(tc.createEnumeration((Enumeration) elem));
				AAccessSpecifierAccessSpecifier access = Uml2VdmUtil.createAccessSpecifier(((Enumeration) elem).getVisibility());
				innerTypeDef.setAccess(access);
				c.getDefinitions().add(innerTypeDef);
			}
		}

		for (Property att : class_.getOwnedAttributes())
		{
			if (att.isReadOnly())
			{
				createValue(c, att);
			} else
			{
				createInstanceVar(c, att);
			}
		}

		for (Operation op : class_.getOwnedOperations())
		{
			if (op.isQuery())
			{
				createFunction(c, op);
			} else
			{
				createOperation(c, op);
			}
		}

		return c;
	}

	private void createFunction(AClassClassDefinition c, Operation op)
	{
		console.out.println("\tConverting function: " + op.getName());
		LexNameToken name = new LexNameToken(c.getName().getName(), op.getName(), null);

		List<List<PPattern>> paramPatternList = new Vector<List<PPattern>>();
		List<PPattern> paramPatterns = new Vector<PPattern>();
		paramPatternList.add(paramPatterns);
		List<PType> parameterTypes = new Vector<PType>();

		for (Parameter p : op.getOwnedParameters())
		{
			if (p.getName() == null)
			{
				continue;// this is the return type
			}
			parameterTypes.add(tc.convert(p.getType()));
			paramPatterns.add(AstFactory.newAIdentifierPattern(new LexNameToken(c.getName().getName(), p.getName(), location)));
		}

		AFunctionType type = AstFactory.newAFunctionType(null, true, parameterTypes, tc.convert(op.getType()));

		AExplicitFunctionDefinition operation = AstFactory.newAExplicitFunctionDefinition(name, null, null, type, paramPatternList, AstFactory.newAUndefinedExp(null), null, null, false, null);
		operation.setAccess(Uml2VdmUtil.createAccessSpecifier(op.getVisibility(), op.isStatic(), false));
		c.getDefinitions().add(operation);
	}

	private void createOperation(AClassClassDefinition c, Operation op)
	{
		console.out.println("\tConverting operation: " + op.getName());
		LexNameToken name = new LexNameToken(c.getName().getName(), op.getName(), null);
		List<PType> parameterTypes = new Vector<PType>();
		List<PPattern> parameters = new Vector<PPattern>();
		for (Parameter p : op.getOwnedParameters())
		{
			if (p.getName() == null)
			{
				continue;// this is the return type
			}
			parameterTypes.add(tc.convert(p.getType()));
			parameters.add(AstFactory.newAIdentifierPattern(new LexNameToken(c.getName().getName(), p.getName(), location)));
		}
		AOperationType type = AstFactory.newAOperationType(null, parameterTypes, tc.convert(op.getType()));

		AExplicitOperationDefinition operation = AstFactory.newAExplicitOperationDefinition(name, type, parameters, null, null, AstFactory.newANotYetSpecifiedStm(null));
		operation.setAccess(Uml2VdmUtil.createAccessSpecifier(op.getVisibility(), op.isStatic(), false));
		c.getDefinitions().add(operation);
	}

	private void createInstanceVar(AClassClassDefinition c, Property att)
	{
		console.out.println("\tConverting instance variable: " + att.getName());
		PType type = tc.convert(att);
		PExp defaultExp = null;// NEW_A_INT_ZERRO_LITERAL_EXP.clone();
		if (att.getDefault() != null && !att.getDefault().isEmpty())
		{
			Settings.dialect = Dialect.VDM_PP;
			ParserResult<PExp> resExp = null;
			boolean failed = false;
			try
			{
				resExp = ParserUtil.parseExpression(att.getDefault());
			} catch (ParserException e)
			{
				failed = true;
				e.printStackTrace();
			} catch (LexException e)
			{
				failed = true;
				e.printStackTrace();
			}
			if (resExp.errors.isEmpty() && !failed)
			{
				defaultExp = resExp.result;
			} else
			{
				console.err.println("\tFaild to parse expression for attribute: "
						+ att.getName()
						+ " in class "
						+ c.getName().getName()
						+ " default is: " + att.getDefault());
			}
		}
		AInstanceVariableDefinition inst = AstFactory.newAInstanceVariableDefinition(new LexNameToken(c.getName().getName(), att.getName(), location), type, defaultExp);
		c.getDefinitions().add(inst);
	}

	private void createValue(AClassClassDefinition c, Property att)
	{
		console.out.println("\tConverting value: " + att.getName());
		PType type = tc.convert(att);

		PExp defaultExp = NEW_A_UNDEFINED_EXP.clone();
		if (att.getDefault() != null && !att.getDefault().isEmpty())
		{
			Settings.dialect = Dialect.VDM_PP;
			ParserResult<PExp> resExp = null;
			boolean failed = false;
			try
			{
				resExp = ParserUtil.parseExpression(att.getDefault());
			} catch (ParserException e)
			{
				failed = true;
				e.printStackTrace();
			} catch (LexException e)
			{
				failed = true;
				e.printStackTrace();
			}
			if (resExp.errors.isEmpty() && !failed)
			{
				defaultExp = resExp.result;
			} else
			{
				console.err.println("\tFaild to parse expression for attribute: "
						+ att.getName()
						+ " in class "
						+ c.getName().getName()
						+ " default is: " + att.getDefault());
			}
		}

		AValueDefinition inst = AstFactory.newAValueDefinition(AstFactory.newAIdentifierPattern(new LexNameToken(c.getName().getName(), att.getName(), location)), null, type, defaultExp);
		c.getDefinitions().add(inst);
	}
}
