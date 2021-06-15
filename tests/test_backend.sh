#!/bin/bash

AS=arm-eabi-as
LD=arm-eabi-ld

asml_files=backend/*
echo "Hello World!"
echo $asml_files
for file in $asml_files;
do
    echo $file
    echo "    " Expected
    
    exp=$(./../tools/asml $file)
    echo $exp

    echo "    " Got

    $(./../backend/mincaml_asml $file out.s)
    $(${AS} -o out.o out.s libmincaml.S)
    $(${LD} -o out.arm out.s libmincaml.S)
    real=$(./out.arm)
    rm out.arm

    echo $real

done