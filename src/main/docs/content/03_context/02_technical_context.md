---
title: "Technical Context"
pre: "<b>3.2</b> "
weight: 20
---

## 3.2 Technical Context

```mermaid
graph TD
    parent[kp-commons-parent]
    api[kp-commons-api] --> parent
    core[kp-commons-core] --> api
    core -.-> test
    core --> parent
    test(kp-commons-test) --> parent

    jackson[kp-commons-jackson] --> core
    jackson -.-> test
    jackson -.-> api
    jackson --> parent
    jpa[kp-commons-jpa] --> core
    jpa -.-> test
    jpa -.-> api
    jpa --> parent
    rest[kp-commons-rest] --> core
    rest -.-> test
    rest -.-> api
    rest --> parent

    dnb[kp-services-dnb-lookup]
    dnb -.-> test
    dnb -.-> api
    dnb --> parent
    ean[kp-services-ean-search]
    ean -.-> test
    ean -.-> api
    ean --> parent
    sms[kp-services-sms77]
    sms -.-> test
    sms -.-> api
    sms --> parent
```

*Technical context of all public components of the KP-COMMONS.*

-----

| Komponente | Nutzung |
|------------|---------|
| kp-commons-api | Central API for all modules |
| kp-commons-core | The generic implementation of all central library components. |
| kp-commons-test | Helpers for tests. |
| kp-commons-jackson | Json/Xml handling. |
| kp-commons-jpa | Handling JPA based databases. |
| kp-commons-rest | REST handling. |
| kp-services-dnb-lookup | Lookup of publications in the DNB. |
| kp-services-ean-search | Lookup of EAN numbers. This is a service to a paid service. |
| kp-services-sms77 | Sending of SMS via a paid service provided by https://seven.io. |
