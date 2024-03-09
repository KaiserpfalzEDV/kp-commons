# Quality

```mermaid
graph LR;

q[Quality] --> s[Security];
q --> u[Upgradeability]
q --> e[Extendability]
q --> p[Performance]
q --> o[Observability]

s --> sa[Authentication and Authorization]
sa --> sa01[SA01 OpenID Connect Integration]
sa --> sa02[SA02 UME Integration]
sa --> sa03[SA03 mTLS]
s --> sd[Dependency Vulnerability]
sd --> sd01[SD01 Build Time Scanning]
sd --> sd02[SD02 Run Time Scanning]
s --> sb[Bugs and secure coding]
sb --> sd01
sb --> sd02
sb --> sb01[SB01 Secure Design]

u --> ud[Dependency Management]
ud --> ab01
u --> uc[Code organization]
u --> us[Replaceable External Services]

e --> a
e --> em[Modularization]
e --> es[Common Approach for External Services]

p --> pl[Core Libraries]
p --> pm[Module Libraries]
p --> ps[External Services]

o --> ol[Logging]
ol --> ol01[OL01 Central Log Collection]
o --> or[Reporting]
or --> or02[OR02 Dashboarding]
or --> or01[OR01 Interval Reporting]
o --> om[Monitoring]
om --> or01
om --> om01[OM01 Metrics]
om01 --> or
om --> om02[OM02 Alerting]

s --> a[Automation]
u --> a

a --> ab[Build Automation]
ab --> ab01[AB01 Maven]
ab --> ab03[AB03 OCI Container]
ab --> as01
ab --> ab04[AB04 Helm Charts]
a --> as[Security Scanning]
as --> as01[AS01 OWASP Scan]
a --> ad[Continuous Deployment]
ad --> ab04
ad --> ad01[AD01 ArgoCD]
ad --> ad02[AD02 Maven Repository]
ad --> ad03[AD03 Kubernetes]
ad --> ai02
a --> ai[Continuous Integration]
ai --> ai02[AI02 Quay.IO]
ai --> ai01[AI01 Github Actions]
```

## Quality Requirements

| ID    | Description |
| :---- | :---------- |
| AB01 | Maven. Build automation via Maven to build all Java Artifacts. |
| AB03 | OCI-Container. Executable services are delivered as OCI-Container plus Helm charts (AB04). |
| AB04 | Helm. Executable services are delivered as OCI-Container (AB03) plus Helm Charts. |
| AD01 | ArgoCD. Active service components are delivered to service via ArgoCD. |
| AD02 | Maven Repository. Java Artifacts are delivered via a maven repository. |
| AD03 | Kubernetes. Active Services are provided via kubernetes are runtime environment. |
| AI01 | Github Actions. The build is automated on Github via GH actions. |
| AI02 | Quay.IO. Containers and Helm Charts are provided via the container registry quay.io |
| AS01 | OWASP-Scan. During build an OWASP based security scan and report is delivered. |
| OM01 | Metrics are collected via a central metric harvesting. |
| OM02 | Alerting is based on metric collection provided by OM01. |
| OR01 | Interval Reporting. |
| OR02 | Dashboarding. |
| SA01 | OpenID Connect is used for network based user authentication |
| SA02 | UME is used for managing user provided content with a permission management. |
| SA03 | mTLS is used for service to service authentication. |
| SB01 | Secure Design is the basis for providing secure software. |
| SD01 | Build Time Scanning. |
| SD02 | Run Time Scanning. |

## Quality Scenarios
