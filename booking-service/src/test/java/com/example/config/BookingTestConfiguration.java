package com.example.config;

import com.example.booking.client.InventoryClient;
import com.example.booking.client.PricingClient;
import com.example.eventing.event.producer.BookingEventProducer;
import com.example.payment.client.PaymentClient;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class BookingTestConfiguration {
    @Bean
    public InventoryClient inventoryClient() {
        return Mockito.mock(InventoryClient.class);
    }

    @Bean
    public PaymentClient paymentClient() {
        return Mockito.mock(PaymentClient.class);
    }

    @Bean
    public PricingClient pricingClient() {
        return Mockito.mock(PricingClient.class);
    }

    @Bean
    public BookingEventProducer bookingEventProducer() {
        return Mockito.mock(BookingEventProducer.class);
    }

}
