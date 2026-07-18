# Decisions

Architecture decisions for this repo are recorded as **WorkSpec Decision
artifacts** under [`docs/decisions/`](docs/decisions/) — schema-bound
`*.decision.yaml` files (options scored on weighted criteria, costed in ongoing
maintenance effort, with a recorded outcome), validated against
`https://schema.workspec.io/v1alpha1/decision.schema.json`.

| ID | Decision | Status |
| -- | -------- | ------ |
| [D1](docs/decisions/d1-json-serialisation.decision.yaml) | JSON serialisation for `oasp-client` (hand-rolled zero-dep codec) | decided |
| [D2](docs/decisions/d2-resource-discriminator.decision.yaml) | Resource discriminator & self-describing types (FHIR-style `resourceType`) | exploring |

Open them in [WorkSpec Studio](https://studio.workspec.io) for the scored
comparison view, or read the YAML directly. The shared cost model lives in
[`docs/decisions/decisions.catalog.yaml`](docs/decisions/decisions.catalog.yaml).
