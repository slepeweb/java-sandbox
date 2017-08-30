#!/bin/bash
#
scp -i ~/.ssh/id_rsa camera.py constants.py controls.py document.py pi@rpi-raspbian:/var/www/html/py
scp -i ~/.ssh/id_rsa index.py messaging.py secam.py spibox.py support.py pi@rpi-raspbian:/var/www/html/py
scp -i ~/.ssh/id_rsa secam.js style.css pi@rpi-raspbian:/var/www/html/resource
#scp -i ~/.ssh/id_rsa jquery.colorbox-min.js colorbox.css pi@rpi-raspbian:/var/www/html/resource
#scp -i ~/.ssh/id_rsa -r images pi@rpi-raspbian:/var/www/html/resource
