# Genie logiciel ENSISA 2024-2025

## URL Staging and Prod:

 - [ ] https://staging.cluster.ensisa.uha.fr/gl2425-testsquad1-genie-logiciel-ensisa-2024-2025/
 - [ ] https://prod.cluster.ensisa.uha.fr/gl2425-testsquad1-genie-logiciel-ensisa-2024-2025/

```sh
source config/config-env-mac.sh
mvn install
mvn -pl mantest-app jetty:run
```