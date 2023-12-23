---
title: "Internals, Level 2"
menuTitle: "Internals"
pre: "<b>5.2.4</b> "
weight: 240
---

The internals provide the maven parent and bom (bill of materials) for all other submodules.
In addition we consider the maven build system technicalities (like the root poms as glue to the published modules) as part of the internals.

```mermaid
graph TD
    api(kp-commons-api) --> core-root
    core(kp-commons-core) --> core-root
    test(kp-commons-test) --> core-root
    core-root[kp-commons-core-root] --> root

    jackson(kp-commons-jackson) --> modules-root
    jpa(kp-commons-jpa) --> modules-root
    rest(kp-commons-rest) --> modules-root
    modules-root[kp-commons-modules-root] --> root

    dnb(kp-commons-dnb-lookup) --> services-root
    ean(kp-commons-ean-search) --> services-root
    sms(kp-sommons-sms77) --> services-root
    services-root[kp-commons-services-root] --> root

    parent[kp-commons-parent] --> root
    bom[kp-commons-bom] --> root
    root[kp-commons-root]
```

*Graph: Technical context of all internal components of the KP-COMMONS.*

## Core Root (kp-commons-core-root)

## Modules Root (kp-commons-modules-root)

## Services Root (kp-commons-services-root)

## Parent (kp-commons-parent)

## Bill of Material (kp-commons-bom)

## Root (kp-commons-root)