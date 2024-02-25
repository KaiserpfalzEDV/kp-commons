# Architecture Constraints

1. Limited capacity for development.
   I'm a solo developer and the library is not my bread and butter job.
   I need to earn a living with other work, so the time is limited.
2. While aiming for immutable objects within the library sometimes the frameworks or even the standardized APIs prevent that.
   In that cases an [anti-corruption layer](https://awesome-architecture.com/cloud-design-patterns/anti-corruption-layer-pattern/) should be implemented.
