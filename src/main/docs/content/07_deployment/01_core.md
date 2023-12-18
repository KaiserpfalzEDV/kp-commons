---
title: "Core Components"
pre: "<b>7.1</b> "
weight: 10
---

This is the usage of the core components.

## Bill of Material

To include all needed dependencies and manage the versions of all components the BOM may be imported in the `dependencyManagement` section of the `pom.xml`.

```xml
...

<dependencyManagement>
    <dependencies>
        ...

        <dependency>
            <groupId>de.kaiserpfalz-edv.commons</groupId>
            <artifactId>kp-commons-bom</artifactId>
            <version>3.2.0-SNAPSHOT</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>

        ...
    </dependencies>
</dependencyManagement>

...
```

## API

```xml
...

<dependencies>
    ...

    <dependency>
        <groupId>de.kaiserpfalz-edv.commons</groupId>
        <artifactId>kp-commons-api</artifactId>
    </dependency>

    ...
</dependencies>

...
```

## Core Implementation

```xml
...

<dependencies>
    ...

    <dependency>
        <groupId>de.kaiserpfalz-edv.commons</groupId>
        <artifactId>kp-commons-core</artifactId>
    </dependency>

    ...
</dependencies>

...
```

## Test Support

```xml
...

<dependencies>
    ...

    <dependency>
        <groupId>de.kaiserpfalz-edv.commons</groupId>
        <artifactId>kp-commons-test</artifactId>
        <scope>test</scope>
    </dependency>

    ...
</dependencies>

...
```
