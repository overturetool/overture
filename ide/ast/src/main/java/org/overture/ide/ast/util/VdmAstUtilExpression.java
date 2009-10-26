package org.overture.ide.ast.util;

import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.expressions.CallArgumentsList;
import org.eclipse.dltk.ast.expressions.CallExpression;
import org.eclipse.dltk.ast.references.VariableReference;
import org.overture.ide.ast.dltk.DltkConverter;
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
import org.overturetool.vdmj.expressions.ExpressionList;
import org.overturetool.vdmj.expressions.FieldExpression;
import org.overturetool.vdmj.expressions.FieldNumberExpression;
import org.overturetool.vdmj.expressions.ForAllExpression;
import org.overturetool.vdmj.expressions.FuncInstantiationExpression;
import org.overturetool.vdmj.expressions.HistoryExpression;
import org.overturetool.vdmj.expressions.IfExpression;
import org.overturetool.vdmj.expressions.IntegerLiteralExpression;
import org.overturetool.vdmj.expressions.IotaExpression;
import org.overturetool.vdmj.expressions.IsExpression;
import org.overturetool.vdmj.expressions.IsOfBaseClassExpression;
import org.overturetool.vdmj.expressions.IsOfClassExpression;
import org.overturetool.vdmj.expressions.LambdaExpression;
import org.overturetool.vdmj.expressions.LetBeStExpression;
import org.overturetool.vdmj.expressions.LetDefExpression;
import org.overturetool.vdmj.expressions.MapExpression;
import org.overturetool.vdmj.expressions.MkBasicExpression;
import org.overturetool.vdmj.expressions.MkTypeExpression;
import org.overturetool.vdmj.expressions.MuExpression;
import org.overturetool.vdmj.expressions.NewExpression;
import org.overturetool.vdmj.expressions.NilExpression;
import org.overturetool.vdmj.expressions.NotYetSpecifiedExpression;
import org.overturetool.vdmj.expressions.PostOpExpression;
import org.overturetool.vdmj.expressions.PreExpression;
import org.overturetool.vdmj.expressions.PreOpExpression;
import org.overturetool.vdmj.expressions.QuoteLiteralExpression;
import org.overturetool.vdmj.expressions.RealLiteralExpression;
import org.overturetool.vdmj.expressions.SameBaseClassExpression;
import org.overturetool.vdmj.expressions.SelfExpression;
import org.overturetool.vdmj.expressions.SeqExpression;
import org.overturetool.vdmj.expressions.SetExpression;
import org.overturetool.vdmj.expressions.StateInitExpression;
import org.overturetool.vdmj.expressions.StringLiteralExpression;
import org.overturetool.vdmj.expressions.SubclassResponsibilityExpression;
import org.overturetool.vdmj.expressions.SubseqExpression;
import org.overturetool.vdmj.expressions.ThreadIdExpression;
import org.overturetool.vdmj.expressions.TimeExpression;
import org.overturetool.vdmj.expressions.TupleExpression;
import org.overturetool.vdmj.expressions.UnaryExpression;
import org.overturetool.vdmj.expressions.UndefinedExpression;
import org.overturetool.vdmj.expressions.VariableExpression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.statements.CallStatement;

public class VdmAstUtilExpression {

