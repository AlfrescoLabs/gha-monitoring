# GHA Badge Generator

| Old Badge | Honest Badge |
|-----------|--------------|
| [![Build Status](https://github.com/Alfresco/alfresco-community-repo/actions/workflows/hackathon.yml/badge.svg?branch=hack/hackathon-demo)](https://github.com/Alfresco/alfresco-community-repo/actions?query=branch%3Ahack%2Fhackathon-demo) | [![Honest Build Status](https://mvdaxgtsxz.eu-west-1.awsapprunner.com/badges/github/Alfresco/alfresco-community-repo/hackathon.yml?branch=hack/hackathon-demo)](https://github.com/Alfresco/alfresco-community-repo/actions?query=branch%3Ahack%2Fhackathon-demo) |

`gha-badge-generator` is a **reactive SVG badge generator for GitHub Actions workflows**, able to produce dynamically rendered build badges for both public and private GitHub repositories.
These badges differ from the standard GitHub Action badges, as they're actually honest when it comes down to the status of the most recent workflow.

## Configuration

You must include the following properties in the [application.yml](https://github.com/AlfrescoLabs/gha-monitoring/blob/main/apps/gha-badge-generator/src/main/resources/config/application.yml) file:

```yaml
github:
  token: <my-github-PAT>
```

You can optionally override the default cache duration _(default time unit if unspecified: `seconds`)_:

```yaml
github:
  token: <my-github-PAT>
  cacheDuration: 60S
```

## Usage

Run the `gha-badge-generator` with `mvnw spring-boot:run`.
Once the application is up and running, you can query the APIs specified in the [openapi.yaml specification file](https://github.com/AlfrescoLabs/gha-monitoring/blob/main/apps/gha-badge-generator/src/main/resources/openapi/openapi.yaml) in order to generate static/dynamic badges.

## Examples

### Generating a Static Badge

`http://localhost:8080/badges/{my-message}?label=my-label&labelColor=GREY&messageColor=BLUE`

### Generating a Dynamic GitHub Action Workflow Badge

`http://localhost:8080/badges/github/{owner}/{repository}/{workflowId}`

The `workflowId` can either be the actual workflow id, or the name of the workflow file _(e.g.: `create-env.yml`)_.

The default branch is `master`, you can also specify a different branch as follows:
`http://localhost:8080/badges/github/{owner}/{repository}/{workflowId}?branch=develop`

### Generating a Dynamic GitHub Pull Request Badge

`http://localhost:8080/badges/prurl/{owner}/{repository}?pattern=fix%28versions%29%3A%20update&user=alfresco-build&field=DESCRIPTION`

Generates a badge that shows the status of the latest pull request containing the string in the pattern field and created by the user in user field. The field parameter indicates if the PR title or description must be shown in the badge.

A successful request returns a badge with one of the following color:
* ![#97ca00](https://via.placeholder.com/15/97ca00/97ca00.png) GREEN: success
* ![#97ca00](https://via.placeholder.com/15/e05d44/e05d44.png) RED: build failed
* ![#fe7d37](https://via.placeholder.com/15/fe7d37/fe7d37.png) ORANGE: build in progress
* ![#dfb317](https://via.placeholder.com/15/dfb317/dfb317.png) YELLOW: merged with error or closed without merging
* ![#9f9f9f](https://via.placeholder.com/15/9f9f9f/9f9f9f.png) LIGHT GREY: build cancelled
* ![#555](https://via.placeholder.com/15/555/555.png) GREY: status unknown

### Generating a Dynamic GitHub Pull Request Redirect URL

`http://localhost:8080/badges/prurl/{owner}/{repository}?pattern=fix%28versions%29%3A%20update&user=alfresco-build`

Redirect the request to the latest pull request containing the string in the pattern field and created by the user in user field. The field parameter indicates if the PR title or description must be shown in the badge.

This API must be used with previous API in order to redirect the user to the right PR when clicking on the badge, so it must have the same parameters, eg:

`
[![prstatus](http://.../badges/prstatus/Activiti/activiti-cloud?pattern=fix%28versions%29%3A%20update&user=alfresco-build&field=DESCRIPTION)](http://.../badges/prurl/Activiti/activiti-cloud?pattern=fix%28versions%29%3A%20update&user=alfresco-build&field=DESCRIPTION)`

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
