/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
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
