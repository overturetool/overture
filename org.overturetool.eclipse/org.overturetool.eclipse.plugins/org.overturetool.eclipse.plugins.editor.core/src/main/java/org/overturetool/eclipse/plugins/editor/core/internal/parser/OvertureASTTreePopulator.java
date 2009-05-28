package org.overturetool.eclipse.plugins.editor.core.internal.parser;

import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.eclipse.dltk.ast.declarations.Argument;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.overturetool.ast.imp.OmlInstanceVariable;
import org.overturetool.ast.imp.OmlInstanceVariableDefinitions;
import org.overturetool.ast.imp.OmlOperationDefinition;
import org.overturetool.ast.imp.OmlOperationDefinitions;
import org.overturetool.ast.itf.IOmlClass;
import org.overturetool.ast.itf.IOmlExplicitOperation;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.OvertureModuleDeclaration;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.VDMClassDeclaration;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.VDMFieldDeclaration;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.VDMMethodDeclaration;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.types.FunctionType;
import org.overturetool.vdmj.types.OperationType;
import org.overturetool.vdmj.types.Type;

public class OvertureASTTreePopulator {

	private OvertureModuleDeclaration moduleDeclaration;
	private DLTKConverter converter;
	private VDMClassDeclaration classDeclaration;
	
	public OvertureASTTreePopulator(OvertureModuleDeclaration moduleDeclaration, DLTKConverter converter) {
		this.moduleDeclaration = moduleDeclaration;
		this.converter = converter;
	}
	
	public OvertureModuleDeclaration populateOverture(Vector classList) throws CGException
	{
		for (Object object : classList) {
			if (object instanceof IOmlClass)
			{
				
				// class
				IOmlClass classDef = (IOmlClass) object;
				int offset = converter.convert(classDef.getLine().intValue(), classDef.getColumn().intValue());
				VDMClassDeclaration classDeclaration = new VDMClassDeclaration(
						classDef.getIdentifier(),
						offset,
						offset,
						offset,
						offset,
						TypeDeclaration.AccPublic);
				moduleDeclaration.addStatement(classDeclaration);
				
				// method
				for (Object body : classDef.getClassBody()) {
					if (body instanceof IOmlExplicitOperation)
					{
						IOmlClass explicitOperation = (IOmlClass) body;
						int offsetOperation = converter.convert(explicitOperation.getLine().intValue(), explicitOperation.getColumn().intValue());
						VDMMethodDeclaration methodDeclaration = new VDMMethodDeclaration(
								explicitOperation.getIdentifier(),
								offsetOperation,
								offsetOperation,
								offsetOperation,
								offsetOperation,
				                MethodDeclaration.AccPublic);
						classDeclaration.getStatements().add(methodDeclaration);
					}
					if (body instanceof OmlOperationDefinitions){
						OmlOperationDefinitions operationDefinitions = (OmlOperationDefinitions) body;
						for (Object opDef : operationDefinitions.getOperationList()) {
							if (opDef instanceof OmlOperationDefinition)
							{
								OmlOperationDefinition operation = (OmlOperationDefinition) opDef;
								int offsetOperation = converter.convert(operation.getLine().intValue(), operation.getColumn().intValue());
								VDMMethodDeclaration methodDeclaration = new VDMMethodDeclaration(
										operation.getShape().identity(),
										offsetOperation,
										offsetOperation,
										offsetOperation,
										offsetOperation,
										operation.getAccess());
								classDeclaration.getStatements().add(methodDeclaration);
							}
						}
					}
					
					if (body instanceof OmlInstanceVariableDefinitions){
						OmlInstanceVariableDefinitions varDefs = (OmlInstanceVariableDefinitions) body;
						for (Object varDef : varDefs.getVariablesList()) {
							if (varDef instanceof OmlInstanceVariable)
							{
								OmlInstanceVariable instanceVar = (OmlInstanceVariable) varDef;
								int offsetField = converter.convert(instanceVar.getLine().intValue(), instanceVar.getColumn().intValue());
								VDMFieldDeclaration fieldDeclaration = new VDMFieldDeclaration(
										instanceVar.getAssignmentDefinition().getIdentifier(),
										offsetField,
										offsetField,
										offsetField,
										offsetField,
										instanceVar.getAccess()
								);
								
								classDeclaration.getStatements().add(fieldDeclaration);
							}
							
//							int offsetOperation = converter.convert(explicitOperation.getLine().intValue(), explicitOperation.getColumn().intValue());
						}
					}
				}
				
				
			}
		}
		return moduleDeclaration;
	}
	
