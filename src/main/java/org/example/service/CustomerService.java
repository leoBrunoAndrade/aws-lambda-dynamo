package org.example.service;

//import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
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

    DynamoDbEnhancedClient enhancedClient;

    DynamoDbTable<CustomerEntity> table;

    public CustomerService() {

        Region region = Region.US_EAST_2;
        DynamoDbClient ddb = DynamoDbClient.builder()
                .region(region)
                .build();

       enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(ddb).build();

        table = enhancedClient.table("customer", TableSchema.fromBean(CustomerEntity.class));

    }


    public Customer createCustomer(APIGatewayProxyRequestEvent requestEvent) {

        try {
            System.out.println(requestEvent.getBody());
            var customerEntity = new ObjectMapper().readValue(requestEvent.getBody(), CustomerEntity.class) ;
            //var customerEntity = new CustomerEntity();
            //customerEntity.setFirstName(customer.firstName());
            //customerEntity.setLastName(customer.firstName());
            //customerEntity.setCustomerId(customer.customerId());
            table.putItem(customerEntity);
            var customerGet =  table.getItem(Key.builder().partitionValue(customerEntity.getCustomerId()).build());
            return new Customer(customerGet.getFirstName(), customerGet.getLastName(), customerGet.getCustomerId());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

}
