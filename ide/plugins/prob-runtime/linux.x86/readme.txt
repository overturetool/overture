This fragment should have this filter defined in the manifest:

Eclipse-PlatformFilter: (& (osgi.os=macosx) (osgi.arch=x86_64) )

but then tycho cannot compile it on windows