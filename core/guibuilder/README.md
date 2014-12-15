# GUI Builder
- **Primary Contacts:**
  Luis Diogo Couto
- **Status:**
  Development

## Description

This module integrates an external tool for prototyping GUIs for VDM models.

This tool integrates entirely using the remote control features of the Overture
interpreter and a special control class called
"org.overture.guibuilder.GuiBuilderRemote". 

In order to use the tool, a jar that provides the GUI Builder is necessary.
That jar can be built by invoking `mvn assembly:single` on this module. Be sure
to grab the one named `custom-dependencies`.

## Known Issues:
VDM-SL models are not supported

## Contributors:
- Carlos Nunes wrote the original GUI builder
- Kenneth Lausdahl integrated it with the Overture build
- Luis Diogo Couto integrated it with the Overture tool itself

