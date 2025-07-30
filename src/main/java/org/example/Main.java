package org.example;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.models.CosmosContainerProperties;

import java.util.UUID;

public class Main {
    private static CosmosAsyncClient client = null;
    private static String dbName = "sample-database-" + UUID.randomUUID();
    private static String containerName = "sample-container";

    public static void main(String[] args) {
        String endpoint = "";
        String key = "";
        String suite = "";

        if (args.length < 3) {
            System.out.println("Usage: java -jar target/gateway-v2-samples-1.0-SNAPSHOT.jar <endpoint> <key> <suite>");
            System.out.println("Suite options: crud, query, batch, bulk");
            System.exit(1);
        } else {
            endpoint = args[0];
            key = args[1];
            suite = args[2];
        }

        try {
            System.out.println("Setting up resources ... ");
            setup(endpoint, key);
            System.out.println("Setup complete");

            CosmosAsyncContainer container = client.getDatabase(dbName).getContainer(containerName);

            switch (suite) {
                case "crud":
                    System.out.println("Executing CRUD operations ...");
                    GatewayV2CrudSuite.execute(container);
                    break;
                case "query":
                    System.out.println("Executing Query operations ...");
                    GatewayV2QuerySuite.execute(container);
                    break;
                case "batch":
                    System.out.println("Executing Batch operations ...");
                    GatewayV2BatchSuite.execute(container);
                    break;
                case "bulk":
                    System.out.println("Executing Bulk operations ...");
                    GatewayV2BulkSuite.execute(container);
                    break;
                default:
                    System.out.println("Unknown suite: " + suite);
            }

        } finally {
            cleanup();
        }
    }

    private static void setup(String endpoint, String key) {
        // Set the system properties for Gateway 2.0
        System.setProperty("COSMOS.THINCLIENT_ENABLED", "true");
        System.setProperty("COSMOS.HTTP2_ENABLED", "true");

        client  = new CosmosClientBuilder()
                .endpoint(endpoint)
                .key(key)
                .gatewayMode()
                .buildAsyncClient();

        System.out.println("CosmosAsyncClient created with endpoint: " + endpoint);

        System.out.println("Attempting to create database ... " + dbName);
        client.createDatabase(dbName).block();
        System.out.println("Database created");

        System.out.println("Attempting to create container ... " + containerName);
        CosmosContainerProperties containerDef = new CosmosContainerProperties(containerName, "/partitionKey");
        client.getDatabase(dbName).createContainer(containerDef).block();
        System.out.println("Container created");
    }

    private static void cleanup() {
        try {
            System.clearProperty("COSMOS.THINCLIENT_ENABLED");
            System.clearProperty("COSMOS.HTTP2_ENABLED");
        } catch (Exception e) {
            System.err.println("Error clearing system properties: " + e.getMessage());
        }

        try {
            // Delete the database
            if (client != null) {
                client.getDatabase(dbName).delete().block();
                System.out.println(dbName + " deleted.");
            }
        } catch (Exception e) {
            System.err.println("Error deleting database: " + e.getMessage());
        }

        try {
            // Close the client to release resources
            if (client != null) {
                client.close();
                System.out.println("CosmosAsyncClient closed.");
            }
        } catch (Exception e) {
            System.err.println("Error closing CosmosAsyncClient: " + e.getMessage());
        }
    }
}