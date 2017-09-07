#/bin/bash

case "$1" in
'start')
    export LD_LIBRARY_PATH=/usr/local/lib/mjpg-streamer
    mjpg_streamer -o "output_http.so -p 8083 -w /usr/local/www" -i "input_raspicam.so -fps 30" &
    ;;
'stop')
    sudo killall mjpg_streamer
    ;;
esac