	public static void addExpression(Expression expression, MethodDeclaration method,
			DltkConverter converter) {

		if (expression instanceof ApplyExpression) {
			ApplyExpression appExpr = (ApplyExpression) expression;
			CallExpression exp = addApplyExpression(appExpr,
					converter);
			method.getBody().addStatement(exp);
		}

		if (expression instanceof BinaryExpression) {

		}

		if (expression instanceof BooleanLiteralExpression) {

		}

		if (expression instanceof BreakpointExpression) {

		}

		if (expression instanceof CasesExpression) {

		}

		if (expression instanceof CharLiteralExpression) {

		}

		if (expression instanceof ElseIfExpression) {

		}

		if (expression instanceof Exists1Expression) {

		}

		if (expression instanceof ExistsExpression) {

		}

		if (expression instanceof FieldExpression) {

		}

		if (expression instanceof FieldNumberExpression) {

		}

		if (expression instanceof ForAllExpression) {

		}

		if (expression instanceof FuncInstantiationExpression) {

		}

		if (expression instanceof HistoryExpression) {

		}

		if (expression instanceof IfExpression) {

		}

		if (expression instanceof IntegerLiteralExpression) {

		}

		if (expression instanceof IotaExpression) {

		}

		if (expression instanceof IsExpression) {

		}

		if (expression instanceof IsOfBaseClassExpression) {

		}

		if (expression instanceof IsOfClassExpression) {

		}

		if (expression instanceof LambdaExpression) {

		}

		if (expression instanceof LetBeStExpression) {

		}

		if (expression instanceof LetDefExpression) {

		}

		if (expression instanceof MapExpression) {

		}

		if (expression instanceof MkBasicExpression) {

		}

		if (expression instanceof MkTypeExpression) {

		}

		if (expression instanceof MuExpression) {

		}

		if (expression instanceof NewExpression) {

		}

		if (expression instanceof NilExpression) {

		}

		if (expression instanceof NotYetSpecifiedExpression) {

		}

		if (expression instanceof PostOpExpression) {

		}

		if (expression instanceof PreExpression) {

		}

		if (expression instanceof PreOpExpression) {

		}

		if (expression instanceof QuoteLiteralExpression) {

		}

		if (expression instanceof RealLiteralExpression) {

		}

		if (expression instanceof SameBaseClassExpression) {

		}

		if (expression instanceof SelfExpression) {

		}

		if (expression instanceof SeqExpression) {

		}

		if (expression instanceof SetExpression) {

		}

		if (expression instanceof StateInitExpression) {

		}

		if (expression instanceof StringLiteralExpression) {

		}

		if (expression instanceof SubclassResponsibilityExpression) {

		}

		if (expression instanceof SubseqExpression) {

		}

		if (expression instanceof ThreadIdExpression) {

		}

		if (expression instanceof TimeExpression) {

		}

		if (expression instanceof TupleExpression) {

		}

		if (expression instanceof UnaryExpression) {

		}

		if (expression instanceof UndefinedExpression) {

		}

		if (expression instanceof VariableExpression) {
			VariableExpression varExp = (VariableExpression) expression;
			VariableReference varRef = VdmAstUtil.createVariableReference(varExp, varExp.location, converter);
			method.getBody().addStatement(varRef);
		}

	}
	
