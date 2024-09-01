package org.example.service;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Customer;
import org.example.domains.CustomerEntity;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class CustomerService {



    DynamoDbTable<CustomerEntity> table;

    public CustomerService() {
        var ddb = DynamoDbClient.builder()
                .region(Region.US_EAST_2)
                .build();
        var enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(ddb).build();
        table = enhancedClient.table("customer", TableSchema.fromBean(CustomerEntity.class));

    }


    public Customer createCustomer(APIGatewayProxyRequestEvent requestEvent) {

        try {
            System.out.println(requestEvent.getBody());
            var customerEntity = new ObjectMapper().readValue(requestEvent.getBody(), CustomerEntity.class) ;
            table.putItem(customerEntity);
            var customerGet =  table.getItem(Key.builder().partitionValue(customerEntity.getCustomerId()).build());
            return getCustomer(customerGet.getCustomerId());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    public Customer getCustomer(String customerId) {
        var customerEntity = table.getItem(Key.builder().partitionValue(customerId).build());
        return new Customer(customerEntity.getFirstName(), customerEntity.getLastName(), customerEntity.getCustomerId());
    }

}
