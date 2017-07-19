#!/bin/bash

set -e
set -x

jar_name=$1
version=$2

source /app/start-kafka-and-zk.sh

java -jar -Xrunjdwp:transport=dt_socket,server=y,suspend=n /app/"$jar_name"-assembly-"$version".jar