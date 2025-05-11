#!/bin/bash

# .env 파일이 있는지 확인
if [ -f .env ]; then
  echo ".env 로딩 중..."
  export $(grep -v '^#' .env | xargs)
else
  echo ".env 파일이 없습니다!"
  exit 1
fi

# 프로젝트 실행
./gradlew bootRun
