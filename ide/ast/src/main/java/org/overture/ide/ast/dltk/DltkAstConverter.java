package org.overture.ide.ast.dltk;

import java.util.Iterator;
import java.util.List;

import org.eclipse.dltk.ast.declarations.Argument;
import org.eclipse.dltk.ast.declarations.FieldDeclaration;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.ast.references.ConstantReference;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.overture.ide.ast.util.VdmAstUtil;
import org.overture.ide.ast.util.VdmAstUtilExpression;
import org.overture.ide.utility.SourceLocationConverter;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.definitions.ImplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ImplicitOperationDefinition;
import org.overturetool.vdmj.definitions.ValueDefinition;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.statements.AlwaysStatement;
import org.overturetool.vdmj.statements.AssignmentStatement;
import org.overturetool.vdmj.statements.AtomicStatement;
import org.overturetool.vdmj.statements.BlockStatement;
import org.overturetool.vdmj.statements.CallObjectStatement;
import org.overturetool.vdmj.statements.CallStatement;
import org.overturetool.vdmj.statements.CasesStatement;
import org.overturetool.vdmj.statements.ClassInvariantStatement;
import org.overturetool.vdmj.statements.CyclesStatement;
import org.overturetool.vdmj.statements.DefStatement;
import org.overturetool.vdmj.statements.DurationStatement;
import org.overturetool.vdmj.statements.ElseIfStatement;
import org.overturetool.vdmj.statements.ErrorStatement;
import org.overturetool.vdmj.statements.ExitStatement;
import org.overturetool.vdmj.statements.ForAllStatement;
import org.overturetool.vdmj.statements.ForIndexStatement;
import org.overturetool.vdmj.statements.ForPatternBindStatement;
import org.overturetool.vdmj.statements.IfStatement;
import org.overturetool.vdmj.statements.LetBeStStatement;
import org.overturetool.vdmj.statements.LetDefStatement;
import org.overturetool.vdmj.statements.NotYetSpecifiedStatement;
import org.overturetool.vdmj.statements.PeriodicStatement;
import org.overturetool.vdmj.statements.ReturnStatement;
import org.overturetool.vdmj.statements.SimpleBlockStatement;
import org.overturetool.vdmj.statements.SkipStatement;
import org.overturetool.vdmj.statements.SpecificationStatement;
import org.overturetool.vdmj.statements.StartStatement;
import org.overturetool.vdmj.statements.Statement;
import org.overturetool.vdmj.statements.SubclassResponsibilityStatement;
import org.overturetool.vdmj.statements.TixeStatement;
import org.overturetool.vdmj.statements.TraceStatement;
import org.overturetool.vdmj.statements.TrapStatement;
import org.overturetool.vdmj.statements.WhileStatement;
import org.overturetool.vdmj.traces.TraceVariableStatement;
import org.overturetool.vdmj.types.FunctionType;
import org.overturetool.vdmj.types.MapType;
import org.overturetool.vdmj.types.OperationType;
import org.overturetool.vdmj.types.SeqType;
import org.overturetool.vdmj.types.SetType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.types.UnionType;
import org.overturetool.vdmj.types.UnresolvedType;

@SuppressWarnings("unchecked")
public class DltkAstConverter {
	ModuleDeclaration model;
	SourceLocationConverter converter;

	public SourceLocationConverter getDltkConverter(){
		return converter;
	}
	
	public DltkAstConverter(char[] source) {
		model = new ModuleDeclaration(source.length);
		converter = new SourceLocationConverter(source);
	}

	public ModuleDeclaration parse(List modules) {

		for (Iterator i = modules.iterator(); i.hasNext();) {
			Object next = i.next();
			if (next instanceof Module) {
				Module module = (Module) next;
				addModuleDefinition(module);
			}
			if (next instanceof ClassDefinition) {
				ClassDefinition _class = (ClassDefinition) next;
				addClassDefinition(_class);
			}
		}

		return model;
	}

	private void addClassDefinition(ClassDefinition classDef) {

		LexLocation loc = classDef.name.location;
		TypeDeclaration classDefinition = new TypeDeclaration(
				classDef.name.name, converter.getStartPos(loc), converter
						.getEndPos(loc), converter.getStartPos(loc),
				converter.getEndPos(loc));

		if (classDef.supernames.size() > 0) {

			for (LexNameToken lexName : classDef.supernames) {
				// create ConstantReference to super classes
				int startPos = converter.convert(lexName.location.startLine,
						lexName.location.startPos - 1);
				int endPos = converter.convert(lexName.location.endLine,
						lexName.location.endPos - 1);
				ConstantReference constRef = new ConstantReference(startPos,
						endPos, lexName.name);
				classDefinition.addSuperClass(constRef);
			}
		}

		for (Iterator<Definition> i = classDef.definitions.iterator(); i
				.hasNext();) {

			Definition def = i.next();

			addDefinition(classDefinition, def);

		}

		model.addStatement(classDefinition);
	}

