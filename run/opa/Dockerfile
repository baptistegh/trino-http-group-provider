FROM alpine:3.23.3

ARG TARGETARCH

RUN apk add --no-cache curl \
    && curl -sSL -f -o /opa https://github.com/open-policy-agent/opa/releases/download/v1.9.0/opa_linux_${TARGETARCH}_static \
    && chmod +x /opa

USER 1000:1000

EXPOSE 8181

ENTRYPOINT ["/opa"]
