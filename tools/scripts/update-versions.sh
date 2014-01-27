#!/bin/sh

BASEDIR=`git rev-parse --show-toplevel`

if [ -z "$BASEDIR" ]; then
    echo "$0 must be used inside of a git repository"
fi

while [ "$#" -gt 0 ]; do
    case $1 in
        -v)
            VERBOSE=-t
            echo "*** Echoing update commands"
            shift
            ;;
        --to-dev)
            POSTSNAP=-SNAPSHOT
            POSTQUAL=.qualifier
            echo "*** Target is development version"
            shift
            ;;
        --from-dev)
            PRESNAP=-SNAPSHOT
            PREQUAL=.qualifier
            echo "*** Source is development version"
            shift
            ;;
        *)
            break
            ;;
    esac
done

if [ -z "$1" -o -z "$2" ]; then
    cat <<EOF
Usage: `basename $0` [-v] [--from-dev] [--to-dev] <from-version-string> <to-version-string>
  -v		Echo the sed commands as they update files.
  --from-dev	Append "-SNAPSHOT"/".qualifier" to the from version.
  --to-dev	Append "-SNAPSHOT"/".qualifier" to the to version
EOF
    exit 0
fi

echo Updating pom.xml files
find $BASEDIR -not -path "*/target/*" -name pom.xml | xargs $VERBOSE -n1 sed -i '' -e "s/$1$PRESNAP<!--Replaceable: Main Version-->/$2$POSTSNAP<!--Replaceable: Main Version-->/"

echo Updating MANIFEST.MF files
find $BASEDIR -not -path "*/target/*" -name MANIFEST.MF | xargs $VERBOSE -n1 sed -i '' -e "s/^Bundle-Version: $1$PREQUAL$/Bundle-Version: $2$POSTQUAL/"

echo Updating feature.xml files
find $BASEDIR -not -path "*/target/*" -name feature.xml | xargs $VERBOSE -n1 sed -i '' -e "s/$1$PREQUAL/$2$POSTQUAL/"

echo Updating `basename $BASEDIR/ide/product/*.product`
[ -n "$VERBOSE" ] && echo sed -i '' -e "s/$1$PREQUAL/$2$POSTQUAL/" $BASEDIR/ide/product/*.product
sed -i '' -e "s/$1$PREQUAL/$2$POSTQUAL/" $BASEDIR/ide/product/*.product

echo Done.

echo "*** Remember to check the git status diffs before committing, to\n*** ensure that nothing got changed that shouldn't have been."
