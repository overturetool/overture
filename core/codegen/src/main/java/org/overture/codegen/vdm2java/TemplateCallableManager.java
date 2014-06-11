package org.overture.codegen.vdm2java;

import org.overture.codegen.merging.TemplateCallable;

public class TemplateCallableManager
{
	private static final String JAVA_FORMAT_KEY = "JavaFormat";
	private static final String IR_ANALYSIS_KEY = "IRAnalysis";
	private static final String TEMP_VAR = "TempVar";
	private static final String VALUE_SEMANTICS = "ValueSemantics";
	private static final String RECORD_CREATOR = "RecordCreator";
	
	public final static TemplateCallable[] constructTemplateCallables(
			Object javaFormat, Object irAnalysis, Object tempVarPrefixes,
			Object valueSemantics, Object recordCreator)
	{
		return new TemplateCallable[] {
				new TemplateCallable(JAVA_FORMAT_KEY, javaFormat),
				new TemplateCallable(IR_ANALYSIS_KEY, irAnalysis),
				new TemplateCallable(TEMP_VAR, tempVarPrefixes),
				new TemplateCallable(VALUE_SEMANTICS, valueSemantics),
				new TemplateCallable(RECORD_CREATOR, recordCreator)};
	}
}
