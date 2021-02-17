#!/bin/bash
#
GIT_FOLDER=/home/george/git-repos/slepeweb-misc/script/pwgjs
cp $GIT_FOLDER/*.js ~george/node-sandpit
cp -r $GIT_FOLDER/public $GIT_FOLDER/routes $GIT_FOLDER/views ~george/node-sandpit
