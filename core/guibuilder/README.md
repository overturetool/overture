# GUI Builder
- **Primary Contacts:**
  Luis Diogo Couto
- **Status:**
  Maintenance

## Description

This module integrates an external tool for prototyping GUIs for VDM models.
The tool primarily uses the remote control features of the Overture interpreter
and in combination with a special control class called
`org.overture.guibuilder.GuiBuilderRemote`. 

In order to use the tool, a jar that provides the GUI Builder is necessary.
That jar can be built by invoking `mvn assembly:single` on this module. Be sure
to grab the one named `custom-dependencies`.

For more information about the original GUI Builder tool see: 
Nunes, C., & Paiva, A. (2011, October). Automatic Generation of Graphical User
Interfaces From VDM++ Specifications. In ICSEA 2011, The Sixth International
Conference on Software Engineering Advances.

## Known Issues:
VDM-SL models are not supported

## Contributors:
- Carlos Nunes wrote the original GUI builder
- Kenneth Lausdahl integrated it with the Overture build
- Luis Diogo Couto integrated it with the Overture tool itself

