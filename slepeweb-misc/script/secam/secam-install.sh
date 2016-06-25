#!/bin/bash
#
scp -i ~/.ssh/id_rsa index.py secam.py controls.py pi@rpi-raspbian:/var/www/html/py
scp -i ~/.ssh/id_rsa secam.js style.css pi@rpi-raspbian:/var/www/html/resource
#scp -i ~/.ssh/id_rsa jquery.colorbox-min.js colorbox.css pi@rpi-raspbian:/var/www/html/resource
#scp -i ~/.ssh/id_rsa -r images pi@rpi-raspbian:/var/www/html/resource
