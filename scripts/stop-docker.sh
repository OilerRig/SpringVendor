

REMOTE_USER="yoonseo"
REMOTE_HOST="your.vm.public.ip"
REMOTE_DIR="~/SpringVendor"

echo "Connecting to $REMOTE_USER@$REMOTE_HOST to stop containers..."

ssh "$REMOTE_USER@$REMOTE_HOST" << EOF
  echo "Changing to project directory..."
  cd "$REMOTE_DIR"

  echo "Stopping and removing containers..."
  docker compose down
EOF
