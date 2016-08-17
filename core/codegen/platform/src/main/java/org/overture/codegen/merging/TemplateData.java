package org.overture.codegen.merging;

/**
 * Used by {@link TemplateManager} to store information about where to look up templates.
 * 
 * @author peter
 */
public class TemplateData
{
	/**
	 * The reference loader used to load the template
	 */
	private Class<?> templateLoaderRef;

	/**
	 * The path to the template, e.g. path/to/Node.vm
	 */
	private String templatePath;

	public TemplateData(Class<?> templateLoaderRef, String templatePath)
	{
		super();
		this.templateLoaderRef = templateLoaderRef;
		this.templatePath = templatePath;
	}

	public Class<?> getTemplateLoaderRef()
	{
		return templateLoaderRef;
	}

	public String getTemplatePath()
	{
		return templatePath;
	}

	@Override
	public String toString()
	{
		return "TemplateData [templateLoaderRef =" + templateLoaderRef
				+ ", templatePath =" + templatePath + "]";
	}
}
