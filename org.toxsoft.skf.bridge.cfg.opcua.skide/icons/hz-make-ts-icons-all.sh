#!/bin/bash

#
# Создает значки по подкаталогам isWWxHH из всех *.png файлов в каталоге запуска
#

for f in *.png ;
  do {
    echo $f ...
    . hz-make-ts-icons-one.sh $f
  };
done;
