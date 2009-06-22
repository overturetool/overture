package org.overturetool.eclipse.plugins.editor.core.internal.parser;

import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.eclipse.dltk.ast.declarations.Argument;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.ast.expressions.BooleanLiteral;
import org.eclipse.dltk.ast.expressions.CallExpression;
import org.eclipse.dltk.ast.expressions.NumericLiteral;
import org.eclipse.dltk.ast.references.ConstantReference;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.ast.references.VariableReference;
import org.eclipse.osgi.baseadaptor.loader.ClasspathEntry;
import org.overturetool.ast.imp.OmlInstanceVariable;
import org.overturetool.ast.imp.OmlInstanceVariableDefinitions;
import org.overturetool.ast.imp.OmlOperationDefinition;
import org.overturetool.ast.imp.OmlOperationDefinitions;
import org.overturetool.ast.itf.IOmlClass;
import org.overturetool.ast.itf.IOmlExplicitOperation;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.AccessModifierConvert;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.OvertureAssignment;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.OvertureCallStatement;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.OvertureIfStatement;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.OvertureModuleDeclaration;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.OvertureStatement;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.VDMClassDeclaration;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.VDMFieldDeclaration;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.VDMJASTUtil;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.VDMMethodDeclaration;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.expressions.ApplyExpression;
import org.overturetool.vdmj.expressions.BooleanLiteralExpression;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.IntegerLiteralExpression;
import org.overturetool.vdmj.expressions.NewExpression;
import org.overturetool.vdmj.expressions.VariableExpression;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.statements.AssignmentStatement;
import org.overturetool.vdmj.statements.BlockStatement;
import org.overturetool.vdmj.statements.CallObjectStatement;
import org.overturetool.vdmj.statements.CallStatement;
import org.overturetool.vdmj.statements.IfStatement;
import org.overturetool.vdmj.statements.Statement;
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
				addClassDefinition(classDef);
				
				// Function or Operation
				DefinitionList defList = classDef.getDefinitions();
				
				for (Definition def : defList) {
					if (def != null){
						if (def.isFunctionOrOperation())
						{
							addMethodDeclaration(def);
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
	
	
	private void addMethodDeclaration(Definition def)
	{
		int startPos = converter.convert(def.location.startLine, def.location.startPos -1);
		int endPos = converter.convert(def.location.endLine, def.location.endPos -1); 
		MethodDeclaration methodDeclaration = new MethodDeclaration(
				def.name.name,
				startPos, endPos, startPos, endPos);
		methodDeclaration.setModifiers(AccessModifierConvert.getModifier(def.accessSpecifier));
		
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
        
        // -----------------------------------------
        // add statements 
        // -----------------------------------------
        if (def instanceof ExplicitOperationDefinition){        	
        	ExplicitOperationDefinition exOp = (ExplicitOperationDefinition) def;
        	
       		if (exOp.body instanceof BlockStatement)
       		{
       			BlockStatement block = (BlockStatement) exOp.body;
       			for (Statement statement : block.statements)
       			{
       				addStatement(statement, methodDeclaration);
       				//methodDeclaration.getBody().addStatement(addStatement(exOp.body));
       			}
        	}
       		else
       		{
       			addStatement(exOp.body, methodDeclaration);
       			//methodDeclaration.getBody().addStatement(addStatement(exOp.body));
       		}
        }
        classDeclaration.getStatements().add(methodDeclaration);
	}
	
	/**
	 * Add class declaration to the module declaration.
	 * If any super classes exist a reference to the super class will be added
	 * @param classDef VDMJ classDifinition
	 */
	private void addClassDefinition(ClassDefinition classDef)
	{
		int startPos = converter.convert(classDef.location.startLine, classDef.location.startPos -1);
		int endPos = converter.convert(classDef.location.endLine, classDef.location.endPos -1); 
		classDeclaration = new VDMClassDeclaration( 
					classDef.name.name, 
					startPos, endPos, startPos, endPos, 
					classDef.accessSpecifier
				);
		
		// Any superClasses
		if (classDef.supernames.size() > 0){
			for (LexNameToken lexName : classDef.supernames) {
				// create ConstantReference to super classes
				startPos = converter.convert(lexName.location.startLine, lexName.location.startPos -1);
				endPos = converter.convert(lexName.location.endLine, lexName.location.endPos -1);
				ConstantReference constRef = new ConstantReference(startPos, endPos, lexName.name);
				classDeclaration.addSuperClass(constRef);
			}
		}
		moduleDeclaration.addStatement(classDeclaration);
	}
	
	private void addStatement(Statement statement, MethodDeclaration methodDeclaration){
		if (statement instanceof CallStatement){
			methodDeclaration.getBody().addStatement(VDMJASTUtil.createCallExpression((CallStatement)statement, converter));
		}
		if (statement instanceof AssignmentStatement){
			OvertureAssignment assignment = createAssignmentStatement((AssignmentStatement)statement);
			if (assignment != null)
			{
				methodDeclaration.getBody().addStatement(assignment);
			}
		}
		if (statement instanceof IfStatement)
		{
			//TODO 
			//methodDeclaration.getBody().addStatement(VDMJASTUtil.createIfStatement((IfStatement) statement, converter));
		}
		if (statement instanceof CallObjectStatement)
		{
			methodDeclaration.getBody().addStatement(VDMJASTUtil.createCallObject((CallObjectStatement)statement, converter));
		}
		// TODO 
		// while        ** TODO
		// elseif       ** TODO
		// forall       ** TODO
		// forIndex     ** TODO
		// forpattern   ** TODO
		// if 			** TODO
		// letBeST      ** TODO
		// LetDef       ** TODO
		// CallObject   ** TODO

		/// is the following statement needed in method?
		// Always
		// AssignmentStatement
		// Atomic
		// cases
		// Classinvariant
		// cycle
		// def
		// duration
		// error
		// exit
		// notyetspecified
		// periodic
		// return
		// block (simple block, block, nonDerministic
		// skip 
		// specification
		// start
		// subclass
		// tixestartement
		// trace
		// trap
	}
	
	private OvertureAssignment createAssignmentStatement(AssignmentStatement assStamement )
	{
		OvertureAssignment overtureAssignment = null; 
		// left = target
		VariableReference leftVarRef = VDMJASTUtil.createVariableReference(assStamement.target.toString(), assStamement.target.location, converter);

		
		// right = exp
		// find the right hand side
		Expression exp = assStamement.exp;
		if (exp instanceof ApplyExpression)
		{
			CallExpression callExp = VDMJASTUtil.createCallExpression((ApplyExpression) exp, converter);
			overtureAssignment = new OvertureAssignment(leftVarRef,callExp);
		}
		if (exp instanceof BooleanLiteralExpression){
			BooleanLiteral boolLit = VDMJASTUtil.createBooleanLiteral((BooleanLiteralExpression) exp, converter);
			overtureAssignment = new OvertureAssignment(leftVarRef,boolLit);
		}
		if (exp instanceof IntegerLiteralExpression){
			NumericLiteral numericLiteral = VDMJASTUtil.createNumericLiteral((IntegerLiteralExpression) exp, converter);
			overtureAssignment = new OvertureAssignment(leftVarRef,numericLiteral);
		}
		if (exp instanceof VariableExpression)
		{
			VariableExpression varExp = (VariableExpression) exp;
			VariableReference varReference = VDMJASTUtil.createVariableReference(varExp, varExp.location, converter);
			overtureAssignment = new OvertureAssignment(leftVarRef, varReference);
		}
		if (exp instanceof NewExpression)
		{
			
		}
		
		// add Assignment
		if (overtureAssignment != null){
			return overtureAssignment;
		}
		return null;
		
	}
}
