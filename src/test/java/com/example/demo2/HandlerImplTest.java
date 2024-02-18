package com.example.demo2;

import org.junit.jupiter.api.Test;

class HandlerImplTest {

    Task2Data.Client client = new ClientImpl();

    Task2Data.Handler handler = new HandlerImpl(client);

    @Test
    void performOperation() {
        handler.performOperation();
    }
}