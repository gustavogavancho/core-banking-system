package com.swiftline.account;

import com.intuit.karate.junit5.Karate;

public class AccountKarateTest {

    @Karate.Test
    Karate testAccount() {
        return Karate.run("account").relativeTo(getClass());
    }

    @Karate.Test
    Karate testTransaction() {
        return Karate.run("transaction").relativeTo(getClass());
    }
}
