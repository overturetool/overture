package org.overture.ide.ui.internal.viewsupport;

import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.modules.ModuleImports;

public class ImportsContainer {

	
	private ModuleImports imports = null;
	private DefinitionList importDefs = null;
	
	public ImportsContainer(ModuleImports imports, DefinitionList importDefs){
		this.imports = imports;
		this.importDefs = importDefs;
	}
	
	public DefinitionList getImportDefs() {
		return importDefs;
	}
	
	public ModuleImports getImports() {
		return imports;
	}
	
}
