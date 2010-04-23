package org.overture.ide.ui.templates;

import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.TemplateContextType;

public class VdmUniversalTemplateContextType extends TemplateContextType {

		
	public VdmUniversalTemplateContextType() {
		addResolver(new GlobalTemplateVariables.Cursor());
		addResolver(new GlobalTemplateVariables.WordSelection());
		addResolver(new GlobalTemplateVariables.LineSelection());
	}

	public VdmUniversalTemplateContextType(String id) {
		super(id);
		addResolver(new GlobalTemplateVariables.Cursor());
		addResolver(new GlobalTemplateVariables.WordSelection());
		addResolver(new GlobalTemplateVariables.LineSelection());
	}

	public VdmUniversalTemplateContextType(String id, String name) {
		super(id, name);
		addResolver(new GlobalTemplateVariables.Cursor());
		addResolver(new GlobalTemplateVariables.WordSelection());
		addResolver(new GlobalTemplateVariables.LineSelection());
	}
	
//	abstract public String getContextType();
	
//	 public static final String CONTEXT_TYPE = IVdmUiConstants.PLUGIN_ID
//			+ ".contextType.vdm";

}
