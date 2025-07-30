package org.example;

import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.implementation.OperationType;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosPatchOperations;
import com.azure.cosmos.models.PartitionKey;

import java.util.UUID;

class GatewayV2CrudSuite {
    // Perform document CRUD operations against a Cosmos DB container using Gateway V2.
    static void execute(CosmosAsyncContainer container) {
        try {
            System.out.println("Executing GatewayV2CrudSuite");

            String id1 = UUID.randomUUID().toString();
            String partitionKeyValue = id1;
            Customer customer = new Customer(id1, partitionKeyValue, "John Doe", "Seattle");

            // create
            CosmosItemResponse<Customer> createResponse = container.createItem(customer).block();
            validate(createResponse, 201, OperationType.Create);

            // read
            CosmosItemResponse<Customer> readResponse = container.readItem(id1, new PartitionKey(partitionKeyValue), Customer.class).block();
            validate(readResponse, 200, OperationType.Read);

            // replace
            String id2 = UUID.randomUUID().toString();
            Customer customer2 = new Customer(id2, partitionKeyValue, "Jane Doe", "New York");
            CosmosItemResponse<Customer> replaceResponse = container.replaceItem(customer2, id1, new PartitionKey(id1)).block();
            validate(replaceResponse, 200, OperationType.Replace);

            CosmosItemResponse<Customer> readAfterReplaceResponse = container.readItem(id2, new PartitionKey(id1), Customer.class).block();
            validate(readAfterReplaceResponse, 200, OperationType.Read);

            // upsert
            String id3 = UUID.randomUUID().toString();
            Customer customer3 = new Customer(id3, partitionKeyValue, "Jane Doe", "Chicago");
            CosmosItemResponse<Customer> upsertResponse = container.upsertItem(customer3, new PartitionKey(partitionKeyValue), new CosmosItemRequestOptions()).block();
            validate(upsertResponse, 201, OperationType.Upsert);

            CosmosItemResponse<Customer> readAfterUpsertResponse = container.readItem(id3, new PartitionKey(partitionKeyValue), Customer.class).block();
            validate(readAfterUpsertResponse, 200, OperationType.Read);

            // patch
            CosmosPatchOperations patchOperations = CosmosPatchOperations.create();
            patchOperations.replace("/city", "Florida");
            CosmosItemResponse<Customer> patchResponse = container.patchItem(id3, new PartitionKey(partitionKeyValue), patchOperations, Customer.class).block();
            validate(patchResponse, 200, OperationType.Patch);

            CosmosItemResponse<Customer> readAfterPatchResponse = container.readItem(id3, new PartitionKey(partitionKeyValue), Customer.class).block();
            validate(readAfterPatchResponse, 200, OperationType.Read);

            // delete
            CosmosItemResponse<Object> deleteResponse = container.deleteItem(id3, new PartitionKey(id1)).block();
            if (deleteResponse.getStatusCode() == 204) {
                System.out.println("Deleted item with status code " + deleteResponse.getStatusCode());
            } else {
                System.out.println("Failed to delete item. Status code " + deleteResponse.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("An error occurred during GatewayV2CrudSuite execution: " + e.getMessage());
        }
    }

    private static void validate(CosmosItemResponse<Customer> response, int expectedStatusCode, OperationType operationType) {
        if (response.getStatusCode() == expectedStatusCode) {
            System.out.println("Operation type " + operationType + " succeeded with status code " + response.getStatusCode());
            if (operationType == OperationType.Read) {
                Customer c = response.getItem();
                System.out.println("Item read: " + response.getItem().toString());
            }
            // Validate that the operation used the Gateway 2.0 endpoint
            Utils.validateThinClientEndpointUsed(response.getDiagnostics());
        } else {
            String errorMessage = "Operation type " + operationType + " failed with status code " + response.getStatusCode();
            System.out.println(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }
}
