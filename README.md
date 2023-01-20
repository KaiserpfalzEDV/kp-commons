# KP-COMMONS - A library for handling common problems Kaiserpfalz EDV-Service encounters

> You don't need to be crazy to be my friend ... ok, maybe you do. It's just more fun that way.
>
> -- @blue_eyed_darkness on TikTok

![Maven](https://github.com/KaiserpfalzEDV/kp-commons/workflows/CI/badge.svg)

## Abstract
This is a library of software artifacts I found useful in a number of projects. 
So I assembled them in this library.


## License
The license for the software is GPL 3.0 or newer. 
Parts of the software may be licensed under other licences like MIT or Apache 2.0 - the files are marked appropriately. 

* libravatar is published unter MIT license from Alessandro Leite.

## Architecture

tl;dr (ok, only the bullshit bingo words):
- Immutable Objects (where frameworks allow)
- Relying heavily on generated code
- 100 % test coverage of human generated code
- Every line of code not written is bug free!

Code test coverage for human generated code should be 100%, machine generated code is considered bugfree until proven wrong. 
Every line that needs not be written is a bug free line without need to test it. 
So aim for not writing code.
And yes, I'm struggling with this requirement.
Beat me.


## Included libraries

* core
  * kp-commons-api
  * kp-commons-core
  * (kp-commons-bom)
  * (kp-commons-parent)
* modules
  * kp-commons-jpa
  * kp-commons-rest
  * kp-commons-quarkus
  * kp-commons-vaadin
* services
  * kp-commons-ean-search
* testsupport
  * kp-core-testsupport
  * oauth2-testsupport (defunct)


## Distribution
Currently there is no distribution of this software.


## Note from the author
This software is meant do be perfected not finished.

If someone is interested in getting it faster, we may team up. 
I'm open for that. 
But be warned: I want to do it_right_.
So no short cuts to get faster. 
And be prepared for some basic discussions about the architecture or software design :-).

---
Bensheim, 2021-05-24