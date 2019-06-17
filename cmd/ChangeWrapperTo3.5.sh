#!/usr/bin/env bash

#!/usr/bin/env bash


# 将项目的gradle-wrapper.properties文件替换为 3.5版本
# 用来兼容3.0 AndroidStudio

#2.3版本的gradle-wrapper.properties是低版本的2.14.1 3.3等
#此脚本可以一键讲2.14版本替换为 3.5版本
# s/表示替换
#/[0-9]\.[0-9] 匹配 3.0 3.3 等正则表达式
# 3.5 意思是替换为 3.5
# mv  将新的文件 输出到最新位置


wrapFilePath=../gradle/wrapper/gradle-wrapper.properties

sed  's/[0-9]\.[0-9]/3.5/'    ${wrapFilePath} > new-gradle-wrapper.properties

mv new-gradle-wrapper.properties ${wrapFilePath}
git update-index --assume-unchanged ${wrapFilePath}
