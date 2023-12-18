---
title: "Core Libraries, Level 2"
menuTitle: "Core Libraries"
pre: "<b>5.2.1</b> "
weight: 210
---

The core of KP-COMMONS consists of the API for **all** submodules of KP-COMMONS (including the [→ modules](/05_buildingblocks/02_2_modules/) and the [→ services](/05_buildingblocks/02_3_services).
And just as the modules and services they rely on the [→ internals](/05_buildingblocks/02_4_internals).

```mermaid
graph TD
    api[kp-commons-api] --> parent
    core[kp-commons-core] --> api
    core -.-> test
    core --> parent
    test(kp-commons-test) --> parent

    parent[kp-commons-parent]
```

*Graph: Technical context of all core components of the KP-COMMONS.*

## API (kp-commons-api) {#API}

## Core (kp-commons-core) {#CORE}

## Test (kp-commons-test) {#TEST}
