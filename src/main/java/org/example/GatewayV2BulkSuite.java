// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package org.example;

import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.models.CosmosBulkOperationResponse;
import com.azure.cosmos.models.CosmosBulkOperations;
import com.azure.cosmos.models.PartitionKey;
import reactor.core.publisher.Flux;

import java.util.List;

public class GatewayV2BulkSuite {
    // Perform bulk operations against a Cosmos DB container using Gateway V2.
    public static void execute(CosmosAsyncContainer container) {
        try {
            Customer customer1 = new Customer("1", "pk1", "Alice Johnson", "Los Angeles");
            Customer customer2 = new Customer("2", "pk1", "Bob Smith", "San Francisco");
            Customer customer3 = new Customer("3", "pk1", "Charlie Brown", "Chicago");

            Flux<CosmosBulkOperationResponse<Customer>> responsesFlux = container.executeBulkOperations(Flux.just(
                    CosmosBulkOperations.getCreateItemOperation(customer1, new PartitionKey("pk1")),
                    CosmosBulkOperations.getCreateItemOperation(customer2, new PartitionKey("pk1")),
                    CosmosBulkOperations.getCreateItemOperation(customer3, new PartitionKey("pk1"))
            ));

            List<CosmosBulkOperationResponse<Customer>> responses = responsesFlux.collectList().block();

            if (responses != null && responses.size() != 3) {
                throw new RuntimeException("Expected 3 responses, but got " + responses.size());
            }

            for (CosmosBulkOperationResponse<Customer> response : responses) {
                if (response.getResponse().getStatusCode() != 201) {
                    throw new RuntimeException("Expected status code 201, but got " + response.getResponse().getStatusCode());
                }

                // Validate that the operation used the Gateway 2.0 endpoint
                Utils.validateThinClientEndpointUsed(response.getResponse().getCosmosDiagnostics());
            }

            System.out.println("Bulk operations executed successfully.");
        } catch (Exception e) {
            System.err.println("Error executing GatewayV2BulkSuite: " + e.getMessage());
        }
    }
}
