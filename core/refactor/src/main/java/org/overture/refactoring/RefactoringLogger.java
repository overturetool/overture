package org.overture.refactoring;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RefactoringLogger<T> {

	private Set<T> set = new HashSet<T>();
	private Set<String> warnings = new HashSet<String>();
	
    public void add(T t) { 
    	this.set.add(t); 
    }
    
    public Set<T> get() { 
    	return set; 
    }
    
    public void clear(){ 
    	set.clear(); 
    	warnings.clear(); 
    }
    
	public Set<String> getWarnings() {
		return warnings;
	}
	
	public void addWarning(String warning) {
		this.warnings.add(warning);
	}
}
