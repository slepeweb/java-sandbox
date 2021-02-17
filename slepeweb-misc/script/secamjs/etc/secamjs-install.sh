#!/bin/bash
#
GIT_FOLDER=/home/george/git-repos/slepeweb-misc/script/secamjs
scp -P 223 $GIT_FOLDER/*.js $GIT_FOLDER/*.json pi@rpi-raspbian:~/secamjs
scp -P 223 -r $GIT_FOLDER/public $GIT_FOLDER/routes $GIT_FOLDER/views pi@rpi-raspbian:~/secamjs
