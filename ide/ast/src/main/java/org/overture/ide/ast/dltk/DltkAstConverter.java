package org.overture.ide.ast.dltk;

import java.util.Iterator;
import java.util.List;

import org.eclipse.dltk.ast.declarations.Argument;
import org.eclipse.dltk.ast.declarations.FieldDeclaration;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.ast.references.SimpleReference;
//import org.overture.ide.util.VDMJUtil;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ValueDefinition;
import org.overturetool.vdmj.expressions.ApplyExpression;
import org.overturetool.vdmj.expressions.BinaryExpression;
import org.overturetool.vdmj.expressions.BooleanLiteralExpression;
import org.overturetool.vdmj.expressions.BreakpointExpression;
import org.overturetool.vdmj.expressions.CasesExpression;
import org.overturetool.vdmj.expressions.CharLiteralExpression;
import org.overturetool.vdmj.expressions.ElseIfExpression;
import org.overturetool.vdmj.expressions.Exists1Expression;
import org.overturetool.vdmj.expressions.ExistsExpression;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.types.FunctionType;
import org.overturetool.vdmj.types.MapType;
import org.overturetool.vdmj.types.SeqType;
import org.overturetool.vdmj.types.SetType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.types.UnionType;
import org.overturetool.vdmj.types.UnresolvedType;

public class DltkAstConverter {
	ModuleDeclaration model;
	DltkConverter converter;

	public DltkAstConverter(char[] fileName, char[] source) {
		model = new ModuleDeclaration(source.length);
		converter = new DltkConverter(source);
	}

