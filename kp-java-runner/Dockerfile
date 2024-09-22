FROM docker.io/amazoncorretto:21-alpine AS runner

USER root

RUN mkdir -p /deployments

COPY run-java.sh /deployments/

ENV JAVA_APP_DIR=/deployments \
    JAVA_OPTIONS="-Dfile.encoding=utf-8" \
    LOG4J_FORMAT_MSG_NO_LOOKUPS=true \
    JAVA_MAJOR_VERSION=21 \
    JAVA_MAX_HEAP_RATIO=40 \
    TZ=UTC

RUN apk add --update --no-cache curl nss bash \
    && rm -rf /tmp/* /var/tmp/* /var/cache/apk/* \
    && chmod 755 /deployments/run-java.sh \
    && addgroup --gid 1000 appgroup \
    && adduser --uid 1000 --ingroup appgroup --no-create-home --home /deployments --disabled-password appuser \
    && addgroup appuser root \
    && chmod 755 /bin/busybox `which sh` `which curl` `which apk` \
    && echo "set disable-completion on" >> /etc/inputrc

USER 1000
EXPOSE 8080 8081 8443

ENTRYPOINT [ "sh", "/deployments/run-java.sh" ]