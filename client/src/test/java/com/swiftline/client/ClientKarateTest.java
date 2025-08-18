package com.swiftline.client;

import com.intuit.karate.junit5.Karate;

public class ClientKarateTest {

    @Karate.Test
    Karate testClient() {
        return Karate.run("client").relativeTo(getClass());
    }
}
