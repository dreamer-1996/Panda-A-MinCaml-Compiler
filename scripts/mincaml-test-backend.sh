#! /bin/sh
cd "$(dirname "$0")"/.. || exit 1

# colors
RED='\033[0;31m'
GREEN='\033[0;32m'
BROWN='\033[0;33m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

printf "\n${CYAN}<<<<<<<<<< TESTING BACKEND >>>>>>>>>>${NC}\n\n"

AS=arm-eabi-as
LD=arm-eabi-ld
GC=arm-linux-gnueabi-gcc

success=0
total=0
failed=0
failed_list=""
passed_list=""

ass_path=tests/backend/assembly
ex_path=tests/backend/executables

mkdir -p $ass_path
mkdir -p $ex_path

for asml_file in tests/backend/*.asml
do
    name="${asml_file##*/}"
    name="${name%.*}"

    ass_name="$ass_path/$name.s"

    echo "======================================="
    echo "testing parser on: $asml_file"
    echo ""
    printf "${BROWN}Expected :${NC} "

    exp=$(./tools/asml "$asml_file" 2> /dev/null );
    if [ $? -ne 0 ]
    then
      echo "Error interpreting $asml_file"
      exp="ERROR"
      continue
    else
      echo $exp
    fi

    printf "${YELLOW}Got :${NC} "

    $(./mincamlc -fromAsml -o "${ass_name}" "${asml_file}" &> /dev/null );
    $(${GC} -nostartfiles -o $ex_path/$name.arm $ass_name ARM/libmincaml.S --static );
    rm out.o 2> /dev/null

    real=$(qemu-arm ./$ex_path/$name.arm );
    echo $real

    if [ "$real" = "$exp" ]
    then
      printf "\n${GREEN}PASS: program compiled correctly${NC}\n\n"
      success=$((success+1))
      passed_list="$passed_list $name"
    else
      printf "\n${RED}FAIL: program did not compile correctly${NC}\n\n"
      failed_list="$failed_list $name"
    fi
    total=$((total+1))

done


printf "=================END===================\n"
printf "${CYAN}Tests successfully: $success/$total ${NC}\n"
echo "======================================="
printf "${GREEN}Passed:"
printf "$passed_list${NC}\n"
echo "---------------------------------------"
printf "${RED}Failed:"
printf "$failed_list${NC}\n"