	/**
	 * Add class declaration to the module declaration. If any super classes
	 * exist a reference to the super class will be added
	 * 
	 * @param classDef
	 *            VDMJ classDifinition
	 */
	private void addModuleDefinition(Module module) {
		// int startPos = converter.convert(module..location.startLine,
		// module.location.startPos -1);
		// int endPos = converter.convert(module.location.endLine,
		// module.location.endPos -1);
		// classDeclaration = new VDMClassDeclaration(
		// classDef.name.name,
		// startPos, endPos, startPos, endPos,
		// classDef.accessSpecifier
		// );
		//		
		// // Any superClasses
		// if (classDef.supernames.size() > 0){
		// for (LexNameToken lexName : classDef.supernames) {
		// // create ConstantReference to super classes
		// startPos = converter.convert(lexName.location.startLine,
		// lexName.location.startPos -1);
		// endPos = converter.convert(lexName.location.endLine,
		// lexName.location.endPos -1);
		// ConstantReference constRef = new ConstantReference(startPos, endPos,
		// lexName.name);
		// classDeclaration.addSuperClass(constRef);
		// }
		// }
		LexLocation loc = module.name.location;
		TypeDeclaration moduleDefinition = new TypeDeclaration(
				module.name.name, converter.getStartPos(loc), converter
						.getEndPos(loc), converter.getStartPos(loc),
				converter.getEndPos(loc));

		for (Iterator<Definition> i = module.defs.iterator(); i.hasNext();) {

			Definition def = i.next();

			addDefinition(moduleDefinition, def);

		}

		// TypeDeclaration typesDefinition = new TypeDeclaration("Types",
		// 0, 0, 0, 0);
		// moduleDefinition.getStatements().add(typesDefinition);

		model.addStatement(moduleDefinition);

	}

	private void addDefinition(TypeDeclaration moduleDefinition, Definition def) {
		if (def.name != null && def.isFunctionOrOperation()) {
			addFunctionOrOperationDefinition(moduleDefinition, def);
			return;
		}

		if (def.name != null && def.isTypeDefinition()) {
			addTypeDefinition(moduleDefinition, def);
			return;
		}

		if (def.isValueDefinition()) {
			addValueDefinition(moduleDefinition, def);
			return;
		}

		if (def.name != null && def.isInstanceVariable()) {
			addInstanceVariable(moduleDefinition, def);
			return;
		}
	}

	private void addInstanceVariable(TypeDeclaration moduleDefinition,
			Definition def) {
		LexLocation location = def.location;

		FieldDeclaration field = new FieldDeclaration(def.name.name, converter
				.getStartPos(location), converter.getEndPos(location) - 1,
				converter.getStartPos(location), converter
						.getEndPos(location) - 1);
		field.setModifier(VdmAstUtil.getModifier(def.accessSpecifier));

		moduleDefinition.getStatements().add(field);

	}

	private void addFunctionOrOperationDefinition(
			TypeDeclaration moduleDefinition, Definition def) {

		if (def instanceof ImplicitFunctionDefinition
				|| def instanceof ExplicitFunctionDefinition)
			addFunctionDefinition(moduleDefinition, def);

		if (def instanceof ImplicitOperationDefinition
				|| def instanceof ExplicitOperationDefinition)
			addOperationDefinition(moduleDefinition, def);
		// TODO Auto-generated method stub

	}

	private void addOperationDefinition(TypeDeclaration moduleDefinition,
			Definition def) {

		int startPos = converter.convert(def.location.startLine,
				def.location.startPos - 1);
		int endPos = converter.convert(def.location.endLine,
				def.location.endPos - 1);
		MethodDeclaration methodDeclaration = new MethodDeclaration(
				def.name.name, startPos, endPos, startPos, endPos);
		methodDeclaration.setModifiers(VdmAstUtil
				.getModifier(def.accessSpecifier));

		if (def.getType() instanceof OperationType) {
			OperationType type = (OperationType) def.getType();
			for (Type definition : type.parameters) {
				String name = ProcessUnresolved(definition);
				LexLocation loc = definition.location;
				SimpleReference argumentName = new SimpleReference(
						definition.location.startPos,
						definition.location.endPos, name);
				methodDeclaration.addArgument(new Argument(argumentName,
						converter.getStartPos(loc), null, 0));
			}

		}

		if (def instanceof ExplicitOperationDefinition) {
			ExplicitOperationDefinition exOp = (ExplicitOperationDefinition) def;

			if (exOp.body instanceof BlockStatement) {
				BlockStatement block = (BlockStatement) exOp.body;
				for (Statement statement : block.statements) {
					addStatement(statement, methodDeclaration);
					// methodDeclaration.getBody().addStatement(addStatement(exOp.body));
				}
			} else {
				addStatement(exOp.body, methodDeclaration);
				// methodDeclaration.getBody().addStatement(addStatement(exOp.body));
			}
		}
		moduleDefinition.getStatements().add(methodDeclaration);

	}