	public ModuleDeclaration parse(List<Module> modules) {

		for (Iterator<Module> i = modules.iterator(); i.hasNext();) {
			Object next = i.next();
			if(next instanceof Module){
				Module module = (Module) next;

			addModuleDefinition(module);
			}
		}

		return model;
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
				module.name.name, converter.convertStart(loc), converter
						.convertEnd(loc), converter.convertStart(loc),
				converter.convertEnd(loc));

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
			addFunctionDefinition(moduleDefinition, def);
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
	}

	private void addValueDefinition(TypeDeclaration moduleDefinition,
			Definition def) {
		if (def instanceof ValueDefinition) {
			ValueDefinition value = (ValueDefinition) def;
			
			LexNameList lexlist = value.getVariableNames();
			
			Iterator<LexNameToken> it = lexlist.iterator();
			
			while(it.hasNext())
			{
				LexNameToken ltoken = it.next();
				LexLocation location = ltoken.location;
				
				FieldDeclaration fieldValue = new FieldDeclaration(ltoken.name,
						converter.convertStart(location), converter
								.convertEnd(location)-1, converter
								.convertStart(location), converter
								.convertEnd(location)-1);
				fieldValue.setModifier(TypeDeclaration.AccPrivate);
				moduleDefinition.getStatements().add(fieldValue);
			}			
			
			
			
		}
	}

	private void addTypeDefinition(TypeDeclaration moduleDefinition,
			Definition def) {

		LexLocation location = def.location;
		FieldDeclaration type = new FieldDeclaration(def.name.name, converter
				.convertStart(location), converter.convertEnd(location) -1,
				converter.convertStart(location), converter
						.convertEnd(location)-1);
		type.setModifier(TypeDeclaration.D_TYPE_DECL);
		moduleDefinition.getStatements().add(type);

		// moduleDefinition.getStatements().add(new FieldDeclaration("BALBLA",
		// 0, 0, 0, 0));

	}

	private void addFunctionDefinition(TypeDeclaration moduleDefinition,
			Definition def) {
		LexLocation loc = def.location;

		MethodDeclaration method = new MethodDeclaration(def.name.name,
				converter.convertStart(loc), converter.convertEnd(loc),
				converter.convertStart(loc), converter.convertEnd(loc));
		if (def.getType() instanceof FunctionType) {
			FunctionType functionType = (FunctionType) def.getType();
			for (Type definition : functionType.parameters) {

				String name = ProcessUnresolved(definition);
				SimpleReference argumentName = new SimpleReference(0, 0, name);
				method.addArgument(new Argument(argumentName, 0, null, 0));

			}

//			Type definition = functionType.result;

//			SimpleReference resultName = new SimpleReference(0, 0, definition.toString());

			if (def instanceof ExplicitFunctionDefinition){        	
	        	ExplicitFunctionDefinition exFunc = (ExplicitFunctionDefinition) def;
	        	
	        	addExpression(exFunc.body,method);
	        	
//	       		if (exFunc.body instanceof BlockStatement)
//	       		{
//	       			BlockStatement block = (BlockStatement) exOp.body;
//	       			for (Statement statement : block.statements)
//	       			{
//	       				addStatement(statement, methodDeclaration);
//	       				//methodDeclaration.getBody().addStatement(addStatement(exOp.body));
//	       			}
//	        	}
//	       		else
//	       		{
//	       			addStatement(exOp.body, methodDeclaration);
//	       			//methodDeclaration.getBody().addStatement(addStatement(exOp.body));
//	       		}
			}
			
			
		}
		moduleDefinition.getStatements().add(method);
	}

	private void addExpression(Expression expression, MethodDeclaration method) {
		
		if(expression instanceof ApplyExpression)
		{
//			ApplyExpression appExpr = (ApplyExpression) expression;
//			CallExpression exp = VDMJUtil.createCallExpression(appExpr, converter);
		}
		
		if(expression instanceof BinaryExpression)
		{
			
		}
		
		if(expression instanceof BooleanLiteralExpression)
		{
			
		}
		
		if(expression instanceof BreakpointExpression)
		{
			
		}
		
		if(expression instanceof CasesExpression)
		{
		
		}
		
		if(expression instanceof CharLiteralExpression)
		{
			
		}
		
		if(expression instanceof ElseIfExpression)
		{
			
		}
		
		if(expression instanceof Exists1Expression)
		{
			
		}
		
		if(expression instanceof ExistsExpression)
		{
			
		}
	}

	private String ProcessUnresolved(Type definition) {

		if (definition.isFunction()) {
			FunctionType funcType = definition.getFunction();

			TypeList parameters = funcType.parameters;
			Type result = funcType.result;

			StringBuilder resultF = new StringBuilder();

			if (parameters.size() > 0) {
				Iterator<Type> itTypes = parameters.iterator();

				while (itTypes.hasNext()) {
					resultF.append(ProcessUnresolved(itTypes.next()));
					if (itTypes.hasNext())
						resultF.append(" * ");
				}
			} else
				resultF.append("()");

			resultF.append(" -> ");
			resultF.append(ProcessUnresolved(result));

			return resultF.toString();

		}

		if (definition.isMap()) {
			MapType mapType = definition.getMap();
			Type from = mapType.from;
			Type to = mapType.to;

			return "map " + ProcessUnresolved(from) + " to "
					+ ProcessUnresolved(to);

		}

		if (definition.isProduct()) {
			System.out.print("Product");
		}

		if (definition.isRecord()) {

		}

		if (definition.isSeq()) {
			SeqType seqType = definition.getSeq();
			Type t = seqType.seqof;
			return "seq of " + ProcessUnresolved(t);
		}

		if (definition.isSet()) {
			SetType setType = definition.getSet();
			Type t = setType.setof;
			return "set of " + ProcessUnresolved(t);
		}

		if (definition.isUnion()) {
			UnionType type = definition.getUnion();
			TypeSet defList = type.types;

			Iterator<Type> it = defList.iterator();
			StringBuilder result = new StringBuilder();
			while (it.hasNext()) {
				Type d = it.next();
				result = result.append(ProcessUnresolved(d));
				if (it.hasNext())
					result.append(" | ");
			}

			return result.toString();
		}

		if (definition instanceof UnresolvedType) {
			UnresolvedType uType = (UnresolvedType) definition;
			return uType.typename.toString();
		} else
			return definition.toString();
	}

}
