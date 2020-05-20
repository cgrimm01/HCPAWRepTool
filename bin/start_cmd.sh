#!/bin/bash

CMD_PARAMS="$@"

if [ $# -eq 0 ]; then
  CMD_PARAMS=-h
fi

pushd $(dirname $0) >/dev/null

# [Optional] Define the Trust Store Path and Password.
TRUST_STORE_PATH=
TRUST_STORE_PASSWORD=

# [Optional] Define the Key Store Path and Password.
KEY_STORE_PATH=
KEY_STORE_PASSWORD=

# If we have configuration for trust and key store, setup the
#  JVM options to use them.
if [ ! -z "$TRUST_STORE_PATH" -a ! -z "$TRUST_STORE_PASSWORD" ]; then
  TRUST_STORE_OPTS="-Djavax.net.ssl.trustStore=${TRUST_STORE_PATH} -Djavax.net.ssl.trustStorePassword=${TRUST_STORE_PASSWORD} -Dhcpawreptool.enforceCertTrust=true"
fi
if [ ! -z "$KEY_STORE_PATH" -a ! -z "$KEY_STORE_PASSWORD" ]; then
  KEY_STORE_OPTS="-Djavax.net.ssl.keyStore=${KEY_STORE_PATH} -Djavax.net.ssl.keyStorePassword=${KEY_STORE_PASSWORD}"
fi

java ${TRUST_STORE_OPTS} ${KEY_STORE_OPTS} -jar HCPAWRepTool.jar $CMD_PARAMS

popd >/dev/null

