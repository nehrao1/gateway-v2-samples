package org.example;

import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.models.*;

import java.util.HashSet;
import java.util.List;

public class GatewayV2BatchSuite {
    // Perform batch operations against a Cosmos DB container using Gateway V2.
    public static void execute(CosmosAsyncContainer container) {
        try {
            // First batch
            CosmosBatch batch1 = CosmosBatch.createCosmosBatch(new PartitionKey("pk1"));
            Customer c1 = new Customer("1", "pk1", "John Doe", "Seattle");
            Customer c2 = new Customer("2", "pk1", "Sarah Smith", "Seattle");
            Customer c3 = new Customer("3", "pk1", "Joseph White", "Chicago");
            batch1.createItemOperation(c1);
            batch1.createItemOperation(c2);
            batch1.createItemOperation(c3);

            CosmosBatchResponse response1 = container
                    .executeCosmosBatch(batch1)
                    .block();

            if (response1 != null && response1.getStatusCode() == 200) {
                System.out.println("Batch 1 executed successfully with status code: " + response1.getStatusCode());
            } else {
                System.out.println("Batch 1 execution failed with status code: " + response1.getStatusCode());
            }

            System.out.println("Validating operations from batch 1 ...");
            queryAndValidate(container, List.of(c1, c2, c3));

            // Second batch
            CosmosBatch batch2 = CosmosBatch.createCosmosBatch(new PartitionKey("pk2"));
            Customer c4 = new Customer("4", "pk2", "Chris Hunter", "Toronto");
            Customer c5 = new Customer("5", "pk2", "Robert Wilson", "Vancouver");
            batch2.createItemOperation(c4);
            batch2.createItemOperation(c5);

            CosmosBatchResponse response2 = container
                    .executeCosmosBatch(batch2)
                    .block();

            if (response2 != null && response2.getStatusCode() == 200) {
                System.out.println("Batch 2 executed successfully with status code: " + response2.getStatusCode());
            } else {
                System.out.println("Batch 2 execution failed with status code: " + response2.getStatusCode());
            }

            System.out.println("Validating operations from batch 2 ...");
            queryAndValidate(container, List.of(c1, c2, c3, c4, c5));

            // Third batch
            CosmosBatch batch3 = CosmosBatch.createCosmosBatch(new PartitionKey("pk1"));
            Customer c6 = new Customer("6", "pk1", "Samuel Miller", "New Mexico");
            batch3.createItemOperation(c6);
            batch3.deleteItemOperation("1");

            CosmosBatchResponse response3 = container
                    .executeCosmosBatch(batch3)
                    .block();

            if (response3 != null && response3.getStatusCode() == 200) {
                System.out.println("Batch 3 executed successfully with status code: " + response3.getStatusCode());
            } else {
                System.out.println("Batch 3 execution failed with status code: " + response3.getStatusCode());
            }

            System.out.println("Validating operations from batch 3 ...");
            queryAndValidate(container, List.of(c2, c3, c4, c5, c6));

        } catch (Exception e) {
            System.err.println("An error occurred during GatewayV2BatchSuite execution: " + e.getMessage());
        }
    }

    public static void queryAndValidate(CosmosAsyncContainer container, List<Customer> expected) {
        String query = "select * from c";
        SqlQuerySpec querySpec = new SqlQuerySpec(query);
        FeedResponse<Customer> response = container
                .queryItems(querySpec, new CosmosQueryRequestOptions(), Customer.class)
                .byPage()
                .blockFirst();

        if (response != null) {
            List<Customer> items = response.getResults();
            if (items.size() == expected.size() && new HashSet<>(items).containsAll(expected)) {
                System.out.println("Query results match expected items.");
            } else {
                String errorMessage = "Query results do not match expected items.";
                System.out.println(errorMessage);
                throw new RuntimeException(errorMessage);
            }

            // Validate that the operation used the Gateway 2.0 endpoint
            Utils.validateThinClientEndpointUsed(response.getCosmosDiagnostics());
        } else {
            String errorMessage = "Query execution failed";
            System.out.println(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }
}
