package org.overture.ast.patterns.assistants;

import java.util.List;

import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.PBind;
import org.overture.ast.patterns.PMultipleBind;

public class PBindAssistant {

	public static List<PMultipleBind> getMultipleBindList(PBind bind) {
		
		switch (bind.kindPBind()) {
		case SET:
			return ASetBindAssistant.getMultipleBindList((ASetBind)bind);
		case TYPE:
			return ATypeBindAssistant.getMultipleBindList((ATypeBind)bind);
		
		}
		return null;
		
	}

	
}
