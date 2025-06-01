


REMOTE_USER="yoonseo"
REMOTE_HOST="bigchungus.eastus.cloudapp.azure.com"
REMOTE_DIR="/home/yoonseo/SpringVendor"
JAR_NAME="app.jar"

echo ">> Building Spring Boot application locally..."
./gradlew clean bootJar || { echo "Build failed"; exit 1; }

echo ">> Copying JAR to remote server..."
scp build/libs/*.jar ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_DIR}/${JAR_NAME}

echo ">> Connecting and restarting the app on remote server..."
ssh ${REMOTE_USER}@${REMOTE_HOST} << EOF
  echo ">> Killing any running Java process..."
  pkill -f ${JAR_NAME} || echo "No existing process to kill"

  echo ">> Starting app in background..."
  nohup java -jar ${REMOTE_DIR}/${JAR_NAME} > ${REMOTE_DIR}/app.log 2>&1 &

  echo ">> App restarted successfully."
EOF
