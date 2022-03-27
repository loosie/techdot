#!/bin/bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh # 여기서도 profile.sh 함수를 사용

REPOSITORY=/home/ec2-user/app
PROJECT_NAME=techdot-alpha

echo "> Build 파일 복사"
echo "> cp $REPOSITORY/build/libs/*.jar $REPOSITORY/"

cp $REPOSITORY/build/libs/*.jar $REPOSITORY/

echo "> 새 애플리케이션 배포"
JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)

echo "> JAR Name: $JAR_NAME"

echo "> $JAR_NAME 에 실행권한 추가"

chmod +x $JAR_NAME

echo "> $JAR_NAME 실행"
echo "> $REPOSITORY로 이동"

cd $REPOSITORY

IDLE_PROFILE=$(find_idle_profile)
IDLE_PORT=$(find_idle_port)

echo "> $JAR_NAME 를 profile=$IDLE_PROFILE ($IDLE_PORT)로 실행합니다."

nohup java -jar \
  -Dspring.config.location=classpath:/application.yml,/home/ec2-user/app/resources/application-$IDLE_PROFILE.yml,/home/ec2-user/app/resources/application-real-db.yml \
  -Dspring.profiles.active=$IDLE_PROFILE \
  $JAR_NAME > $REPOSITORY/nohup.out 2>&1 &