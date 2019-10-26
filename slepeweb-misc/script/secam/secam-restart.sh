#/bin/bash

sudo pkill -HUP -f "^python .*?/secam.py"
sudo pkill -HUP mjpg_streamer
sudo python /var/www/html/py/secam.py &
sudo service apache2 restart
