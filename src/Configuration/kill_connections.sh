#!/bin/bash
# Kills all java processes on remote servers

for server in `cat server.txt`;  do
    ssh d.hawley@$server "killall java -u d.hawley"
done