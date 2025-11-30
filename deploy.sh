#!/usr/bin/env bash
set -e

EC2_HOST="ubuntu@15.164.215.31"
EC2_KEY="$HOME/Documents/Beeve/aws_key_pem/beeve_key.pem"
REMOTE_DIR="~/beeve"
IMAGE="sunakang1/beeve-server:latest"

echo "1) Spring Boot 빌드"
./gradlew clean build -x test

echo "2) Docker 이미지 buildx 빌드 + push"
docker buildx build \
  --platform linux/amd64,linux/arm64 \
  -t $IMAGE \
  --push .

echo "3) EC2에서 컨테이너 업데이트"
ssh -i $EC2_KEY $EC2_HOST "
  cd $REMOTE_DIR && \
  sudo docker-compose pull app && \
  sudo docker-compose up -d && \
  sudo docker-compose ps
"

echo "배포 완료!"
