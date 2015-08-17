/*
 * #%~
 * Overture GUI Builder
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
/*******************************************************************************
 * Copyright (c) 2009, 2013 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.guibuilder.internal.ir;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.PType;
import org.overture.ast.util.definitions.ClassList;
import org.overture.guibuilder.internal.ToolSettings;
import org.overture.interpreter.util.ClassListInterpreter;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Vdm Class Reader that uses Vdmj to extract most of the information (the exception is annotation)
 * 
 * @author carlos
 */
public class VdmjVdmClassReader implements IVdmClassReader
{

	private Vector<IVdmDefinition> classList = null;
	ClassListInterpreter classes;

	/**
	 * Constructor
	 */
	public VdmjVdmClassReader()
	{
		classList = new Vector<IVdmDefinition>();
	}

	public VdmjVdmClassReader(ClassListInterpreter classes)
	{
		this();
		this.classes = classes;
	}

	@Override
	public void readFiles(Vector<File> files, ITypeCheckerAssistantFactory af)
	{
		// ClassList classes = new ClassList();
		AnnotationTable annotationTable = new AnnotationTable();
		AnnotationReader annotationReader = new AnnotationReader(annotationTable);

		// for (File file : files ) {
		// LexTokenReader ltReader =
		// new LexTokenReader(file, Settings.dialect, Charset.defaultCharset().name());
		// ClassReader classReader = new ClassReader(ltReader);
		// classes.addAll(classReader.readClasses());
		//
		// }
		// this is needed as the token reader doesn't do the semantic checks
		// necessary to find out the constructors of a class
		// TypeChecker typeChecker = new ClassTypeChecker(classes);
		// typeChecker.typeCheck();
		// we extract the annotations (ideally this should be done by the same parser)
		annotationReader.readFiles(files);
		System.out.println(annotationTable.printTable());
		readVdmjClassList(classes, annotationTable, af);

	}

	@Override
	public Vector<IVdmDefinition> getClassList()
	{
		return classList;
	}

	/**
	 * Reads from the class list provided by vdmj and the from the annotation table building the internal list of
	 * classes with the matching information
	 * 
	 * @param classes
	 * @param annotationTable
	 * @throws InvocationAssistantException
	 */
	private void readVdmjClassList(ClassList classes,
			AnnotationTable annotationTable, ITypeCheckerAssistantFactory af)
	{
		// the name of the classes, this is usefull later on
		Vector<String> classNames = new Vector<String>();
		for (SClassDefinition c : classes)
		{
			classNames.add(c.getName().getName());
		}

		for (SClassDefinition c : classes)
		{
			readVdmjClass(c, annotationTable, classNames, af);
		}

	}

