#!/bin/bash

#Installing node 12.22
NODE_VERSION="12.22"
export NVM_DIR="$HOME/.nvm"
[ -s "$NVM_DIR/nvm.sh" ] && . "$NVM_DIR/nvm.sh"  # This loads nvm
nvm install ${NODE_VERSION}
nvm use ${NODE_VERSION}

# Installing yarn
YARN_VERSION="1.3.2"
YARN_LOCATION="tools"

if [ ! -d "$YARN_LOCATION" ]; then
	mkdir -p ${YARN_LOCATION}
	cd ${YARN_LOCATION}/
	wget -qO- https://github.com/yarnpkg/yarn/releases/download/v${YARN_VERSION}/yarn-v${YARN_VERSION}.tar.gz | tar zvx
	cd ..
fi
PATH="$PATH:${YARN_LOCATION}/yarn-v${YARN_VERSION}/bin/"

# Installing packages via yarn

echo "INSTALLING PRODUCTION DEPENDENCIES"
yarn dist

echo "INSTALLING BUILD DEPENDENCIES"
yarn install

echo "TRANSPILING"
yarn compile

echo "BUNDLING AND UPLOADING TO RIFFRAFF"
yarn riffraff