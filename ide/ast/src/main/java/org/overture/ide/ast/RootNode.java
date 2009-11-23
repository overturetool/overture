package org.overture.ide.ast;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.modules.ModuleList;

public class RootNode {
	private boolean checked=false;
	private Date checkedTime;
	@SuppressWarnings("unchecked")
	private List rootElementList;

	@SuppressWarnings("unchecked")
	public RootNode(List modules) {
		this.rootElementList = modules;
	}

	@SuppressWarnings("unchecked")
	public void setRootElementList(List rootElementList) {
		this.rootElementList = rootElementList;
	}

	@SuppressWarnings("unchecked")
	public List getRootElementList() {
		return rootElementList;
	}

	public void setCheckedTime(Date checkedTime) {
		this.checkedTime = checkedTime;
	}

	public Date getCheckedTime() {
		return checkedTime;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean isChecked() {
		return checked;
	}

	/***
	 * Updates the local definition list with a new list of Definitions if any definition exists the old definitions are replaced
	 * @param module the new definition
	 */
	@SuppressWarnings("unchecked")
	public void update(List modules) {
this.setChecked(false);
		if (this.rootElementList.size() != 0)
			for (Object module : modules) {
				if (module instanceof ClassDefinition)
					update((ClassDefinition) module);
				else if (module instanceof Module)
					update((Module) module);
			}
		else {
			this.rootElementList.addAll(modules);
		}

	}

	/***
	 * Updates the local list with a new Definition if it already exists the old one is replaced
	 * @param module the new definition
	 */
	@SuppressWarnings("unchecked")
	private void update(Module module) {
		Module existingModule = null;
		for (Object m : this.rootElementList) {
			if (m instanceof Module && ((Module) m).name.equals(module.name) && ((Module) m).name.location.file.getName().equals(module.name.location.file.getName()))
				existingModule = (Module) m;
		}

		if (existingModule != null)
			this.rootElementList.remove(existingModule);

		this.rootElementList.add(module);


	}

	/***
	 * Updates the local list with a new Definition if it already exists the old one is replaced
	 * @param module the new definition
	 */
	@SuppressWarnings("unchecked")
	private void update(ClassDefinition module) {
		ClassDefinition existingModule = null;
		for (Object m : this.rootElementList) {
			if (m instanceof ClassDefinition
					&& ((ClassDefinition) m).name.equals(module.name))
				existingModule = (ClassDefinition) m;
		}

		if (existingModule != null)
			this.rootElementList.remove(existingModule);

		this.rootElementList.add(module);

	}

	/***
	 * Check if any definition in the list has the file as source location
	 * @param file The file which should be tested against all definitions in the list
	 * @return true if the file has a definition in the list
	 */
	public boolean hasFile(File file) {
		for(Object o : rootElementList)
		{
			if(o instanceof Module && ((Module)o).name.location.file.equals(file))
				return true;
			else if(o instanceof ClassDefinition && ((ClassDefinition)o).name.location.file.equals(file))
				return true;
				
		}
		return false;
	}
	
	public ModuleList getModuleList() throws NotAllowedException
	{
		ModuleList modules = new ModuleList();
		for (Object definition : rootElementList) {
			if (definition instanceof Module)
				modules.add((Module) definition);
			else
				throw new NotAllowedException("Other definition than Module is found: "+ definition.getClass().getName());
		}
		return modules;
	}
	
	
	public ClassList getClassList() throws NotAllowedException
	{
		ClassList classes = new ClassList();
		for (Object definition : rootElementList) {
			if (definition instanceof ClassDefinition)
				classes.add((ClassDefinition) definition);
			else
				throw new NotAllowedException("Other definition than ClassDefinition is found: "+ definition.getClass().getName());
		}
		return classes;
	}
}
