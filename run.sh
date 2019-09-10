#!/bin/bash

if [[ -z ${1} ]]; then
	echo "Must supply an argument, the directory where you would like the clojure script binary and helper server to live."
	exit 1
fi

if [[ ! -d ${1} ]]; then
	echo "Argument must be a directory. Preferably one owned by you."
	exit 1
fi

TARGET=$(realpath ${1})

exec docker run -e "MNT=/mnt" -v ${TARGET}:/mnt clj-graal-mountable