	/**
	 * Reads a individual vdmj ClassDefinition object, cross checking with the annotation table, and translates it into
	 * the intermediate representation
	 * 
	 * @param c
	 *            class definition
	 * @param annotationTable
	 *            annotation table associated with the specification
	 * @param classNames
	 * @throws InvocationAssistantException
	 */
	private void readVdmjClass(SClassDefinition c,
			AnnotationTable annotationTable, Vector<String> classNames,
			ITypeCheckerAssistantFactory assistantFactory)
	{
		boolean hasConstructors = c.getHasContructors();
		VdmClass vdmClass = new VdmClass(c.getName().getName(), hasConstructors);

		// adds annotation if any
		/*
		 * if ( annotationTable.getOpAnnotations(vdmClass.getName()) != null ) { for ( VdmAnnotation annotation :
		 * annotationTable.getOpAnnotations(vdmClass.getName())) { vdmClass.addAnnotation( annotation ); } }
		 */

		for (PDefinition def : c.getDefinitions())
		{
			VdmMethod newDefinition = null;
			if (assistantFactory.createPDefinitionAssistant().isFunctionOrOperation(def))
			{
				// now we check what sub class it is...
				if (def instanceof AExplicitOperationDefinition)
				{
					AExplicitOperationDefinition operation = (AExplicitOperationDefinition) def;
					// In terms of type only 'class types' are treated
					VdmType type = getType(((AOperationType) operation.getType()).getResult(), assistantFactory);
					newDefinition = new VdmMethod(operation.getName().getName(), operation.getIsConstructor(), type);
					// Temporary solution, just to check if there's a return
					/*
					 * if (!operation.type.result.equals("()")) newDefinition.setType( "" );
					 */// fetching the arguments
					for (List<PPattern> li : assistantFactory.createAExplicitOperationDefinitionAssistant().getParamPatternList(operation))
					{
						for (int n = 0; n < li.size(); ++n)
						{
							LexNameList varName = assistantFactory.createPPatternAssistant().getVariableNames(li.get(n));
							// the type
							String typeName = extractTypeName(operation.getType(), n);
							boolean flag = false;
							for (String cn : classNames)
							{
								if (typeName.equals(cn))
								{
									flag = true;
								}
							}
							((VdmMethod) newDefinition).addParam(new VdmParam(varName.toString(), new VdmType(typeName, flag)));
						}
					}
				} else if (def instanceof AImplicitOperationDefinition)
				{
					AImplicitOperationDefinition operation = (AImplicitOperationDefinition) def;
					VdmType type = null;
					//  In terms of type only 'class types' are treated
					type = getType(((AOperationType) operation.getType()).getResult(), assistantFactory);
					newDefinition = new VdmMethod(operation.getName().getName(), operation.getIsConstructor(), type);

					// fetching the arguments
					int n = 0;
					for (PPattern li : assistantFactory.createAImplicitOperationDefinitionAssistant().getParamPatternList(operation))
					{
						LexNameList varName = assistantFactory.createPPatternAssistant().getVariableNames(li);
						// the type
						String typeName = extractTypeName(operation.getType(), n);
						boolean flag = false;
						for (String cn : classNames)
						{
							if (typeName.equals(cn))
							{
								flag = true;
							}
						}
						((VdmMethod) newDefinition).addParam(new VdmParam(varName.toString(), new VdmType(typeName, flag)));
						extractTypeName(operation.getType(), n);
						++n;
					}

				} else if (def instanceof AExplicitFunctionDefinition)
				{
					AExplicitFunctionDefinition function = (AExplicitFunctionDefinition) def;
					VdmType type = null;
					//  In terms of type only 'class types' are treated
					type = getType(((AFunctionType) function.getType()).getResult(), assistantFactory);
					newDefinition = new VdmMethod(function.getName().getName(), false, type);
					// fetching the arguments
					for (List<PPattern> li : function.getParamPatternList())
					{
						for (int n = 0; n < li.size(); ++n)
						{
							LexNameList varName = assistantFactory.createPPatternAssistant().getVariableNames(li.get(n));
							// the type
							String typeName = extractTypeName(function.getType(), n);
							boolean flag = false;
							for (String cn : classNames)
							{
								if (typeName.equals(cn))
								{
									flag = true;
								}
							}
							((VdmMethod) newDefinition).addParam(new VdmParam(varName.toString(), new VdmType(typeName, flag)));
							extractTypeName(function.getType(), n);
						}
					}

				} else if (def instanceof AImplicitFunctionDefinition)
				{
					AImplicitFunctionDefinition function = (AImplicitFunctionDefinition) def;
					VdmType type = null;
					//  In terms of type only 'class types' are treated
					type = getType(((AFunctionType) function.getType()).getResult(), assistantFactory);
					newDefinition = new VdmMethod(function.getName().getName(), false, type);
					// fetching the arguments
					for (List<PPattern> li : assistantFactory.createAImplicitFunctionDefinitionAssistant().getParamPatternList(function))
					{
						for (int n = 0; n < li.size(); ++n)
						{
							LexNameList varName = assistantFactory.createPPatternAssistant().getVariableNames(li.get(n));
							// the type
							String typeName = extractTypeName(function.getType(), n);
							boolean flag = false;
							for (String cn : classNames)
							{
								if (typeName.equals(cn))
								{
									flag = true;
								}
							}
							((VdmMethod) newDefinition).addParam(new VdmParam(varName.toString(), new VdmType(typeName, flag)));
							extractTypeName(function.getType(), n);
						}
					}
				}
				// adds annotations if any
				if (newDefinition != null)
				{
					if (annotationTable.getOpAnnotations(vdmClass.getName()
							+ newDefinition.getName()) != null)
					{
						for (VdmAnnotation annotation : annotationTable.getOpAnnotations(vdmClass.getName()
								+ newDefinition.getName()))
						{
							newDefinition.addAnnotation(annotation);
						}
					}
				}
			} // isFunctionOrOperation()
				// we add the new definition
			vdmClass.addDefinition(newDefinition);
			// adds annotations if any
			if (annotationTable.classHasAnnotations(vdmClass.getName()))
			{
				for (VdmAnnotation annotation : annotationTable.getClassAnnotations(vdmClass.getName()))
				{
					vdmClass.addAnnotation(annotation);
				}
			}
		}

		//  we only have one annotation for classes, so we can get away with this, but
		// a sanity check is needed...
		if (!(ToolSettings.GENERATION_SETTINGS == ToolSettings.GENERATION_MODE.ANNOTATIONS && vdmClass.hasAnnotations()))
		{
			classList.add(vdmClass);
		}

	}

	


	public static VdmType getType(PType type,
			ITypeCheckerAssistantFactory assistantFactory) // added parameter for the assistantFactory
	{
		if (assistantFactory.createPTypeAssistant().isClass(type, null))
		{
			return new VdmType(type.getLocation().getModule(), true);
		}
		return null;
	}

	@Override
	public Vector<String> getClassNames()
	{
		Vector<String> classNames = new Vector<String>();
		for (IVdmDefinition def : classList)
		{
			classNames.add(def.getName());
		}
		return classNames;
	}

	/**
	 * Given a vdm operation signature (ex.: '( nat * nat => nat )' ) extracts the name (string) of the type.
	 * 
	 * @param type
	 *            - the function/operation type
	 * @param n
	 *            - n argument we want to extract the type from
	 * @return type name
	 */
	private String extractTypeName(PType type, int n)
	{
		//  rewrite
		String ret = type.toString();
		// System.out.println(ret);
		ret = ret.replaceAll("\\(", "");
		ret = ret.replaceAll("\\)", "");
		if (ret.contains("==>"))
		{
			// System.out.println(ret);
			ret = ret.substring(0, ret.indexOf("==>"));
		} else if (ret.contains("+>"))
		{
			// System.out.println(ret);
			ret = ret.substring(0, ret.indexOf("+>"));
		} else if (ret.contains("->"))
		{
			// System.out.println(ret);
			ret = ret.substring(0, ret.indexOf("->"));
		}

		String[] tokens = ret.split("\\*");
		String result = tokens[n].trim();
		// System.out.println("Res: "+result);
		return result;
	}

}
