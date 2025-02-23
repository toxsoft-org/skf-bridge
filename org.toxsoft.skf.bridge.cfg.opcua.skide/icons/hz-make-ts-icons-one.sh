#!/bin/bash

# Создает значки из указанного файла разного размера в директориях /isWWxHH

ICONSIZES=(
  "16x16"
  "24x24"
  "32x32"
  "48x48"
  "64x64"
  "96x96"
  "128x128"
)

SUBSUBDIR=""

usage() {
  echo "Создает значки из указанного файла разного размера в директориях /isWWxHH"
  echo "Usage:"
  echo "  $0 image_file_name.ext [subdir]"
  echo ""
  exit 1
}

# check command line args
if [[ $# == 0 ]]; then
  usage
fi

if [[ $# == 1 ]]; then
  SUBSUBDIR=""
fi

if [[ $# == 2 ]]; then
  SUBSUBDIR="$2/"
fi

if [[ $# > 2 ]]; then
  usage
fi

FILENAME="$1"
# BARENAME=`getfname "${FILENAME}"`

for (( i=0;i<${#ICONSIZES[@]};i+=1)); do
  SZ=${ICONSIZES[$i]}
  SUBDIR=is$SZ
  mkdir $SUBDIR $SUBDIR/${SUBSUBDIR} 2> /dev/null
  gm convert -resize $SZ $FILENAME is$SZ/${SUBSUBDIR}${FILENAME}
done

# gm convert -resize 128x128 $FILENAME $BARENAME-128.xpm    
