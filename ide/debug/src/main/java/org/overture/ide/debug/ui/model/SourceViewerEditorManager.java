package org.overture.ide.debug.ui.model;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.overture.ide.debug.core.IDebugConstants;

public class SourceViewerEditorManager
{
	/**
	 * A handle to the unique Singleton instance.
	 */
	static private SourceViewerEditorManager _instance = null;
	static final Map<String,String> editors = new Hashtable<String,String>();

	/**
	 * @return The unique instance of this class.
	 */
	static public SourceViewerEditorManager getInstance()
	{
		if (null == _instance)
		{
			_instance = new SourceViewerEditorManager();
		}
		return _instance;
	}

	public SourceViewerEditorManager() {
		editors.putAll(getEditors());
	}



	private Map<String,String> getEditors()
	{
		Map<String,String> editors = new Hashtable<String,String>();

		IConfigurationElement[] config = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(IDebugConstants.EXTENSION_SOURCEVIEWER_EDITOR);

		for (IConfigurationElement e : config)
		{
			String editorId = e.getAttribute("EditorId");
			String contentTypeId =e.getAttribute("ContentTypeId");
			
			editors.put( contentTypeId,editorId);
		}

		return editors;
	}
	
	public Collection<String> getContentTypeIds()
	{
		return editors.keySet();
	}
	
	public String getEditorId(String contentTypeId)
	{
		return editors.get(contentTypeId);
	}

	
	
}
