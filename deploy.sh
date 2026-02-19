#!/bin/bash

# Check if required parameters are provided
if [ "$#" -lt 2 ]; then
    echo "Usage: $0 <image-tag> <env> [extra-helm-args...]"
    exit 1
fi

IMAGE_TAG=$1
ENV=$2  # e.g., DEV(empty), prod
shift 2

# Source environment variables if .env file exists
ENV_FILE=".env.${ENV}"
if [ -f "$ENV_FILE" ]; then
    echo "Sourcing environment variables from $ENV_FILE..."
    # set -a: Automatically export all variables defined in the file
    set -a
    source "$ENV_FILE"
    set +a
fi

# Configuration based on environment
BASE_NAME="ms-search-engine"
RELEASE_NAME="${BASE_NAME}-${ENV}"
NAMESPACE="${BASE_NAME}-${ENV}"
CHART_PATH="./charts/ms-search-engine"
VALUES_FILE_BASE="${CHART_PATH}/values.yaml"
VALUES_FILE_BY_ENV="${CHART_PATH}/values-${ENV}.yaml"

# Check if the environment values file exists
if [ ! -f "$VALUES_FILE_BASE" ]; then
    echo "Error: Configuration file $VALUES_FILE_BASE not found!"
    exit 1
fi

# Check if the environment values file exists
if [ ! -f "$VALUES_FILE_BY_ENV" ]; then
    echo "Error: Configuration file $VALUES_FILE_BY_ENV not found!"
    exit 1
fi

echo "Deploying $RELEASE_NAME to namespace $NAMESPACE..."
echo "Using values from: $VALUES_FILE"
echo "Image tag: $IMAGE_TAG"

# Helm upgrade --install
# HELM_ARGS includes environment-specific values and secrets
# --set image.tag: Apply the specific build tag
# "$@": Support custom overrides
helm upgrade --install $RELEASE_NAME $CHART_PATH \
  --namespace $NAMESPACE \
  --create-namespace \
  -f $VALUES_FILE_BASE \
  -f $VALUES_FILE_BY_ENV \
  --set image.tag=$IMAGE_TAG \
  --set config.java_sdk.logger.mail_notify=$CONFIG_JAVA_SDK_LOGGER_MAIL_NOTIFY \
  --set config.java_sdk.gmail.mail_username=$CONFIG_JAVA_SDK_GMAIL_MAIL_USERNAME \
  --set config.java_sdk.gmail.mail_password=$CONFIG_JAVA_SDK_GMAIL_MAIL_PASSWORD \
  --set config.java_sdk.discord.webhook_url=$CONFIG_JAVA_SDK_DISCORD_WEBHOOK_URL \
  --set config.spring.datasource.mysql_local.username=$CONFIG_SPRING_DATASOURCE_MYSQL_LOCAL_USERNAME \
  --set config.spring.datasource.mysql_local.password=$CONFIG_SPRING_DATASOURCE_MYSQL_LOCAL_PASSWORD \
  --set config.spring.mail.username=$CONFIG_SPRING_MAIL_USERNAME \
  --set config.spring.mail.password=$CONFIG_SPRING_MAIL_PASSWORD \
  --set config.spring.security.oauth2.client.registration.google.client_id=$CONFIG_SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID \
  --set config.spring.security.oauth2.client.registration.google.client_secret=$CONFIG_SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET \
  --set config.spring.security.oauth2.client.registration.github.client_id=$CONFIG_SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GITHUB_CLIENT_ID \
  --set config.spring.security.oauth2.client.registration.github.client_secret=$CONFIG_SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GITHUB_CLIENT_SECRET \
  --set config.spring.security.admin.username=$CONFIG_SPRING_SECURITY_ADMIN_USERNAME \
  --set config.spring.security.admin.password=$CONFIG_SPRING_SECURITY_ADMIN_PASSWORD \
  --set config.aws.s3.access_key=$CONFIG_AWS_S3_ACCESS_KEY \
  --set config.aws.s3.secret_key=$CONFIG_AWS_S3_SECRET_KEY \
  --set config.aws.cloud_front.endpoint=$CONFIG_AWS_CLOUD_FRONT_ENDPOINT \
  --set config.redis.task_queue.password=$CONFIG_REDIS_TASK_QUEUE_PASSWORD \
  "$@" \
  --wait \
  --timeout 5m0s

if [ $? -eq 0 ]; then
    echo "Deployment to $ENV successful!"
else
    echo "Deployment to $ENV failed!"
    exit 1
fi
