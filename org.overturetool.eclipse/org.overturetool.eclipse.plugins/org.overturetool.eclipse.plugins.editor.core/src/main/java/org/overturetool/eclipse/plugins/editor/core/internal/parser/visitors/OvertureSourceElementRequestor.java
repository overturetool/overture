package org.overturetool.eclipse.plugins.editor.core.internal.parser.visitors;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.PositionInformation;
import org.eclipse.dltk.ast.expressions.Expression;
import org.eclipse.dltk.ast.statements.Statement;
import org.eclipse.dltk.compiler.ISourceElementRequestor;
import org.eclipse.dltk.compiler.SourceElementRequestVisitor;
import org.eclipse.dltk.compiler.ISourceElementRequestor.FieldInfo;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.OvertureCallStatement;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.VDMFieldDeclaration;
public class OvertureSourceElementRequestor extends SourceElementRequestVisitor {
	private static class TypeField {
		private String fName;

		private String fInitValue;

		private PositionInformation fPos;

		private Expression fExpression;

		private ASTNode fToNode;

		private ASTNode declaredIn; // The node where the declaration was found
									// (should be either class or method node)
		TypeField(String name, String initValue, PositionInformation pos,
				Expression expression, ASTNode toNode, ASTNode declaredIn) {

			this.fName = name;
			this.fInitValue = initValue;
			this.fPos = pos;
			this.fExpression = expression;
			this.fToNode = toNode;
			this.declaredIn = declaredIn;
		}

		String getName() {

			return this.fName;
		}

		String getInitValue() {

			return this.fInitValue;
		}

		PositionInformation getPos() {

			return this.fPos;
		}

		Expression getExpression() {

			return this.fExpression;
		}

		ASTNode getToNode() {

			return this.fToNode;
		}

		public boolean equals(Object obj) {

			if (obj instanceof TypeField) {
				TypeField second = (TypeField) obj;
				return second.fName.equals(this.fName)
						&& second.fToNode.equals(this.fToNode);
			}
			return super.equals(obj);
		}

		public String toString() {

			return this.fName;
		}

		public ASTNode getDeclaredIn() {
			return declaredIn;
		}

	}

//	private static String ANONYMOUS_LAMBDA_FORM_MARKER = "<anonymous>";
//	// Used to prehold fields if adding in methods.
//	private List fNotAddedFields = new ArrayList();
//	
//	private String lastLambdaFormName = ANONYMOUS_LAMBDA_FORM_MARKER;

	/**
	 * Used to depermine duplicate names.
	 */
	private Map fTypeVariables = new HashMap();

	public OvertureSourceElementRequestor(ISourceElementRequestor requestor) {
		super(requestor);
	}

	/**
//	 * Used to create Call value in python syntax.
//	 */
//	protected String makeLanguageDependentValue(Expression value) {
//		return null; //TODO
//	}
//
//	
//	private void onVisitStaticVariableAssignment(SimpleReference var, Statement val)
//	{
//		// for module static of class static variables.
//		
//		if (canAddVariables((ASTNode) this.fNodes.peek(), var.getName())) {
//			ISourceElementRequestor.FieldInfo info = new ISourceElementRequestor.FieldInfo();
//			info.modifiers = Modifiers.AccStatic;
//			info.name = var.getName();
//			info.nameSourceEnd = var.sourceEnd() - 1;
//			info.nameSourceStart = var.sourceStart();
//			info.declarationStart = var.sourceStart();
//			this.fRequestor.enterField(info);
//			if (val != null) {
//				this.fRequestor.exitField(val.sourceEnd() - 1);
//			} else {
//				this.fRequestor.exitField(var.sourceEnd() - 1);
//			}
//		}
//	}
//
//	public boolean visit(Expression expression) throws Exception {
//
//		return false; // TODO 
//	}
//
//	public boolean endvisit(Expression expression) throws Exception {
//		return true;
//	}
//
//	protected void onEndVisitMethod(MethodDeclaration method) {
//
//	}

	public boolean visit(Statement statement) throws Exception {
		if (statement instanceof VDMFieldDeclaration)
		{   
			return addField((VDMFieldDeclaration) statement);
		}
		return false;//TODO
	}
	

//	private boolean canAddVariables(ASTNode type, String name) {
//
//		if (this.fTypeVariables.containsKey(type)) {
//			List variables = (List) this.fTypeVariables.get(type);
//			if (variables.contains(name)) {
//				return false;
//			}
//			variables.add(name);
//			return true;
//		} else {
//			List variables = new ArrayList();
//			variables.add(name);
//			this.fTypeVariables.put(type, variables);
//			return true;
//		}
//	}

	public boolean endvisit(Statement s) throws Exception {
		if (s instanceof VDMFieldDeclaration)
		{   
			return endField((VDMFieldDeclaration) s);
		}
		return true;
	}

//	public boolean visit(MethodDeclaration method) throws Exception {
//		this.fNodes.push(method);
//		List/* < Argument > */args = method.getArguments();
//		String[] parameter = new String[args.size()];
//		for (int a = 0; a < args.size(); a++) {
//			Argument arg = (Argument) args.get(a);
//			parameter[a] = arg.getName();
//		}
//
//		ISourceElementRequestor.MethodInfo mi = new ISourceElementRequestor.MethodInfo();
//		mi.parameterNames = parameter;
//		mi.name = method.getName();
//		mi.modifiers = method.getModifiers();
//		mi.nameSourceStart = method.getNameStart();
//		mi.nameSourceEnd = method.getNameEnd() - 1;
//		mi.declarationStart = method.sourceStart();
//
//		this.fRequestor.enterMethodRemoveSame(mi);
//
//		this.fInMethod = true;
//		this.fCurrentMethod = method;
//		return true;
//	}
	
	
	private boolean addField(VDMFieldDeclaration statement)
	{
		try {
			FieldInfo fieldInfo = new ISourceElementRequestor.FieldInfo();
			fieldInfo.name = statement.getName();
			fieldInfo.declarationStart = statement.sourceStart();
			fieldInfo.nameSourceStart = statement.getNameStart();
			fieldInfo.nameSourceEnd = statement.getNameEnd();
			fieldInfo.modifiers = statement.getModifiers();
			
			fRequestor.enterField(fieldInfo);

			return true;
			
		} catch (Exception e) {
			System.err.println("error: " + e.getMessage());
			return false;
		}
	}
	
	private boolean endField(VDMFieldDeclaration statement){
	
		fRequestor.exitField(statement.sourceEnd());
		return false;
	}
	
	
	
}
