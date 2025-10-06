## Utility-cli v4.2.0

### Highlights
- Rebuilt `UtilityCliHelpFormatter` around an ordered section model (Usage / Options / custom blocks) so help screens can be rearranged freely.
- Added a section-first `UtilityCliHelpFormatterBuilder` that supports `addUsageSection`, `addOptionsSection`, `addCustomSection`, etc., replacing the fixed heading setters.
- `CommandRepository` now accepts default and command-scoped formatter builders so commands can set headings, descriptions, and example blocks independently.
- Added `CommandRepositoryHelpTest` ensuring help-flag execution paths exercise the new formatter.

### Documentation
- README now documents the formatter builder workflow and updates all usage snippets to reference the 4.2.0 fat jar.

### Build
- Project version bumped to `4.2.0`; run `./mvnw clean package` to regenerate `target/Utility-cli-4.2.0.jar` (shaded) alongside `target/original-Utility-cli-4.2.0.jar` (unshaded) for distribution.
