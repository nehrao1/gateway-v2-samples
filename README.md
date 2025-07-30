# Gateway 2.0 Samples

A functioning application that provides examples of how to perform supported operations against a Gateway 2.0 enabled Cosmos DB account.

## Overview
This project demonstrates how to interact with Azure Cosmos DB using Gateway 2.0.

The intention of this sample is to demonstrate the basic syntax and properties required to perform simple operations from each of the supported operation types.

## Prerequisites
- JDK 11+
- Maven 3.6+
- Gateway 2.0 enabled Azure Cosmos DB account
- Setting of the following JVM properties (shown in Main.java setup):
  - `COSMOS.THINCLIENT_ENABLED=true`
  - `COSMOS.HTTP2_ENABLED=true`

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── org/
│   │       └── example/
│   │           ├── Customer.java               # Data model class
│   │           ├── GatewayV2BatchSuite.java    # Suite for performing batch operations
│   │           ├── GatewayV2BulkSuite.java     # Suite for performing bulk operations
│   │           ├── GatewayV2CrudSuite.java     # Suite for performing CRUD operations
│   │           ├── GatewayV2QuerySuite.java    # Suite for performing query operations
│   │           ├── Main.java                   # Application entry point
│   │           ├── Utils.java                  # Utils
│   └── resources/
│       └── simplelogger.properties             # Logging configuration
```

## Building the Project

```bash
mvn clean package
```

## Running the Application

```bash
java -jar target/gateway-v2-samples-1.0-SNAPSHOT.jar <account endpoint> <account key> <suite>
```
E.g.:
```
java -jar target/gateway-v2-samples-1.0-SNAPSHOT.jar "https://your-account.documents.azure.com:443/" "your-master-key" "crud"
```

## Supported Operations
Supported values for `<suite>`:

| Value | Executes                 | Operations                                   |
|-------|--------------------------|----------------------------------------------|
| crud  | GatewayV2CrudSuite.java  | Create, Read, Replace, Upsert, Patch, Delete |
| query | GatewayV2QuerySuite.java | Single & Cross Partition Queries             |
| batch | GatewayV2BatchSuite.java | Batch Operations                             |
| bulk  | GatewayV2BulkSuite.java  | Bulk Operations                              |