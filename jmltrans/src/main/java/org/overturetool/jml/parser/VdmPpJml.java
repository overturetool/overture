package org.overturetool.jml.parser;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.jmlspecs.jml4.compiler.DefaultCompilerExtension;
import org.jmlspecs.jml4.util.Logger;
import org.overturetool.jml.visitor.JmlVisitor;
import org.overturetool.jml.visitor.JmlPpVisitor;

public class VdmPpJml extends DefaultCompilerExtension {

	
	public static boolean DEBUG = false;
	
	public String name() {
		
		return "JMLtoVDM++Parser";
	}
	
	public void preCodeGeneration(Compiler compiler, CompilationUnitDeclaration unit) {
		
		if (DEBUG) {
			Logger.println(this + " - compiler.options.jmlEnabled:     "+compiler.options.jmlEnabled); //$NON-NLS-1$
			Logger.println(this + " - compiler.options.jmlDbcEnabled:  "+compiler.options.jmlDbcEnabled); //$NON-NLS-1$
			Logger.println(this + " - compiler.options.jmlThyEnabled:  "+compiler.options.jmlThyEnabled); //$NON-NLS-1$
		}
		if (compiler.options.jmlEnabled && compiler.options.jmlDbcEnabled && compiler.options.jmlThyEnabled) {
			this.compile(compiler, unit);
		}
		
	}
	
	private void compile(Compiler compiler, CompilationUnitDeclaration unit) {
		
		if (unit.compilationResult.hasSyntaxError
				|| unit.compilationResult.hasErrors()
				|| unit.hasErrors()) 
			return;
		
		// Copy and simplify the tree
			
		//VdmPpVisitor simpleVisitor = new VdmPpVisitor(); // need to build a new visitor. Example: JmlJava2SimpleVisitor
		ASTJMLVisitor simpleVisitor = new ASTJMLVisitor();
		unit.traverse(simpleVisitor, unit.scope);
		
		
		
	}
	
}
