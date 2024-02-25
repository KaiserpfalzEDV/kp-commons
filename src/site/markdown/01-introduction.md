# Introduction and Goals

> You don't need to be crazy to be my friend ... ok, maybe you do. It's just more fun that way.
>
> -- @blue_eyed_darkness on TikTok

## Requirements Overview

* Provide a core library with functionality used in the most of the projects.
* Provide modules for special tasks not found everywhere (like persistence, REST, Vaadin, ...)
* Provide specialized service integrations to external providers (like looking up books, EAN or sending SMS)

## Quality Goals

tl;dr (ok, only the bullshit bingo words):

* Immutable Objects (where frameworks allow)
* Relying heavily on generated code
* 100 % test coverage of human generated code
* Every line of code not written is bug free!

Code test coverage for human generated code should be 100%, machine generated code is considered bug free until proven wrong.
Every line that needs not be written is a bug free line without need to test it.
So aim for not writing code.
And yes, I'm struggling with this goals.
Beat me.

## Stakeholder

| Name  | Description |
| :---- | :---------- |
| Roland Lichti | User of these libraries. And designer. And coder. And architect. And ... well, you sort it out ... |