	private void addStatement(Statement statement,
			MethodDeclaration methodDeclaration) {
		if (statement instanceof AlwaysStatement) {

		}

		if (statement instanceof AssignmentStatement) {

		}

		if (statement instanceof AtomicStatement) {

		}

		if (statement instanceof CallObjectStatement) {

		}

		if (statement instanceof CallStatement) {

		}

		if (statement instanceof CasesStatement) {

		}

		if (statement instanceof ClassInvariantStatement) {

		}

		if (statement instanceof CyclesStatement) {

		}

		if (statement instanceof DefStatement) {

		}

		if (statement instanceof DurationStatement) {

		}

		if (statement instanceof ElseIfStatement) {

		}

		if (statement instanceof ErrorStatement) {

		}

		if (statement instanceof ExitStatement) {

		}

		if (statement instanceof ForAllStatement) {

		}

		if (statement instanceof ForIndexStatement) {

		}

		if (statement instanceof ForPatternBindStatement) {

		}

		if (statement instanceof IfStatement) {

		}

		if (statement instanceof LetBeStStatement) {

		}

		if (statement instanceof LetDefStatement) {

		}

		if (statement instanceof NotYetSpecifiedStatement) {

		}

		if (statement instanceof PeriodicStatement) {

		}

		if (statement instanceof ReturnStatement) {

		}

		if (statement instanceof SimpleBlockStatement) {

		}

		if (statement instanceof SkipStatement) {

		}

		if (statement instanceof SpecificationStatement) {

		}

		if (statement instanceof StartStatement) {

		}

		if (statement instanceof SubclassResponsibilityStatement) {

		}

		if (statement instanceof TixeStatement) {

		}

		if (statement instanceof TraceStatement) {

		}

		if (statement instanceof TraceVariableStatement) {

		}

		if (statement instanceof TrapStatement) {

		}

		if (statement instanceof WhileStatement) {

		}
	}

	
	private void addValueDefinition(TypeDeclaration moduleDefinition,
			Definition def) {
		if (def instanceof ValueDefinition) {
			ValueDefinition value = (ValueDefinition) def;

			LexNameList lexlist = value.getVariableNames();

			Iterator<LexNameToken> it = lexlist.iterator();

			while (it.hasNext()) {
				LexNameToken ltoken = it.next();
				LexLocation location = ltoken.location;

				FieldDeclaration fieldValue = new FieldDeclaration(ltoken.name,
						converter.getStartPos(location), converter
								.getEndPos(location) - 1, converter
								.getStartPos(location), converter
								.getEndPos(location) - 1);
				// fieldValue.setModifier(TypeDeclaration.AccPrivate);
				fieldValue.setModifier(VdmAstUtil
						.getModifier(value.accessSpecifier));
				moduleDefinition.getStatements().add(fieldValue);
			}

		}
	}

	private void addTypeDefinition(TypeDeclaration moduleDefinition,
			Definition def) {

		LexLocation location = def.location;
		FieldDeclaration type = new FieldDeclaration(def.name.name, converter
				.getStartPos(location), converter.getEndPos(location) - 1,
				converter.getStartPos(location), converter
						.getEndPos(location) - 1);
		type.setModifier(TypeDeclaration.D_TYPE_DECL);
		moduleDefinition.getStatements().add(type);

		// moduleDefinition.getStatements().add(new FieldDeclaration("BALBLA",
		// 0, 0, 0, 0));

	}

