#!/bin/sh
if [ -z "$CONTAINER_CLI" ]; then CONTAINER_CLI="docker"; fi

mkdir -p src/main/resources/static
rm -r src/main/resources/static/* || true
rm -r ui/build/* || true
cd ui || { echo "Unable to access ui directory"; exit 1; }
npm install

if ! npx npx eslint .; then
  echo "ESLint found issues"
  exit 1
fi

npm run build
cd ..
cp -r ui/build/* src/main/resources/static
rm -r ui/build/* || true

if [ "$SKIP_TESTS" = true ] ; then
  CONTAINER_CLI=$CONTAINER_CLI mvn clean install -Dmaven.test.skip=true -Dexec.skip=true -Djacoco.skip=true
else
  CONTAINER_CLI=$CONTAINER_CLI mvn clean install
fi

$CONTAINER_CLI build --platform linux/amd64 . -t europe-west2-docker.pkg.dev/c31-rm-ci-prod/rm-docker-snapshot/census-rm-support-tool:latest
