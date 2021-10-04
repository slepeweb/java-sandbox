#!/bin/bash
#
SCRIPTS=/home/george/git-repos/slepeweb-misc/script
PROJ_FOLDER=$SCRIPTS/pwgjs
DEST=pi@rpi-raspbian
scp -P 223 -r $PROJ_FOLDER/server $DEST:~/pwgjs
scp -P 223 -r $PROJ_FOLDER/public $PROJ_FOLDER/routes $PROJ_FOLDER/views $SCRIPTS/slepeweb-modules $DEST:~/pwgjs
