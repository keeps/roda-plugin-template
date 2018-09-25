#!/bin/bash
set -e

RODA_VERSION_ARG=""

if [[ ! -z "$1" ]]; then
  RODA_VERSION_ARG="--build-arg RODA_VERSION=$1"
fi

PLUGIN=$(basename $(dirname $(readlink -m $0)))

echo "Building plugin ${PLUGIN}"

mvn clean package -Dmaven.test.skip -Denforcer.skip
docker build $RODA_VERSION_ARG --build-arg PLUGIN=${PLUGIN} -t ${PLUGIN}  .
