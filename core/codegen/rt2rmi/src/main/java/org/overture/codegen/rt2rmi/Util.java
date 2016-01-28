package org.overture.codegen.rt2rmi;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.APublicAccess;
import org.overture.ast.definitions.PDefinition;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.utils.GeneratedModule;

/*
 * This is a utility class in order to 
 * encapsulate reusable methods
 */

public class Util {

	// Get files from their whole path names
	public static List<File> getFilesFromPaths(String[] args) {
		List<File> files = new LinkedList<File>();

		for (int i = 0; i < args.length; i++) {
			String fileName = args[i];
			File file = new File(fileName);
			files.add(file);
		}
		return files;
	}
	
	// Get classes
	public static List<ADefaultClassDeclCG> getClasses(
			List<GeneratedModule> generatedModules) {
		List<ADefaultClassDeclCG> irClasses = new LinkedList<ADefaultClassDeclCG>();
		for (GeneratedModule module : generatedModules) {
//			SDeclCG irDecl = module.getIrDecl();
			
			INode irDecl = module.getIrNode();
			
			if (irDecl instanceof ADefaultClassDeclCG) {
				irClasses.add((ADefaultClassDeclCG) irDecl);
			}
		}

		return irClasses;
	}
	
	// Get public operations
	public static List<AExplicitOperationDefinition> getPublicOperations(List<PDefinition> defs)
	{
		List<AExplicitOperationDefinition> publicOperations = new LinkedList<AExplicitOperationDefinition>();
		for(PDefinition def : defs)
		{
			if(def instanceof AExplicitOperationDefinition)
			{
				AExplicitOperationDefinition op = (AExplicitOperationDefinition) def;
				
				if(op.getAccess().getAccess() instanceof APublicAccess)
				{
					publicOperations.add(op.clone());
				}
			}
		}
		
		return publicOperations;
	}
}
