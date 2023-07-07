FROM ubuntu:22.04

ENV JAVA_VERSION 17
ENV POSTGRES_VERSION 15
ENV LANG de_DE.UTF-8
ENV PGPASSWORD postgres
# Disable any interactive prompts during installation, fail on first error.
ENV DEBIAN_FRONTEND=noninteractive
RUN set -xeu

# general
RUN apt-get update \
    && apt-get install -y --no-install-recommends git gnupg2 ca-certificates curl netbase wget \
    && rm -rf /var/lib/apt/lists/*

# install JDK
RUN wget -O- https://apt.corretto.aws/corretto.key | apt-key add - \
    && echo "deb https://apt.corretto.aws stable main" | tee /etc/apt/sources.list.d/corretto.list \
    && apt-get update \
    && apt-get -y install --no-install-recommends java-$JAVA_VERSION-amazon-corretto-jdk \
    && rm -rf /var/lib/apt/lists/*

## install postgresql and other minors needed for timescaleDB
# https://docs.timescale.com/self-hosted/latest/install/installation-linux/
# https://docs.timescale.com/self-hosted/latest/configuration/timescaledb-tune/
# I know it's bad, it should be docker-compose. But whats interesting that in order to being able to run psql with server running, you need to do everything in one layer (meaning in one RUN command) - that's why it is so stupidly long
RUN apt-get update && apt-get upgrade -y \
      && apt-get install -y --no-install-recommends locales lsb-release apt-transport-https systemctl \
      && locale-gen $LANG && update-locale LANG=$LANG \
      && wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | apt-key add - \
      && echo "deb http://apt.postgresql.org/pub/repos/apt/ `lsb_release -cs`-pgdg main" >> /etc/apt/sources.list.d/pgdg.list \
      && apt-get update && apt-get upgrade -y\
      && apt-get install -y --no-install-recommends \
           postgresql-$POSTGRES_VERSION \
           postgresql-client-$POSTGRES_VERSION \
           postgresql-contrib-$POSTGRES_VERSION \
           postgresql-common \
      && apt-get clean \
      && rm -rf /var/lib/apt/lists/* \
      && rm -rf /tmp/* \
    && service postgresql start \
    && /usr/share/postgresql-common/pgdg/apt.postgresql.org.sh -y \
    && echo "deb https://packagecloud.io/timescale/timescaledb/ubuntu/ $(lsb_release -c -s) main" | tee /etc/apt/sources.list.d/timescaledb.list \
    && wget --quiet -O - https://packagecloud.io/timescale/timescaledb/gpgkey | apt-key add - \
    && apt-get update && apt-get upgrade -y \
    && apt install -y timescaledb-2-postgresql-$POSTGRES_VERSION \
    && rm -rf /var/lib/apt/lists/* \
    && timescaledb-tune  --quiet --yes  \
    && systemctl start postgresql  \
    && psql -U postgres

# we can also use docker-compose like below:
# https://gist.github.com/ismailyenigul/b62c6f25ba473d9dac30caf7bbfeab73
# BUT i do not know whether it will run on the same OS?

# TODO: create postgres DB here, set its credentials as ENV_VARS and use them in code to connect to this DB
# TODO: install clickhouse and do the same probably