#!/bin/bash
files=(`cat server_list.txt`)
port=2000
for i in "${files[@]}"
do
	echo "starting trekwar server with file $i on port $port"
	java org.aakretech.trekwar2.server.TrekwarServer $port $i &
	let "port++"
	sleep 5
done