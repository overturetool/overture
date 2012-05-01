package org.overture.ast.patterns.assistants;

import java.util.List;

import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.PBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overturetool.vdmj.lex.LexNameList;

public class PBindAssistantTC {

	public static List<PMultipleBind> getMultipleBindList(PBind bind) {
		
		switch (bind.kindPBind()) {
		case SET:
			return ASetBindAssistantTC.getMultipleBindList((ASetBind)bind);
		case TYPE:
			return ATypeBindAssistantTC.getMultipleBindList((ATypeBind)bind);
		
		}
		return null;
		
	}
	
	public static LexNameList getOldNames(PBind bind)
	{
		switch (bind.kindPBind()) {
		case SET:
			return ASetBindAssistantTC.getOldNames((ASetBind)bind);
		case TYPE:
			return ATypeBindAssistantTC.getOldNames((ATypeBind)bind);
		default:
			assert false : "Should not happen";
			return null;	
		}
		
	}

	
}
