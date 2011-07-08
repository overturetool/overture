package org.overture.ast.patterns.assistants;

import java.util.List;

import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.PBind;
import org.overture.ast.patterns.PMultipleBind;

public class PBindAssistant {

	public static List<PMultipleBind> getMultipleBind(PBind bind) {
		
		switch (bind.kindPBind()) {
		case SET:
			if(bind instanceof ASetBind)
			{
				return ASetBindAssistant.getMultipleBind((ASetBind)bind);
			}
			break;
		case TYPE:
			break;
		
		}
		return null;
		
	}

	
}
