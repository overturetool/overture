package org.overture.ide.ast;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;

public interface IAstManager
{
	/**
	 * Set a new parsed AST
	 * 
	 * @param project
	 *            The project which was parsed
	 * @param nature
	 *            The nature used when parsing the project
	 * @param classList
	 *            A new class list or module list
	 */
	void updateAst(IProject project, String nature, List data);

	

//	/**
//	 * Get a DLTK ModuleDeclaration from a specific AST
//	 * 
//	 * @param project
//	 *            The project of the desired AST
//	 * @param nature
//	 *            The nature of the project
//	 * @return A DLTK ModuleDeclaration of the selected AST
//	 */
//	ModuleDeclaration getAstModuleDeclaration(IProject project, String nature);

	/**
	 * Set the new AST nodes in the ast for the current project and nature
	 * 
	 * @param project
	 *            The project of the AST component
	 * @param nature
	 *            The nature used when parsing
	 * @param fileName
	 *            The file which is parsed
	 * @param source
	 *            The source of the file
	 * @param modules
	 *            The modules constructed by the parser
	 * @return Returns a ModuleDeclaration of the AST parsed by the parser
	 */
	ModuleDeclaration addAstModuleDeclaration(IProject project, String nature,
			char[] fileName, char[] source, List modules);
	
	

	/**
	 * Get the AST of a project from its nature
	 * @param project The project to select
	 * @param nature The nature if filter the AST
	 * @return The existing AST for the current project + nature
	 */
	Object getAstList(IProject project, String nature);

	/**
	 * Get the RootNode from a project and the corresponding nature
	 * @param project The project to select
	 * @param nature The nature if filter the AST
	 * @return The rootnode for the current project + nature
	 */
	RootNode getRootNode(IProject project, String nature);
	
	void setAstAsTypeChecked(IProject project, String nature);
	
}
