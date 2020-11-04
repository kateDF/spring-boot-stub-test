package com.karpuk.account.emulator.test.client;

import com.karpuk.account.emulator.test.model.TestDbAccount;
import com.karpuk.account.emulator.test.model.TestDbTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;

import static com.karpuk.account.emulator.test.utils.TestDataUtils.getRandomName;
import static com.karpuk.account.emulator.test.utils.TestDataUtils.getRandomTransactionValue;

@Service
public class MongoDbClient {

    @Autowired
    private MongoTemplate mongoTemplate;

    public TestDbAccount createRandomAccountInDb() {
        TestDbAccount dbObject = mongoTemplate.insert(new TestDbAccount(null, getRandomName(10), LocalDate.now(),
                Arrays.asList(
                        new TestDbTransaction("Other", getRandomTransactionValue()),
                        new TestDbTransaction("Paycheck", getRandomTransactionValue()))));
        return dbObject;
    }

    public long getDbSize() {
        long documentsCount = mongoTemplate.getCollection("accounts").countDocuments();
        return documentsCount;
    }

}
