---
title: "Quality Tree"
pre: "<b>10.1</b> "
weight: 10
---

The following tree is an overview over the quality requirements (non functional requirements) of the software.
The requirements are listed in [→ Quality Scenarios]({{< relref "02_quality_scenarios.md" >}}).
The functional requireements are listed in [→ Requirements]({{< relref "../01_introduction/04_requirements.md" >}}).

```mermaid
graph LR
    s[Software] --> m[Maintainability]
    s --> e[Efficiency]

    m --> M01

    e --> E01

```

*Graph: Strucutre of the quality requirements of the software.*