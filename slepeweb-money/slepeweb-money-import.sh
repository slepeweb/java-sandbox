#/bin/bash
#

# The directory containing the data import application files
INSTALL=/home/george/money

# The location of the .MNY file
MONEY=/media/george/Data/George/Kryptonite/home.MNY

# The location of the class files to be jar'ed
GIT_REPOS=/home/george/git-repos

# All other locations are relative to the money installation, and assume  the following file structure:
# INSTALL
# -- jars (symbolic link to workspace folder containing supporting jar files, 
# --   eg. /home/george/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp1/wtpwebapps/slepeweb-money/WEB-INF/lib
# -- tomcat-jars (some services refer to HttpServletRequest, etc)
# -- dist
# ---- slepeweb-money-import.jar (the runnable jar file that executes the data import)
# ---- home.MNY (a working copy of the input data)
# ---- home.MDB (the .MDB conversion of .MNY)
# -- sunriise-export-0.0.1-SNAPSHOT-exec.jar (the file conversion app)
# -- slepeweb-money-import.sh (this bash script)
# -- slepeweb-money-build.xml (ant script to produce the jar file and supporting libraries)


JARS=$INSTALL/jars
TOMCAT_JARS=$INSTALL/tomcat-jars
DIST=$INSTALL/dist
JAR=$DIST/slepeweb-money-import.jar
WORKING_MONEY=$DIST/home.MNY
WORKING_MDB=$DIST/home.MDB
FROM=""

while [[ $# -gt 0 ]]
do
case $1 in
-from)
	FROM=$2
	echo "Time window is from $FROM"
	shift
	shift
	;;
esac
done

if [ -z $FROM ]
then
	echo "Usage $0 -from <yyyy-mm-dd>"
	exit 1
fi

if [ ! -r $MONEY ]
then
	echo "*** Failed to locate original MS Money file [$MONEY]"
	exit 1
fi

ant -Ddir.install=$INSTALL -Ddir.gitrepos=$GIT_REPOS -f $INSTALL/slepeweb-money-build.xml

if [ $? -ne 0 ]
then
	echo "*** Failed to build jar file [$JAR]"
	exit 1
fi
     
if [ ! -r $JAR ]
then
	echo "*** Please export jar file [$JAR]"
	exit 1
fi

if [ ! -d $JARS ]
then
	echo "*** Please export all dependant jars into $JARS"
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
cd $DIST
java -jar $INSTALL/sunriise-export-0.0.1-SNAPSHOT-exec.jar $WORKING_MONEY g1ga50ft $WORKING_MDB

if [ $? -ne 0 ]
then
	echo "*** Failed to output MS Access Database [$WORKING_MDB]"
	exit 1
fi

cd $DIST
jar xvf $JAR

cd
java -cp $JARS/*:$TOMCAT_JARS/*:$DIST com.slepeweb.money.MoneyImportManager -mdb $WORKING_MDB -from $FROM