	public static Expression addExpression(Expression expression,CallArgumentsList list,
			DltkConverter converter) {

		if (expression instanceof ApplyExpression) {
			ApplyExpression appExpr = (ApplyExpression) expression;
			CallExpression exp = addApplyExpression(appExpr,
					converter);
			list.getChilds().add(exp);
		}

		if (expression instanceof BinaryExpression) {

		}

		if (expression instanceof BooleanLiteralExpression) {

		}

		if (expression instanceof BreakpointExpression) {

		}

		if (expression instanceof CasesExpression) {

		}

		if (expression instanceof CharLiteralExpression) {

		}

		if (expression instanceof ElseIfExpression) {

		}

		if (expression instanceof Exists1Expression) {

		}

		if (expression instanceof ExistsExpression) {

		}

		if (expression instanceof FieldExpression) {

		}

		if (expression instanceof FieldNumberExpression) {

		}

		if (expression instanceof ForAllExpression) {

		}

		if (expression instanceof FuncInstantiationExpression) {

		}

		if (expression instanceof HistoryExpression) {

		}

		if (expression instanceof IfExpression) {

		}

		if (expression instanceof IntegerLiteralExpression) {

		}

		if (expression instanceof IotaExpression) {

		}

		if (expression instanceof IsExpression) {

		}

		if (expression instanceof IsOfBaseClassExpression) {

		}

		if (expression instanceof IsOfClassExpression) {

		}

		if (expression instanceof LambdaExpression) {

		}

		if (expression instanceof LetBeStExpression) {

		}

		if (expression instanceof LetDefExpression) {

		}

		if (expression instanceof MapExpression) {

		}

		if (expression instanceof MkBasicExpression) {

		}

		if (expression instanceof MkTypeExpression) {

		}

		if (expression instanceof MuExpression) {

		}

		if (expression instanceof NewExpression) {

		}

		if (expression instanceof NilExpression) {

		}

		if (expression instanceof NotYetSpecifiedExpression) {

		}

		if (expression instanceof PostOpExpression) {

		}

		if (expression instanceof PreExpression) {

		}

		if (expression instanceof PreOpExpression) {

		}

		if (expression instanceof QuoteLiteralExpression) {

		}

		if (expression instanceof RealLiteralExpression) {

		}

		if (expression instanceof SameBaseClassExpression) {

		}

		if (expression instanceof SelfExpression) {

		}

		if (expression instanceof SeqExpression) {

		}

		if (expression instanceof SetExpression) {

		}

		if (expression instanceof StateInitExpression) {

		}

		if (expression instanceof StringLiteralExpression) {

		}

		if (expression instanceof SubclassResponsibilityExpression) {

		}

		if (expression instanceof SubseqExpression) {

		}

		if (expression instanceof ThreadIdExpression) {

		}

		if (expression instanceof TimeExpression) {

		}

		if (expression instanceof TupleExpression) {

		}

		if (expression instanceof UnaryExpression) {

		}

		if (expression instanceof UndefinedExpression) {

		}

		if (expression instanceof VariableExpression) {
			VariableExpression varExp = (VariableExpression) expression;
			VariableReference varRef = VdmAstUtil.createVariableReference(varExp, varExp.location, converter);
			list.getChilds().add(varRef);
		}
		return null;

	}
	
	public static CallExpression addCallExpression(CallStatement callStatement,
			DltkConverter converter) {
		String name = callStatement.name.name;
		int start = getStartPos(callStatement.location, converter);
		int end = getEndPos(callStatement.location, converter);
		// TODO receiver???
		if (callStatement.args.size() > 0) {
			CallArgumentsList argList = processArgumentList(callStatement.args,
					converter);
			return new CallExpression(start, end, null, name, argList);
		} else {
			return new CallExpression(start, end, null, name, null);
		}
	}

	public static CallExpression addApplyExpression(
			ApplyExpression applyExpression, DltkConverter converter) {
		String name = applyExpression.root.toString();
		int start = getStartPos(applyExpression.root.location, converter);
		int end = getEndPos(applyExpression.root.location, converter);

		// TODO receiver???
		if (applyExpression.args.size() > 0) {
			CallArgumentsList argList = processArgumentList(
					applyExpression.args, converter);
			return new CallExpression(start, end, null, name, argList);
		} else {
			return new CallExpression(start, end, null, name,
					CallArgumentsList.EMPTY);
		}
	}

	private static CallArgumentsList processArgumentList(ExpressionList args,
			DltkConverter converter) {
		int startPos = getStartPos(args.get(0).location, converter);
		int endPos = getEndPos(args.get(args.size() - 1).location, converter);

		CallArgumentsList argList = new CallArgumentsList(startPos, endPos);

		for (Expression exp : args) {
			addExpression(exp,argList,converter);
		}

		return argList;
	}
	
	private static int getStartPos(LexLocation loc, DltkConverter converter) {
		return converter.convert(loc.startLine, loc.startPos) - 1;
	}

	private static int getEndPos(LexLocation loc, DltkConverter converter) {
		return converter.convert(loc.endLine, loc.endPos);
	}
	
}
