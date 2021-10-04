#!/bin/bash
#
SCRIPTS=/home/george/git-repos/slepeweb-misc/script
PROJ_FOLDER=$SCRIPTS/pwgjs
DEST_FOLDER=~george/node-sandpit
mkdir -p $DEST_FOLDER/server $DEST_FOLDER/logs $DEST_FOLDER/db
cp -r $SCRIPTS/slepeweb-modules $DEST_FOLDER
cp -r $PROJ_FOLDER/server $PROJ_FOLDER/test $DEST_FOLDER
cp -r $PROJ_FOLDER/public $PROJ_FOLDER/routes $PROJ_FOLDER/views $DEST_FOLDER
