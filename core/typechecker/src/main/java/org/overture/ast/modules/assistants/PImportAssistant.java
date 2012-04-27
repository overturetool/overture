package org.overture.ast.modules.assistants;


import java.util.List;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.modules.AAllImport;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.modules.ATypeImport;
import org.overture.ast.modules.PImport;
import org.overture.ast.modules.SValueImport;

public class PImportAssistant {

	public static List<PDefinition> getDefinitions(PImport imp,
			AModuleModules from) {
		switch (imp.kindPImport()) {
		case ALL:
			return AAllImportAssistant.getDefinitions((AAllImport)imp,from);		
		case TYPE:
			return ATypeImportAssistant.getDefinitions((ATypeImport)imp,from);
		case VALUE:
			return SValueImportAssistant.getDefinitions((SValueImport)imp,from);
		default:
			assert false : "PImport.getDefinitions should never hit this case";
			return null;			
		}
	}

}
