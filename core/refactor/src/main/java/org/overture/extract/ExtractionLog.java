package org.overture.extract;

import java.util.HashSet;
import java.util.Set;

public class ExtractionLog {
	private static Set<Extraction> extractions = new HashSet<Extraction>();

	public static Set<Extraction> getExtractions(){
		return extractions;
	}
	
	public static void clearExtractions(){
		if(extractions != null){
			extractions.clear();
		}
	}
	
	public static void addExtraction(Extraction extract){
		if(extract != null){
			extractions.add(extract);
		}
	}
}
