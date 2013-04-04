package org.overture.typechecker.assistant.module;


import java.util.List;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.modules.AAllImport;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.modules.ATypeImport;
import org.overture.ast.modules.PImport;
import org.overture.ast.modules.SValueImport;

public class PImportAssistantTC {

	public static List<PDefinition> getDefinitions(PImport imp,
			AModuleModules from) {
		switch (imp.kindPImport()) {
		case AAllImport.kindPImport:
			return AAllImportAssistantTC.getDefinitions((AAllImport)imp,from);		
		case ATypeImport.kindPImport:
			return ATypeImportAssistantTC.getDefinitions((ATypeImport)imp,from);
		case SValueImport.kindPImport:
			return SValueImportAssistantTC.getDefinitions((SValueImport)imp,from);
		default:
			assert false : "PImport.getDefinitions should never hit this case";
			return null;			
		}
	}

}
