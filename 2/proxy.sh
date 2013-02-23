#!/usr/bin/env bash

for parameter in "$@"
do
  if [ "$parameter" == '--help' ]
  then
    echo "Proxy server. Usage: ./proxy.sh [listening_port]"
    exit
  fi
done


if [ $# -ne 1 ]
then
  port=1234
else
  port=$1
fi

mkfifo response_fifo request_fifo request_fifo_copy

( cat < response_fifo |  nc -l $port | tee request_log | tee request_fifo_copy | tee request_fifo ) &
pid=$!

(
  while read s; do
    host=`echo $s | grep "Host: " | tr -d '\r' | sed "s/Host: //"`
    if [[ "$host" != "" ]]
    then
      nc $host 80 < request_fifo | tee response_log | tee response_fifo
      break
    fi
  done
) < request_fifo_copy


wait $pid
rm response_fifo request_fifo request_fifo_copy

