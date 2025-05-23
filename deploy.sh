#!/bin/bash

set -e

DIR=$(dirname "$(readlink -m "$0")")

CLEAN=false
PULL=false
DOWN=false
UP=false

for arg in "$@"; do
    case "$arg" in
        clean) CLEAN=true ;;
        pull)  PULL=true ;;
        down)  DOWN=true ;;
        up)    UP=true ;;
    esac
done

if $CLEAN; then
    docker compose -f "$DIR/deploys/docker-compose.yml" down -v
elif $DOWN; then
    docker compose -f "$DIR/deploys/docker-compose.yml" down
fi

if $PULL; then
    docker compose -f "$DIR/deploys/docker-compose.yml" pull zoo solr clamd siegfried
fi

if $UP; then
    docker compose -f "$DIR/deploys/docker-compose.yml" up -d
fi