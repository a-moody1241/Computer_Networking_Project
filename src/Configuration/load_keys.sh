#!/bin/bash
# Bash script used to copy your ssh keys over to the list of hosts in 'server.txt'.
# Ensure to change the file location and name of your public ssh key, many keys use
# RSA and therefore have the filename 'id_rsa.pub'.
# Lastly, change the username from 'd.hawley' to your Gatorlink username.

for server in `cat server.txt`;  do
    ssh-copy-id -f -i ~/.ssh/id_ed25519.pub d.hawley@$server  
done