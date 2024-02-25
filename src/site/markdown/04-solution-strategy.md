# Solution Strategy

| Quality Goal | Scenario | Solution Approach | Link to Details |
| :---- | :---- | :---- | :---- |
| SOLID | Extending the library | By strict domain design the seperate modules or java packages should be closed to changes but the collection stays open to extension. | |
| YAGNI | Providing functionality | Only functionality needed by existing projects should be added. | |
| DRY | Adding functionality | I heavily rely on existing frameworks, libraries and services. | |
| DDD | Working with business domains | Trying to seperate the different domains as needed to concentrate on solving one functionality a time. | Following the old unix principle: solving one problem - but solving it fully. |
| Tested Software | TDD | Using Test Driven Design to provide a full test harness and designing testable software. | I'm still struggling there. |
| Security | Code bugs and security issues with dependencies | Using static code analysis and dependency scans to keep the software vulnerabilities at a minimum level. | |
