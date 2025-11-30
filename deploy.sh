EC2_HOST="ubuntu@15.164.215.31"
EC2_KEY="$HOME/Documents/Beeve/aws_key_pem/beeve_key.pem"
REMOTE_DIR="~/beeve"

IMAGE_REPO="sunakang1/beeve-server-app"
IMAGE_TAG="prod"                         # <- 버전 올릴 때 여기만 바꾸면 됨
IMAGE="${IMAGE_REPO}:${IMAGE_TAG}"

echo "1) Spring Boot 빌드"
./gradlew clean build -x test

echo "2) Docker 이미지 buildx 빌드 + push (${IMAGE})"

export DOCKER_BUILDKIT=1
docker buildx create --use --name beeve-builder >/dev/null 2>&1 || docker buildx use beeve-builder

docker buildx build \
  --platform linux/amd64 \
  -t "${IMAGE}" \
  --push \
  .

echo "3) EC2에서 컨테이너 업데이트"
ssh -i "$EC2_KEY" "$EC2_HOST" "
  set -e && \
  cd $REMOTE_DIR && \
  sudo docker compose stop app || true && \
  sudo docker compose rm -f app || true && \
  sudo docker compose pull app && \
  sudo docker compose up -d && \
  sudo docker compose ps
"

echo "배포 완료!"