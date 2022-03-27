#!/bin/bash

# 쉬고 있는 profile 찾기: real1이 사용 중이면 real2가 쉼. 반대로, real2가 사용 중이면 real1이 쉼.
function find_idle_profile() {
  RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost/app/profile) # Nginx와 연결되어 있는 스프링 부트가 정상 작동 중인지 확인

  if [ ${RESPONSE_CODE} -ge 400 ] # 400보다 크면(즉, 40x, 50x 에러 모두 포함)
  then
    CURRENT_PROFILE=real2
  else
    CURRENT_PROFILE=$(curl -s http://localhost/app/profile)
  fi

  # 연결되지 않은 profile 저장
  if [ ${CURRENT_PROFILE} == real1 ]
  then
    IDLE_PROFILE=real2
  else
    IDLE_PROFILE=real1
  fi

  echo "${IDLE_PROFILE}" # IDLE_PROFILE 출력. 스크립트는 값을 반환하는 기능이 없어서 마지막 줄 echo로 출력 후 그 값을 캐치하는 식으로 전송한다. ($(find_idle_profile))
}

# 쉬고 있는 profile의 port 찾기
function find_idle_port() {
    IDLE_PROFILE=$(find_idle_profile)

    if [ ${IDLE_PROFILE} == real1 ]
    then
      echo "8081"
    else
      echo "8082"
    fi
}