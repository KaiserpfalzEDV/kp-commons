---
title: "Services, Level 2"
menuTitle: "Services"
pre: "<b>5.2.3</b> "
weight: 230
---

The services rely on the [→ core libraries](/05_buildingblocks/02_1_core/).

```mermaid
graph TD
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

    api[kp-commons-api]
    core[kp-commons-core]
    test(kp-commons-test)
    parent[kp-commons-parent]
```

*Graph: Dependencies between the modules and core libraries.*

## Print Publication Lookup via DNB (kp-commons-dnb-lookup)

## EAN lookup via Relaxed Communications GmbH (kp-commons-ean-search)

## Sending SMS via Seven.io (kp-commons-sms77)
