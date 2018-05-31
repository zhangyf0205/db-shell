#!/bin/sh

#获得当前shell所在路径
base_home=$(cd `dirname $0`; pwd)
class_path=""
for i in $base_home/lib/*.jar;
    do class_path=$i:"$class_path";
done

main="com.dbus.db.Application"
java  -classpath $class_path $main