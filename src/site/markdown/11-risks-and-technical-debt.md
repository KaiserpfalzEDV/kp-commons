# Risks and Technical Debts

## Business or Domain Risks

| ID     | Risk             |
| :----- | :--------------- |
| RD-001 | Some frameworks like Vaadin develop very fast. It is hard to keep up. |
| RD-002 | The Versions follow the spring-boot version. Having release managed by an independent external organisation may put a burden on the collection development. |

## Technical Risks

| ID     | Risk             |
| :----- | :--------------- |
| RT-001 | Keeping up with Spring-Boot may lead to problems known as "DLL-hell" where different frameworks have the same dependency in different versions which may lead to incompatibilities. |

## Technical Debt

| ID     | Debt Description |
| :----- | :--------------- |
| TD-001 | Old Code has still no test harness. |
| TD-002 | The documentation is not finished. There is lot's of boilerplate. |
