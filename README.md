# Trino HTTP Group Provider

[![Build and Test](https://github.com/baptistegh/trino-http-group-provider/actions/workflows/build.yml/badge.svg)](https://github.com/baptistegh/trino-http-group-provider/actions/workflows/build.yml)

A Trino plugin that enables group retrieval from an HTTP endpoint for user authorization and access control.

## Features

- HTTP-based group provider for Trino
- Configurable endpoint URL
- Bearer token authentication support
- Fault-tolerant HTTP client implementation
- Compatible with Trino's security model

## Requirements

- Java 24
- Maven 3.9.11 or later
- Trino server (tested with version 429)

## Installation

1. Build the plugin:
   ```bash
   mvn clean package
   ```

2. Copy the plugin to your Trino installation:
   ```bash
   cp target/trino-http-group-provider-*.jar $TRINO_HOME/plugin/http-group-provider/
   ```

## Configuration

1. Create `etc/group-provider.properties` in your Trino configuration directory:
   ```properties
   group-provider.name=http
   http-group-provider.endpoint=https://your-api-endpoint/groups
   http-group-provider.auth-token=your-bearer-token
   ```

2. Configure the group provider in your Trino server's `etc/config.properties`:
   ```properties
   group-provider=http
   ```

## HTTP Endpoint Requirements

The HTTP server should expose an endpoint compatible with Open Policy Agent (OPA) input/output formats.

The HTTP endpoint should:
- Accept `POST` requests
- Return a JSON object with the `result` key containing a list of strings representing group names
- Use Bearer token authentication (if needed)
- Return 404 (or other non 2XX status code) for unknown users

### Request Format

```json
{
  "input": {
    "user": "<username>"
  }
}
```

### Response Format

```json
{
  "result": [
    "<group>",
    "<group>"
  ]
}
```

## Building from Source

```bash
git clone https://github.com/baptistegh/trino-http-group-provider.git
cd trino-http-group-provider
mvn clean verify
```

### Running Tests

The project includes both unit and integration tests using JUnit 5:

- Run unit tests only:
  ```bash
  mvn test
  ```

- Run all tests (including integration tests):
  ```bash
  mvn verify
  ```

- Run code style check:
  ```bash
  mvn checkstyle:check
  ```

The project uses Google Java Style for code formatting.

## Docker

A Dockerfile is provided to build and package the plugin:

```bash
docker build -t trino-http-group-provider .
```

## Contributing

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin feature/my-new-feature`
5. Submit a pull request

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## Support

For issues, questions, or contributions, please create an issue in the GitHub repository.