# Crosscutting Concepts

## Observability

| Concept | Description |
| :------ | :---------- |
| OL01 | Logfiles are collected centrally and provided from there. |
| OM01 | Central metrics can be queried ad-hoc. |
| OM02 | Alerting is based on collected metrics. |
| OR02 | Dashboards based on metric collection (OM01) on the services are provided to provide insight into the runtime of the system. |
| OR01 | Reports and the way of generating them is an open issue. They should be based on the metrics (OM01). |

## Softwaredelivery

| Concept | Description |
| :------ | :---------- |
| AB03 & AB04| OCI container and helm chart (for our runtimes). |
| AD01 & AD03 | Provided as service via internet installed on k8s. |
| AD02 | The software is delivered via Maven Repositories (for java artifacts). |
