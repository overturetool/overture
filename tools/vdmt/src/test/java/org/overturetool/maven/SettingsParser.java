package org.overturetool.maven;

import java.io.FileReader;
import java.io.IOException;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;



public class SettingsParser extends DefaultHandler
{
	boolean inSettings;
	boolean inProfiles;
	boolean inProfile;
	boolean inId;
	boolean inActivation;
	boolean inActiveByDefault;
	boolean inPropeties;
	String property = null;

	public Stack<Profile> GetProfiles()
	{
		return profiles;
	}

	Stack<Profile> profiles = new Stack<Profile>();

	public void Parse(String file) throws SAXException, IOException
	{
		XMLReader xr = XMLReaderFactory.createXMLReader();

		xr.setContentHandler(this);
		xr.setErrorHandler(this);

		FileReader r = new FileReader(file);
		xr.parse(new InputSource(r));

	}

	public void startElement(String uri, String localName, String qName,
			Attributes attribs)
	{
		if (localName.equals("settings"))
			inSettings = true;
		if (inSettings && localName.equals("profiles"))
			inProfiles = true;
		if (inProfiles && localName.equals("profile"))
		{
			profiles.add(new Profile());
			inProfile = true;
		}
		if (profiles.size() > 0)
		{
			if (inProfile && localName.equals("id")
					&& profiles.lastElement().getId() == null)
				inId = true;
			if (inProfile && localName.equals("activation"))
				inActivation = true;
			if (inActivation && localName.equals("activeByDefault"))
				inActiveByDefault = true;
			if (inPropeties)
				property = localName;
			if (inProfile && localName.equals("properties"))
				inPropeties = true;

		}

	}

	public void endElement(String uri, String localName, String qName)
	{
		if (inActivation && localName.equals("activeByDefault"))
			inActiveByDefault = false;
		if (inProfile && localName.equals("activation"))
			inActivation = false;
		if (inProfile && localName.equals("properties"))
			inPropeties = false;
		if (localName.equals("id"))
			inId = false;
		if (localName.equals("profiles"))
			inProfiles = false;
		if (localName.equals("profile"))
			inProfile = false;
		if (localName.equals("settings"))
			inSettings = false;
	}

	public void characters(char[] data, int start, int length)
	{
		String value = new StringBuffer().append(data, start, length).toString().trim();
		if (inProfile && inId)
			profiles.lastElement().setId(value);
		if (inActiveByDefault)
			profiles.lastElement().setIsActive(Boolean.parseBoolean(value));
		if (property != null)
		{
			profiles.lastElement().addProperty(property, value);
			property = null;
		}
	}

}
