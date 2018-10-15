#/bin/bash
#

JAR=~/slepeweb-money-import.jar
LIBS=~/slepeweb-money-import_lib
DIST=~/slepeweb-money
MONEY=/media/george/Data/George/Kryptonite/home.MNY
SUNRIISE=/media/george/Windows/Users/gbutt/Desktop/sunriise2-misc-master
WORKING_MONEY=$DIST/home.MNY
WORKING_MDB=$DIST/home.MDB
FROM=""

while [[ $# -gt 0 ]]
do
case $1 in
	-d)
	FROM=$2
	echo "Time window is from $FROM"
	shift
	shift
	;;
esac
done

if [ ! -r $MONEY ]
then
	echo "*** Failed to locate MS Money file [$MONEY]"
	exit 1
fi

if [ ! -d $SUNRIISE ]
then
	echo "*** Failed to locate MS Money conversion app [$SUNRIISE]"
	exit 1
fi

if [ ! -r $JAR ]
then
	echo "*** Please export jar file [$JAR]"
	exit 1
fi

if [ ! -d $LIBS ]
then
	echo "*** Please export all dependant jars into $LIBS"
	exit 1
fi

if [ -e $WORKING_MONEY ]
then
	rm $WORKING_MONEY
	echo "- Deleted previous working copy of .mny file"
fi

if [ -e $WORKING_MDB ]
then
	rm $WORKING_MDB
	echo "- Deleted previous working copy of .mdb file"
fi

cp $MONEY $WORKING_MONEY
java -jar $SUNRIISE/out/sunriise-export-0.0.1-SNAPSHOT-exec.jar $WORKING_MONEY g1ga50ft $WORKING_MDB

if [ ! -r $WORKING_MDB ]
then
	echo "*** Failed to output MS Access Database [$WORKING_MDB]"
	exit 1
fi

if [ ! -d $DIST ]
then
	mkdir $DIST
fi

cd $DIST
jar xvf $JAR
#mv $DIST/resources/* $DIST

cd
java -cp $LIBS/*:$DIST:$DIST/resources com.slepeweb.money.MoneyImportManager $FROM