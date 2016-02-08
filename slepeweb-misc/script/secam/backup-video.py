#!/usr/bin/python 
#
import sys, secam, os.path, time

numArgs = len(sys.argv)
if numArgs < 2:
    print("\n***\nUsage: backup-video <video-file-path> \n\n")
    sys.exit()
    
file_path = sys.argv[1]
file_parts = file_path.split("/")
file_name = file_parts[-1]

msg, ok = secam.backup_file(file_name)
