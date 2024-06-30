FROM docker.io/alpine/java:21-jdk AS runner

USER root

RUN mkdir -p /deployments

COPY run-java.sh /deployments/

ENV JAVA_APP_DIR=/deployments \
    JAVA_OPTIONS="-Dfile.encoding=utf-8" \
    LOG4J_FORMAT_MSG_NO_LOOKUPS=true \
    JAVA_MAJOR_VERSION=21 \
    JAVA_MAX_HEAP_RATIO=40

RUN apk add --update --no-cache curl nss bash \
    && cp -f /usr/share/zoneinfo/UTC /etc/localtime && echo "UTC" > /etc/timezone && apk del tzdata \
    && sed -i 's/securerandom.source=file:\/dev\/random/securerandom.source=file:\/dev\/.\/urandom/' /opt/java/openjdk/conf/security/java.security \
    && sed -i 's/SSLv3, TLSv1, TLSv1.1, //' /opt/java/openjdk/conf/security/java.security \
    && rm -rf /tmp/* /var/tmp/* /var/cache/apk/* \
    && chmod 755 /deployments/run-java.sh \
    && addgroup --gid 1000 appgroup \
    && adduser --uid 1000 --ingroup appgroup --no-create-home --home /deployments --disabled-password appuser \
    && addgroup appuser root \
    && chmod 744 /bin/busybox && chmod 744 `which curl` `which apk` \
    && echo "set disable-completion on" >> /etc/inputrc

USER 1000
EXPOSE 8080 8081 8443

CMD [ "/deployments/run-java.sh" ]