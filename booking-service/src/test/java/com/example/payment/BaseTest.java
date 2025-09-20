package com.example.payment;

import com.example.config.BookingTestConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ContextConfiguration(classes = {BookingTestConfiguration.class})
public abstract class BaseTest {


}