REMOTE_USER="yoonseo"
REMOTE_HOST="bigchungus.eastus.cloudapp.azure.com"
REMOTE_DIR="/home/yoonseo/SpringVendor"

echo "Connecting to $REMOTE_USER@$REMOTE_HOST..."

ssh -tt "$REMOTE_USER@$REMOTE_HOST" << 'EOF'
  echo "Changing to project directory..."
  cd /home/yoonseo/SpringVendor || { echo "Directory not found"; exit 1; }

  echo "Pulling latest code from GitHub..."
  git pull origin main || { echo "Git pull failed"; exit 1; }

  echo "Building and starting Docker containers..."
  docker compose down || { echo "Docker down failed"; exit 1; }
  docker compose up --build -d || { echo "Docker up failed"; exit 1; }

  echo "Deployment complete! LESGOOO"
EOF