	public OvertureModuleDeclaration populateVDMJ(ClassList classList)
	{
		try
		{
			for (ClassDefinition classDef : classList)
			{
				classDeclaration = new VDMClassDeclaration(
							classDef.name.name,
							converter.convert(classDef.location.startLine, classDef.location.startPos -1),
							converter.convert(classDef.location.endLine, classDef.location.endPos -1),
							converter.convert(classDef.location.startLine, classDef.location.startPos -1),
							converter.convert(classDef.location.endLine, classDef.location.endPos -1),
							classDef.accessSpecifier);
				moduleDeclaration.addStatement(classDeclaration);
				
				// Function or Operation
				DefinitionList defList = classDef.getDefinitions();
				
				for (Definition def : defList) {
					if (def != null){
						if (def.isFunctionOrOperation())
						{
							VDMMethodDeclaration methodDeclaration = new VDMMethodDeclaration(
									def.name.name,
									converter.convert(def.location.startLine, def.location.startPos -1),
									converter.convert(def.location.endLine, def.location.endPos -1),
									converter.convert(def.location.startLine, def.location.startPos -1),
									converter.convert(def.location.endLine, def.location.endPos -1),
			                        def.accessSpecifier);
							
			                if (def.getType() instanceof OperationType)
			                {
		                		OperationType type = (OperationType) def.getType();
		                		for (Type definition : type.parameters) {
				                	SimpleReference argumentName = new SimpleReference(definition.location.startPos,definition.location.endPos, definition.toString());
				                    methodDeclaration.addArgument(new Argument(argumentName, 0, null, 0));
								}
			                }
			                if (def.getType() instanceof FunctionType)
			                {
			                	FunctionType type = (FunctionType) def.getType();
		                		for (Type definition : type.parameters) {
				                	SimpleReference argumentName = new SimpleReference(definition.location.startPos,definition.location.endPos, definition.toString());
				                    methodDeclaration.addArgument(new Argument(argumentName, 0, null, 0));
								}
			                }
			                classDeclaration.getStatements().add(methodDeclaration);
						}
						// InstanceVariable variable:
						if (def.isInstanceVariable())
						{
							classDeclaration.getStatements().add(
									 new VDMFieldDeclaration(
										 def.name.name,
									     converter.convert(def.location.startLine, def.location.startPos -1),
										 converter.convert(def.location.endLine, def.location.endPos -1),
										 converter.convert(def.location.startLine, def.location.startPos -1),
										 converter.convert(def.location.endLine, def.location.endPos -1),
										 def.accessSpecifier)
							 		);
//							System.out.println("instance var: " + def.name.name);
						}
						if (def.isTypeDefinition())
						{
							classDeclaration.getStatements().add(
									 new VDMFieldDeclaration(
										 def.name.name,
									     converter.convert(def.location.startLine, def.location.startPos -1),
										 converter.convert(def.location.endLine, def.location.endPos -1),
										 converter.convert(def.location.startLine, def.location.startPos -1),
										 converter.convert(def.location.endLine, def.location.endPos -1),
										 def.accessSpecifier)
							 		);
									 
//							System.out.println("name " + def.name.name + 
//									"\n\tstartPos: " + converter.convert(def.location.startLine, def.location.startPos -1) + 
//									"\tstartLine: " + def.location.startLine + 
//									"\tendPos: "+ converter.convert(def.location.endLine, def.location.endPos -1) + 
//									"\tendLine: " + def.location.endLine);
						}
						if (def.isValueDefinition())
						{
							classDeclaration.getStatements().add(
								 new VDMFieldDeclaration(
									 def.name.name,
									 converter.convert(def.location.startLine, def.location.startPos -1),
									 converter.convert(def.location.endLine, def.location.endPos -1),
									 converter.convert(def.location.startLine, def.location.startPos - 1),
									 converter.convert(def.location.endLine, def.location.endPos -1),
									 def.accessSpecifier)
						 		);
						}
						if (def.getType().isUnknown()){
							classDeclaration.getStatements().add(
									 new VDMFieldDeclaration(
										 def.name.name,
										 converter.convert(def.location.startLine, def.location.startPos -1),
										 converter.convert(def.location.endLine, def.location.endPos -1),
										 converter.convert(def.location.startLine, def.location.startPos - 1),
										 converter.convert(def.location.endLine, def.location.endPos -1),
										 def.accessSpecifier)
							 		);
						}
					}
				}
			}
			return moduleDeclaration;
		} catch (Exception e) {
			System.err.println("An error occured while extracting information from the parser " + e.getMessage());
			e.printStackTrace();
			return null;
		}

	}
	
}
