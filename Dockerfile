ARG RODA_VERSION=latest
FROM keeps/roda:${RODA_VERSION}
ARG PLUGIN

ENV PLUGIN=${PLUGIN}

COPY target/${PLUGIN} ${RODA_HOME}/config/plugins/${PLUGIN}/
USER root
RUN ${RODA_HOME}/config/plugins/${PLUGIN}/install_dependencies.sh
USER ${RODA_USER}
