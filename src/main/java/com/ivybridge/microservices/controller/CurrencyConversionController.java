package com.ivybridge.microservices.controller;

import com.ivybridge.microservices.model.CurrencyConversion;
import com.ivybridge.microservices.model.CurrencyExchangeServiceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CurrencyConversionController {

    @Autowired
    private CurrencyExchangeServiceProxy proxy;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion convertCurrency(@PathVariable String from,
                                              @PathVariable String to,
                                              @PathVariable BigDecimal quantity) {

        // Feign - Problem 1
        Map<String,String> uriVariables = new HashMap<>();
        uriVariables.put("from",from);
        uriVariables.put("to",to);

        //Calling the other service with the variables
        ResponseEntity<CurrencyConversion> responseEntity = new RestTemplate().getForEntity(
                "http://localhost:8000/currency-exchange/from/{from}/to/{to}",
                CurrencyConversion.class,
                uriVariables);
        // getting the response back
        CurrencyConversion response = responseEntity.getBody();

        logger.info("{}",response);

        // creating new response bean
        return new CurrencyConversion(response.getId(),from, to, response.getConversionMultiple()
                                        ,quantity,quantity.multiply(response.getConversionMultiple())
                                        ,response.getPort());

    }

    @GetMapping("/currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion convertCurrencyFeign(@PathVariable String from,
                                              @PathVariable String to,
                                              @PathVariable BigDecimal quantity) {

        // Feign - Problem 1
        // getting the response back
        CurrencyConversion response = proxy.retrieveExchangeValue(from, to);

        // creating new response bean
        return new CurrencyConversion(response.getId(),from, to, response.getConversionMultiple()
                ,quantity,quantity.multiply(response.getConversionMultiple())
                ,response.getPort());

    }
}
