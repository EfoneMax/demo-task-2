package com.example.demo2;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ClientImpl implements Task2Data.Client {

    @Override
    public Task2Data.Event readData() {
        List<Task2Data.Address> addresses = IntStream.range(0, 10)
                .mapToObj(i -> new Task2Data.Address(UUID.randomUUID().toString(), UUID.randomUUID().toString()))
                .collect(Collectors.toList());

        Task2Data.Payload payload = new Task2Data.Payload(UUID.randomUUID().toString(),
                UUID.randomUUID().toString().getBytes());

        return new Task2Data.Event(addresses, payload);
    }

    @Override
    public Task2Data.Result sendData(Task2Data.Address dest, Task2Data.Payload payload) {
        Task2Data.Result result = ThreadLocalRandom.current().nextBoolean() ? Task2Data.Result.ACCEPTED : Task2Data.Result.REJECTED;

        if (Task2Data.Result.ACCEPTED == result) {
            System.out.println(LocalDateTime.now() + "address " + dest + " sent success");
        } else {
            System.out.println(LocalDateTime.now() + "address " + dest + " rejected");
        }

        return result;
    }
}
