This fragment should have this filter defined in the manifest:

Eclipse-PlatformFilter: (& (osgi.ws=win32) (osgi.os=win32) )

but then tycho cannot compile it on windows