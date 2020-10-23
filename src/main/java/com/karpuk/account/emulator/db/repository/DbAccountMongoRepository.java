package com.karpuk.account.emulator.db.repository;

import com.karpuk.account.emulator.db.model.DbAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DbAccountMongoRepository extends MongoRepository<DbAccount, Long> {
}
