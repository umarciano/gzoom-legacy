#!/bin/bash
BASENAME="project.info"
FILE_STANDARD="hot-deploy/base/config/$BASENAME"
TAG_STANDARD=$1
FILE_CUSTOM="hot-deploy/custom/config/$BASENAME"
TAG_CUSTOM=$2

function generate_file {
 echo '<?xml version="1.0" encoding="UTF-8"?>' > $1
 echo "<info>" >> $1
 echo "<version>$2</version>" >> $1
 echo "<date>`date +%F`T`date +%T`.000000Z</date>" >> $1
 echo "</info>" >> $1
}
generate_file "$FILE_STANDARD" "$TAG_STANDARD"
generate_file "$FILE_CUSTOM" "$TAG_CUSTOM"
