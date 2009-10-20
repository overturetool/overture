package org.overture.ide.ast.util;


import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.ast.expressions.BooleanLiteral;
import org.eclipse.dltk.ast.expressions.CallArgumentsList;
import org.eclipse.dltk.ast.expressions.CallExpression;
import org.eclipse.dltk.ast.expressions.NumericLiteral;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.ast.references.VariableReference;
import org.overture.ide.ast.dltk.DltkConverter;
import org.overturetool.vdmj.definitions.AccessSpecifier;
import org.overturetool.vdmj.expressions.ApplyExpression;
import org.overturetool.vdmj.expressions.BooleanLiteralExpression;
import org.overturetool.vdmj.expressions.ExpressionList;
import org.overturetool.vdmj.expressions.IntegerLiteralExpression;
import org.overturetool.vdmj.expressions.VariableExpression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.statements.CallObjectStatement;
import org.overturetool.vdmj.statements.CallStatement;

public class VdmAstUtil {
	

		public static CallExpression createCallExpression(CallStatement callStatement, DltkConverter converter)
		{
			String name = callStatement.name.name;
			int start = getStartPos(callStatement.location, converter);
			int end = getEndPos(callStatement.location, converter);
			// TODO receiver???
			if (callStatement.args.size() > 0)
			{
				CallArgumentsList argList = processArgumentList(callStatement.args, converter);
				return new CallExpression(start, end, null, name, argList);
			}	
			else
			{
				return new CallExpression(start, end, null, name, null);			
			}
		}
		
		public static CallExpression createCallExpression(ApplyExpression applyExpression, DltkConverter converter)
		{
			String name = applyExpression.root.toString();
			int start = getStartPos(applyExpression.root.location, converter);
			int end = getEndPos(applyExpression.root.location, converter);

			// TODO receiver???
			if (applyExpression.args.size() > 0)
			{
				CallArgumentsList argList = processArgumentList(applyExpression.args, converter);
				return new CallExpression(start, end, null, name, argList);
			}	
			else
			{
				return new CallExpression(start, end, null, name, CallArgumentsList.EMPTY);			
			}
		}
		
		
		public static VariableReference createVariableReference(String name, LexLocation location, DltkConverter converter){
			int start = getStartPos(location, converter);
			int end = getEndPos(location, converter);
			return new VariableReference(start, end, name);
		}
		
		public static VariableReference createVariableReference(VariableExpression varExp, LexLocation location, DltkConverter converter){
			int start = getStartPos(location, converter);
			int end = getEndPos(location, converter);
			
			if (varExp.getPreName() != null)
			{
				return new VariableReference(start, end, varExp.getPreName());
			}
			else
			{
				return new VariableReference(start, end, varExp.name.name);
			}
		}
		
		private static CallArgumentsList processArgumentList(ExpressionList args, DltkConverter converter)
		{
			int startPos = getStartPos(args.get(0).location, converter);
			int endPos = getEndPos(args.get(args.size()-1).location, converter);
			
			
			
			
			CallArgumentsList argList = new CallArgumentsList(startPos, endPos);
			
//			for(Expression exp : args)
//			{
//				
//			}
			
			
			
			return argList;
		}
		
		
		

		public static BooleanLiteral createBooleanLiteral(BooleanLiteralExpression exp, DltkConverter converter) {
			int start = getStartPos(exp.location, converter);
			int end = getEndPos(exp.location, converter);
			return new BooleanLiteral(start, end, exp.value.value);
		}

		public static NumericLiteral createNumericLiteral(IntegerLiteralExpression exp, DltkConverter converter) {
			int start = getStartPos(exp.location, converter);
			int end = getEndPos(exp.location, converter);
			return new NumericLiteral(start, end, exp.value.value);
		}
		
		private static int getStartPos(LexLocation loc, DltkConverter converter)
		{
			return converter.convert(loc.startLine, loc.startPos) - 1;
		}
		
		private static int getEndPos(LexLocation loc, DltkConverter converter)
		{
			return converter.convert(loc.endLine, loc.endPos);
		}

//		public static OvertureIfStatement createIfStatement(IfStatement statement, DltkConverter converter) {
//			return null;
//		}

		public static CallExpression createCallObject(CallObjectStatement statement, DltkConverter converter) {
			
			// receiver
			String varName = statement.designator.toString();
			int varStartPos = getStartPos(statement.designator.location, converter);
			int varEndPos = getEndPos(statement.designator.location, converter);
			VariableReference variableReference = new VariableReference(varStartPos, varEndPos, varName);
			
			// simpleReference 
			int simpleRefStartPos = getEndPos(statement.location, converter) + 1; // the dot  e.g. var.op()
			int simpleRefEndPos = simpleRefStartPos + statement.fieldname.length();
			SimpleReference simpleReference = new SimpleReference(simpleRefStartPos, simpleRefEndPos, statement.fieldname);
			
			
			return new CallExpression(varStartPos, simpleRefEndPos, variableReference, simpleReference , CallArgumentsList.EMPTY);
		}
		
		public static int getModifier(AccessSpecifier specifier) {
			try {
				int modifiers = 0;
				if(specifier.isStatic){
					modifiers |= TypeDeclaration.AccStatic;
				}
				if (specifier.access.name().equals("PRIVATE"))
				{
					modifiers |=  TypeDeclaration.AccPrivate;
				}
				if (specifier.access.name().equals("PROTECTED"))
				{
					modifiers |=  TypeDeclaration.AccProtected;
				}
				if (specifier.access.name().equals("PUBLIC"))
				{
					modifiers |=  TypeDeclaration.AccPublic;
				}
				return modifiers;
			} catch (Exception e) {
				System.out.println("Could not create field.. " + e.getMessage());
				return -666;
			}
		}
		
	
}
