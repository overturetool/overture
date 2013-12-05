package org.overture.typechecker.assistant.module;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.modules.PImport;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class PImportAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public PImportAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static List<PDefinition> getDefinitions(PImport imp, AModuleModules from)
	{
		try
		{
			return imp.apply(af.getImportDefinitionFinder(),from);// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return null;
		}
//		if (imp instanceof AAllImport) {
//			return AAllImportAssistantTC.getDefinitions((AAllImport)imp,from);
//		} else if (imp instanceof ATypeImport) {
//			return ATypeImportAssistantTC.getDefinitions((ATypeImport)imp,from);
//		} else if (imp instanceof SValueImport) {
//			return SValueImportAssistantTC.getDefinitions((SValueImport)imp,from);
//		} else {
//			assert false : "PImport.getDefinitions should never hit this case";
//			return null;
//		}
	}
}
