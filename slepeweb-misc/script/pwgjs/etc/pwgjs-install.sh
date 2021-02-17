#!/bin/bash
#
GIT_FOLDER=/home/george/git-repos/slepeweb-misc/script/pwgjs
scp -P 223 $GIT_FOLDER/*.js pi@rpi-raspbian:~/pwgjs
scp -P 223 -r $GIT_FOLDER/public $GIT_FOLDER/routes $GIT_FOLDER/views pi@rpi-raspbian:~/pwgjs
