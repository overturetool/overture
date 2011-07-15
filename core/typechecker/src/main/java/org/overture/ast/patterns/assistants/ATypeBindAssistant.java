package org.overture.ast.patterns.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.typecheck.TypeCheckInfo;

public class ATypeBindAssistant {

	public static void typeResolve(ATypeBind typebind,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		typebind.setType(PTypeAssistant.typeResolve(typebind.getType(), null, rootVisitor, question));
		
	}

	public static List<PMultipleBind> getMultipleBindList(ATypeBind bind) {
		List<PPattern> plist = new Vector<PPattern>();
		plist.add(bind.getPattern());
		List<PMultipleBind> mblist = new Vector<PMultipleBind>();
		mblist.add(new ATypeMultipleBind(bind.getLocation(),plist, bind.getType()));
		return mblist;
	}

}
