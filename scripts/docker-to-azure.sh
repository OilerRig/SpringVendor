

REMOTE_USER="yoonseo"     
REMOTE_HOST="bigchungus.eastus.cloudapp.azure.com"
REMOTE_DIR="/home/yoonseo/SpringVendor"
DOCKER_COMPOSE_FILE="docker-compose.yml"


echo "Connecting to $REMOTE_USER@$REMOTE_HOST..."

ssh "$REMOTE_USER@$REMOTE_HOST" << EOF
  echo "changing to project directory..."
  cd "$REMOTE_DIR"

  echo "Pulling latest code from GitHub..."
  git pull origin main

  echo "Building and starting Docker containers..."
  docker compose down
  docker compose up --build -d

  echo "Deployment complete! LESGOOO"
EOF
