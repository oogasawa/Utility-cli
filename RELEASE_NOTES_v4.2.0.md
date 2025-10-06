## Utility-cli v4.2.0

### Highlights
- Introduced `UtilityCliHelpFormatter` and `UtilityCliHelpFormatterBuilder`, enabling per-command customization of help output without leaving Commons CLI.
- `CommandRepository` now accepts default and command-scoped formatter builders so commands can set headings, descriptions, and example blocks independently.
- Added `CommandRepositoryHelpTest` ensuring help-flag execution paths exercise the new formatter.

### Documentation
- README now documents the formatter builder workflow and updates all usage snippets to reference the 4.2.0 fat jar.

### Build
- Project version bumped to `4.2.0`; run `./mvnw clean package` to regenerate `target/Utility-cli-4.2.0-fat.jar` for distribution.

Artifacts located under `target/` after packaging:
- `Utility-cli-4.2.0.jar`
- `Utility-cli-4.2.0-fat.jar`
