package org.example.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Customer;
import org.example.service.CustomerService;


import java.util.HashMap;
import java.util.Map;

public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {

        CustomerService customerService = new CustomerService();
        var result = customerService.createCustomer(requestEvent);
        return createAPIResponse(result, 201);

    }


    private APIGatewayProxyResponseEvent createAPIResponse(Customer body, int statusCode){
        Map<String,String> headers = new HashMap<>();
        headers.put("Content-Type","application/json");
        headers.put("X-amazon-author","bruno");
        headers.put("X-amazon-apiVersion","v1");

        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        try {
            responseEvent.setBody(new ObjectMapper().writeValueAsString(body));

        responseEvent.setHeaders(headers);
        responseEvent.setStatusCode(statusCode);
        return responseEvent;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }


}
