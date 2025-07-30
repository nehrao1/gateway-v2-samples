package org.example;

import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.implementation.OperationType;
import com.azure.cosmos.models.*;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;

class GatewayV2QuerySuite {
    static Customer customer1 = new Customer("1", "1", "John Doe", "Seattle");
    static Customer customer2 = new Customer("2", "2", "Sarah Smith", "Seattle");
    static Customer customer3 = new Customer("3", "3", "Joseph White", "Chicago");
    static Customer customer4 = new Customer("4", "4", "Jason Brown", "Los Angeles");

    static List<Customer> customers = Arrays.asList(customer1, customer2, customer3, customer4);

    // Perform document query operations against a Cosmos DB container using Gateway V2.
    static void execute(CosmosAsyncContainer container) {
        try {
            System.out.println("Executing GatewayV2QuerySuite");

            // Populate the container with some documents
            for (Customer customer : customers) {
                container.createItem(customer, new PartitionKey(customer.partitionKey), null).block();
            }

            // Single partition query
            System.out.println("Executing executeSelectPartitionKeyQuery ...");
            executeSelectPartitionKeyQuery(container);

            // Cross partition queries
            System.out.println("Executing executeSelectCustomersInSeattleQuery ...");
            executeSelectCustomersInSeattleQuery(container);
            System.out.println("Executing executeSelectAllQuery ...");
            executeSelectAllQuery(container);

        } catch (Exception e) {
            System.err.println("An error occurred during GatewayV2QuerySuite execution: " + e.getMessage());
        }
    }

    private static void executeSelectAllQuery(CosmosAsyncContainer container) {
        String query = "select * from c";
        SqlQuerySpec querySpec = new SqlQuerySpec(query);
        FeedResponse<Customer> response = container
                .queryItems(querySpec, new CosmosQueryRequestOptions(), Customer.class)
                .byPage()
                .blockFirst();

        validateQuery(response.getResults(), customers);
    }

    private static void executeSelectPartitionKeyQuery(CosmosAsyncContainer container) {
        String query = "select * from c WHERE c.partitionKey=@id";
        SqlQuerySpec querySpec = new SqlQuerySpec(query);
        querySpec.setParameters(Arrays.asList(new SqlParameter("@id", "1")));
        CosmosQueryRequestOptions requestOptions = new CosmosQueryRequestOptions().setPartitionKey(new PartitionKey("1"));
        FeedResponse<Customer> response = container
                .queryItems(querySpec, requestOptions, Customer.class)
                .byPage()
                .blockFirst();

        validateQuery(response.getResults(), Arrays.asList(customer1));
    }

    private static void executeSelectCustomersInSeattleQuery(CosmosAsyncContainer container) {
        String query = "select * from c WHERE c.city=@city";
        SqlQuerySpec querySpec = new SqlQuerySpec(query);
        querySpec.setParameters(Arrays.asList(new SqlParameter("@city", "Seattle")));
        FeedResponse<Customer> response = container
                .queryItems(querySpec, new CosmosQueryRequestOptions(), Customer.class)
                .byPage()
                .blockFirst();

        validateQuery(response.getResults(), Arrays.asList(customer1, customer2));
    }

    private static void validateQuery(List<Customer> actual, List<Customer> expected) {
        System.out.println("");

        System.out.println("Expected items:");
        expected.forEach(customer -> {System.out.println(customer.toString());});

        System.out.println("Actual items returned:");
        actual.forEach(customer -> {System.out.println(customer.toString());});

        if (actual.size() == expected.size() && new HashSet<>(actual).containsAll(expected)) {
            System.out.println("Query results match expected items.");
        } else {
            String errorMessage = "Query results do not match expected items.";
            System.out.println(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }
}
