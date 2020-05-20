#!/bin/bash

pushd $(dirname $0) >/dev/null

# [Optional] Define the Trust Store Path and Password.
TRUST_STORE_PATH=
TRUST_STORE_PASSWORD=

# If we have configuration for trust and key store, setup the
#  JVM options to use them.
if [ ! -z "$TRUST_STORE_PATH" -a ! -z "$TRUST_STORE_PASSWORD" ]; then
  TRUST_STORE_OPTS="-Djavax.net.ssl.trustStore=${TRUST_STORE_PATH} -Djavax.net.ssl.trustStorePassword=${TRUST_STORE_PASSWORD} -Dhcpawreptool.enforceCertTrust=true"
fi

java ${TRUST_STORE_OPTS} -jar HCPAWRepTool.jar

popd >/dev/null

