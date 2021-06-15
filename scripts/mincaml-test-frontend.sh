#!/bin/bash

cd "$(dirname "$0")"/.. || exit 1

# colors
RED='\033[0;31m'
GREEN='\033[0;32m'
CYAN='\033[0;36m'
NC='\033[0m'

printf "\n\n${CYAN}<<<<<<<<<< TESTING FRONTEND >>>>>>>>>>${NC}\n\n"

# programs
ASML=tools/asml
MINCAMLC=mincamlc
MVN=mvn

S_COUNT=0
T_COUNT=0

for test_case in tests/frontend/*.ml
do
  T_COUNT=$((T_COUNT + 1))
  file_name=$(basename "$test_case")
  printf "testing asml generation on: $file_name\n\n"
  timeout 7s ./$MINCAMLC -asml -o output.asml "$test_case" 1>/dev/null
  if [ $? -eq 124 ]
    then
      printf "${RED}FAIL: Could not generate $file_name asml ${NC}\n\n"
      continue
  fi

  if timeout 5s ./$ASML "output.asml" 1> /dev/null
  then
    printf "${GREEN}PASS: $file_name compiled successfully ${NC}\n\n"
    S_COUNT=$((S_COUNT + 1))
  elif  [ $? -eq 124 ]
  then
    printf "${RED}FAIL: compiling $file_name asml took too long ${NC}\n\n"
  else
    printf "${RED}FAIL: $file_name did not compile ${NC}\n\n"
  fi
done

printf "=================END===================\n"
printf "${CYAN}Tests successfully: ${S_COUNT}/${T_COUNT} ${NC}\n"
