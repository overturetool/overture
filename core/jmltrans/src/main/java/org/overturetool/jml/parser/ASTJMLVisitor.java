package org.overturetool.jml.parser;

import java.util.Stack;
import java.util.Vector;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.jmlspecs.jml4.ast.JmlCompilationUnitDeclaration;
import org.jmlspecs.jml4.fspv.Fspv;
import org.jmlspecs.jml4.util.Logger;

public class ASTJMLVisitor extends ASTVisitor {

	private Stack stack = new Stack();
	private Vector methods = new Vector();
	private Vector types = new Vector();
	JmlCompilationUnitDeclaration result;
	
	public boolean visit(JmlCompilationUnitDeclaration unit, CompilationUnitScope scope) {

		System.out.println("VISITING Compilation unit");
		
		return true;
	}
	
	public void endVisit(JmlCompilationUnitDeclaration unit, CompilationUnitScope scope) {
		
		System.out.println("endVisit method");

	}
	
	public boolean visit(CompilationUnitDeclaration unit, CompilationUnitScope scope) {
		
		System.out.println("VISITING Compilation unit");
		if(Fspv.DEBUG) {
			Logger.println(this + " - visit - CompilationUnitDeclaration.getFileName() : " + String.valueOf(unit.getFileName())); //$NON-NLS-1$
		}
		return true;
	}
	public void endVisit(CompilationUnitDeclaration unit, CompilationUnitScope scope) {
		System.out.println("endVisit method");
		if(Fspv.DEBUG) {
			Logger.println(this + " - endVisit - CompilationUnitDeclaration.getFileName() : " + String.valueOf(unit.getFileName())); //$NON-NLS-1$
		}
	}
	
}