	private void addFunctionDefinition(TypeDeclaration moduleDefinition,
			Definition def) {

		LexLocation loc = def.location;


//		System.out.println("Method name:" + def.name.name + " Start: "
//				+ converter.getStartPos(loc) + " End: "
//				+ converter.getEndPos(loc));


		MethodDeclaration method = new MethodDeclaration(def.name.name,
				converter.getStartPos(loc), converter.getEndPos(loc),
				converter.getStartPos(loc), converter.getEndPos(loc));
		method.setModifier(VdmAstUtil.getModifier(def.accessSpecifier));
		
		if (def.getType() instanceof FunctionType) {
			FunctionType functionType = (FunctionType) def.getType();
			for (Type definition : functionType.parameters) {

				String name = ProcessUnresolved(definition);
				SimpleReference argumentName = new SimpleReference(0, 0, name);
				method.addArgument(new Argument(argumentName, 0, null, 0));
			}

			Type definition = functionType.result;
			SimpleReference resultName = new SimpleReference(0, 0, definition
					.toString());

			if (def instanceof ExplicitFunctionDefinition) {
				ExplicitFunctionDefinition exFunc = (ExplicitFunctionDefinition) def;

				VdmAstUtilExpression.addExpression(exFunc.body, method,converter);

				// if (exFunc.body instanceof BlockStatement)
				// {
				// BlockStatement block = (BlockStatement) exOp.body;
				// for (Statement statement : block.statements)
				// {
				// addStatement(statement, methodDeclaration);
				// //methodDeclaration.getBody().addStatement(addStatement(exOp.body));
				// }
				// }
				// else
				// {
				// addStatement(exOp.body, methodDeclaration);
				// //methodDeclaration.getBody().addStatement(addStatement(exOp.body));
				// }
			}

		}
		moduleDefinition.getStatements().add(method);
	}

	private String ProcessUnresolved(Type definition) {

		String defString = definition.toString();
		if (defString.contains("unresolved "))
		{
			defString = defString.replace("(", "");
			defString = defString.replace(")", "");
			defString = defString.replace("unresolved ", "");
			return defString;
			
		}
//		if ( !definition.resolved){
//			if ( definition instanceof UnresolvedType){
//				UnresolvedType unresolvedType = (UnresolvedType) definition;
//				return unresolvedType.typename.toString();
//			}
//			else if (definition instanceof UnionType){
//				UnionTypeVDM unionType = (UnionTypeVDM) definition;
//				return unionType.toDisplay();
//			}
//		}
//		if (definition.isFunction()) {
//			FunctionType funcType = definition.getFunction();
//if(funcType==null)
//	return "";
//			TypeList parameters = funcType.parameters;//TODO check for null first
//			Type result = funcType.result;
//
//			StringBuilder resultF = new StringBuilder();
//
//			if (parameters.size() > 0) {
//				Iterator<Type> itTypes = parameters.iterator();
//
//				while (itTypes.hasNext()) {
//					resultF.append(ProcessUnresolved(itTypes.next()));
//					if (itTypes.hasNext())
//						resultF.append(" * ");
//				}
//			} else
//				resultF.append("()");
//
//			resultF.append(" -> ");
//			resultF.append(ProcessUnresolved(result));
//
//			return resultF.toString();
//
//		}
//
//		if (definition.isMap()) {
//			MapType mapType = definition.getMap();
//			Type from = mapType.from;
//			Type to = mapType.to;
//
//			return "map " + ProcessUnresolved(from) + " to "
//					+ ProcessUnresolved(to);
//
//		}
//
//		if (definition.isProduct()) {
//			//System.out.print("Product");
//		}
//
//		if (definition.isRecord()) {
//
//		}
//
//		if (definition.isSeq()) {
//			SeqType seqType = definition.getSeq();
//			Type t = seqType.seqof;
//			return "seq of " + ProcessUnresolved(t);
//		}
//
//		if (definition.isSet()) {
//			SetType setType = definition.getSet();
//			Type t = setType.setof;
//			return "set of " + ProcessUnresolved(t);
//		}
//
//		if (definition.isUnion()) {
//			UnionType type = definition.getUnion();
//			TypeSet defList = type.types;
//
//			Iterator<Type> it = defList.iterator();
//			StringBuilder result = new StringBuilder();
//			while (it.hasNext()) {
//				Type d = it.next();
//				result = result.append(ProcessUnresolved(d));
//				if (it.hasNext())
//					result.append(" | ");
//			}
//
//			return result.toString();
//		}
//
//		if (definition instanceof UnresolvedType) {
//			UnresolvedType uType = (UnresolvedType) definition;
//			return uType.typename.toString();
//		} else
			return definition.toString();
	}
	
	private static int getStartPos(LexLocation loc, SourceLocationConverter converter)
	{
		return converter.convert(loc.startLine, loc.startPos) - 1;
	}
	
	private static int getEndPos(LexLocation loc, SourceLocationConverter converter)
	{
		return converter.convert(loc.endLine, loc.endPos);
	}

}
