# Badge Generator

`badge-generator` is a **reactive SVG badge generator for GitHub Actions workflows**, able to produce dynamically rendered build badges for both public and private GitHub repositories.

## Configuration

You must include the following properties in the [application.yml](https://github.com/dsibilio/badge-generator/blob/main/src/main/resources/config/application.yml) file:

```yaml
github:
  token: <my-github-PAT>
```

## Usage

Run the `gha-badge-generator` with `mvnw spring-boot:run`.
Once the application is up and running, you can query the APIs specified in the [openapi.yaml specification file](https://github.com/dsibilio/badge-generator/blob/main/src/main/resources/openapi/openapi.yaml) in order to generate static/dynamic badges.

## Examples

### Generating a Static Badge

`http://localhost:8080/badges/{my-message}?label=my-label&labelColor=GREY&messageColor=BLUE`

### Generating a Dynamic GitHub Action Workflow Badge

`http://localhost:8080/badges/github/{owner}/{repository}/{workflowId}`

## Testing

Run the Unit Tests with `mvnw verify`. 

Run all tests, including Integration Tests, with `mvnw verify -PtestIT`.

## Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.7.3/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.7.3/maven-plugin/reference/html/#build-image)
* [Spring cache abstraction](https://docs.spring.io/spring-boot/docs/2.7.3/reference/htmlsingle/#boot-features-caching)

## Guides
The following guides illustrate how to use some features concretely:

* [Caching Data with Spring](https://spring.io/guides/gs/caching/)

