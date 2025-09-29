# Microservice Helm Chart

## Abstract
A helm chart for microservices.


## Changelog

### Version 3.0.0

- Removed sub charts for postgres and HRabbitMQ.
- Added support for cnpg databases.
- Added support for HashiCorp Vault secrets.


### Version 2.1.6

- Reconfigured the default probes. Startup probes now runs every second for 300 Seconds.


### Version 2.1.3

- Corrected the postgresql connection name.


### Version 2.1.0

- Added env and envFrom to the microservice values file.
