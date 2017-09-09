#/bin/bash

sudo pkill -HUP -f "^python .*?/secam.py"
sudo pkill -HUP mjpg_streamer

sleep 4
sudo python /var/www/html/py/secam.py &

sleep 2
echo "Secam process"
echo "-------------"
ps -ef|grep secam

echo "mjpg_streamer process"
echo "---------------------"
ps -ef|grep mjpg_streamer

echo "Apache process"
echo "--------------"
ps -ef|grep apache2
