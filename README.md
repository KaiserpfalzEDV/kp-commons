# KP-COMMONS - A library for handling common problems Kaiserpfalz EDV-Service encounters

> You don't need to be crazy to be my friend ... ok, maybe you do. It's just more fun that way.
>
> -- @blue_eyed_darkness on TikTok

[![CodeQL](https://github.com/KaiserpfalzEDV/kp-commons/actions/workflows/release.yml/badge.svg)](https://github.com/KaiserpfalzEDV/kp-commons/actions/workflows/release.yml)
[![CodeQL](https://github.com/KaiserpfalzEDV/kp-commons/actions/workflows/github-pages.yml/badge.svg)](https://github.com/KaiserpfalzEDV/kp-commons/actions/workflows/github-pages.yml)
[![JavaRunner](https://github.com/KaiserpfalzEDV/kp-commons/actions/workflows/build-and-publish-java-runner-to-quay.yml/badge.svg)](https://github.com/KaiserpfalzEDV/kp-commons/actions/workflows/build-and-publish-java-runner-to-quay.yml)
[![CodeQL](https://github.com/KaiserpfalzEDV/kp-commons/actions/workflows/publish-helm-webserice.yml/badge.svg)](https://github.com/KaiserpfalzEDV/kp-commons/actions/workflows/publish-helm-webserice.yml)
[![CodeQL](https://github.com/KaiserpfalzEDV/kp-commons/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/KaiserpfalzEDV/kp-commons/actions/workflows/codeql-analysis.yml)
[![CI](https://github.com/KaiserpfalzEDV/kp-commons/actions/workflows/ci.yml/badge.svg)](https://github.com/KaiserpfalzEDV/kp-commons/actions/workflows/ci.yml)

## Abstract

This is a library of software artifacts I found useful in a number of projects.
So I assembled them in this library.

## License

The license for the software is GPL 3.0 or newer.
Parts of the software may be licensed under other licences like MIT or Apache 2.0 - these files are marked appropriately.

* _libravatar_ is published under MIT license from Alessandro Leite.
* Parts of kp-commons-vaadin are published under the [Unlicense](https://unlicense.org).
  This includes the packages:
  * _de.kaiserpfalzedv.commons.vaadin.nav_
  * _de.kaiserpfalzedv.commons.vaadin.users_

  And of course every single source file with the unlicense as header.
  I value the decision of [Vaadin](https://vaadin.com) to use this way to sponsor code into the public domain and since I used that code, I use of course the same license for the derivative work.
  Pull requests for these parts need to be accompanied by the text

  > I dedicate any and all copyright interest in this software to the
  > public domain. I make this dedication for the benefit of the public at
  > large and to the detriment of my heirs and successors. I intend this
  > dedication to be an overt act of relinquishment in perpetuity of all
  > present and future rights to this software under copyright law.```

  as laid out on https://unlicense.org.
* Parts of kp-commons-dnb-lookup are [published under MIT license from Technische Informationsbibliothek (TIB), Hannover](https://github.com/TIBHannover/library-profile-service)
  This is the package _de.kaiserpfalzedv.services.dnb.marcxml_ which does the heavy lifting of converting the MARC-XML data into our own internal model.
  Of course, I changed it to use our model and not the model of TIB.

## Architecture

tl;dr (ok, only the bullshit bingo words):

* Immutable Objects (where frameworks allow)
* Relying heavily on generated code
* 100 % test coverage of human generated code
* Every line of code not written is bug free!

Code test coverage for human generated code should be 100%, machine generated code is considered bug free until proven wrong.
Every line that needs not be written is a bug free line without need to test it.
So aim for not writing code.
And yes, I'm struggling with this requirement.
Beat me.

## Included libraries

* base
  * kp-commons-bom
  * kp-commons-parent
  * kp-spring-boot-parent
  * kp-checkstyle
* core
  * kp-commons-api
  * kp-commons-core
  * kp-commons-test
* modules
  * kp-commons-jpa
  * kp-commons-rest
  * (kp-commons-vaadin - WIP)
* services
  * kp-commons-ean-search
  * kp-commons-sms77
  * kp-commons-dnb-lookup
* Helm Charts
  * postgresql (from [postgres-operator-examples](https://github.com/CrunchyData/postgres-operator-examples.git)) under Apache 2.0 License
  * microservice (for spring-boot based microservices with optional pre-configured OIDC and Postgres)

## Distribution

At the moment there is no distribution of this software.

## Note from the author

This software is meant do be perfected not finished.

If someone is interested in getting it faster, we may team up.
I'm open for that.
But be warned: I want to _do it right_.
So no shortcuts to get faster.
And be prepared for some basic discussions about the architecture or software design :-).

---
Bensheim, 2021-05-24
