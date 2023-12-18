---
title: "Modules, Level 2"
menuTitle: "Modules"
pre: "<b>5.2.2</b> "
weight: 220
---

The core of KP-COMMONS consists of the API for **all** submodules of KP-COMMONS (including the [→ modules](/05_buildingblocks/02_2_modules/) and the [→ services](/05_buildingblocks/02_3_services).
And just as the modules and services they rely on the [→ internals](/05_buildingblocks/02_4_internals).

```mermaid
graph TD
    jackson[kp-commons-jackson] --> core
    jackson -.-> test
    jackson -.-> api
    jackson -.-> parent

    jpa[kp-commons-jpa] --> core
    jpa -.-> test
    jpa -.-> api
    jpa -.-> parent

    rest[kp-commons-rest] --> core
    rest -.-> test
    rest -.-> api
    rest -.-> parent

    api[kp-commons-api]
    core[kp-commons-core]
    test(kp-commons-test)
    parent[kp-commons-parent]
```

*Graph: Dependencies between the modules and core libraries.*

## Json and XML Handling (kp-commons-jackson)

## Database Handling (kp-commons-jpa)

## REST Interface Handling (kp-commons-test)