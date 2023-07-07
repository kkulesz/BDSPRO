FROM ubuntu:22.04

ENV JAVA_VERSION 17
ENV POSTGRES_VERSION 15
ENV LANG de_DE.UTF-8

# Disable any interactive prompts during installation, fail on first error.
ENV DEBIAN_FRONTEND=noninteractive
RUN set -xeu

# general
RUN apt-get update \
    && apt-get install -y --no-install-recommends git gnupg2 ca-certificates curl netbase wget lsb-release \
    && rm -rf /var/lib/apt/lists/*

# install JDK
RUN wget -O- https://apt.corretto.aws/corretto.key | apt-key add - \
    && echo "deb https://apt.corretto.aws stable main" | tee /etc/apt/sources.list.d/corretto.list \
    && apt-get update \
    && apt-get -y install --no-install-recommends java-$JAVA_VERSION-amazon-corretto-jdk \
    && rm -rf /var/lib/apt/lists/*

## install postgresql
#RUN apt-get update  \
#    && apt-get install -y gnupg postgresql-common yes | /usr/share/postgresql-common/pgdg/apt.postgresql.org.sh  \
#    && apt-get -y install postgresql-$POSTGRES_VERSION postgresql-server-dev-$POSTGRES_VERSION  \
#    && apt-get install -y gcc cmake libssl-dev libkrb5-dev git nano \
#    && rm -rf /var/lib/apt/lists/*
#
#RUN wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | apt-key add - \
#    && echo "deb http://apt.postgresql.org/pub/repos/apt/ $(lsb_release -cs)-pgdg main" >> /etc/apt/sources.list.d/pgdg.list \
#    && apt-get update \
#    && apt install -y --no-install-recommends postgresql-$POSTGRES_VERSION \
#    && rm -rf /var/lib/apt/lists/*

RUN apt-get update && apt-get upgrade -y \
      && apt-get install -y --no-install-recommends locales \
      && locale-gen $LANG && update-locale LANG=$LANG \
      && wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | apt-key add - \
      && echo "deb http://apt.postgresql.org/pub/repos/apt/ `lsb_release -cs`-pgdg main" >> /etc/apt/sources.list.d/pgdg.list \
      && apt-get update && apt-get upgrade -y\
      && apt-get install -y --no-install-recommends \
           postgresql-$POSTGRES_VERSION \
           postgresql-client-$POSTGRES_VERSION \
           postgresql-contrib-$POSTGRES_VERSION \
      && apt-get clean \
      && rm -rf /var/lib/apt/lists/* \
      && rm -rf /tmp/*

# TODO: create postgres DB here, set its credentials as ENV_VARS and use them in code to connect to this DB
# TODO: install clickhouse and do the same probably