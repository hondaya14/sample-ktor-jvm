#!/bin/bash

NODE_COUNT=3
BASE_PORT=6380
LOCALHOST="127.0.0.1"
NODE_LIST=""

for i in $(seq 1 $NODE_COUNT); do
  let NODE_PORT=BASE_PORT+i
  echo "Start valkey node (port: $NODE_PORT) ..."
  mkdir $NODE_PORT
  cd ./$NODE_PORT
  cat << EOF > node$NODE_PORT.conf
port $NODE_PORT
cluster-enabled yes
appendonly yes
cluster-config-file node_$NODE_PORT.conf
EOF

  # create valkey nodes
  valkey-server node$NODE_PORT.conf &
  NODE_LIST="$NODE_LIST $LOCALHOST:$NODE_PORT"
  cd ..
done

# wait for node start
echo "Waiting for all valkey nodes to respond to PING..."

for addr in $NODE_LIST; do
  host="${addr%%:*}"; port="${addr##*:}"
  # Suppress errors while nodes are booting
  until valkey-cli -h "$host" -p "$port" ping >/dev/null 2>&1; do
    sleep 0.5
  done
  echo "Started node $addr."
done

# create valkey cluster
echo "Start valkey cluster (nodes: $NODE_LIST)..."
valkey-cli --cluster create $NODE_LIST --cluster-replicas 0 --cluster-yes

# container lives permanently
tail -f /dev/null
