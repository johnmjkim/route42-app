#bin/sh

#file=$1
for file in $(find ./app/src/main -name '*.java')
do
  file_name=$(echo -e $file | sed -E "s/.*\/([A-Z][A-Za-z0-9]+\.java)$/\1/")
  echo $file_name

  echo "Attributes"
  attributes=$(grep -E "public|private|protected" $file | grep -wv "class" | sed -e 's/^[ \t]*//' | grep -Evw "{$" | grep -Ev "^//" | sed -E "s/[[:<:]]final[[:>:]]//g")
  echo $attributes \
    | sed "s/[[:<:]]public[[:>:]]/+ /g" \
    | sed "s/[[:<:]]private[[:>:]]/-/g" \
    | sed "s/[[:<:]]protected[[:>:]]/#/g" \
    | sed -E "s/[[:<:]]final[[:>:]]//g" \
    | sed -E "s/; ?/\n/g" \
    | sed -E "s/=.*//g"

  echo "Methods"
  grep -E "public|private|protected" $file \
    | grep -E "{$" \
    | grep -wv "class" \
    | sed -E "s/void//g" \
    | sed -E "s/@N[a-z]+//g" \
    | sed -E "s/public/+/g" \
    | sed -E "s/private/-/g" \
    | sed -E "s/protected/#/g" \
    | sed -E "s/(.*){$/\1/g"

  echo ""
done