package org.overture.codegen.assistant;

import java.util.List;

import org.overture.codegen.cgast.SDeclCG;
import org.overture.codegen.cgast.declarations.AClassDeclCG;

public interface DeclStrategy<T extends SDeclCG>
{
	public String getAccess(T decl);
	
	public List<T> getDecls(AClassDeclCG classDecl);
}
