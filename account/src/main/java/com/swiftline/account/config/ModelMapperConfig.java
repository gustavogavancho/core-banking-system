package com.swiftline.account.config;

import com.swiftline.account.domain.model.Transaction;
import com.swiftline.account.infrastructure.persistence.entity.TransactionEntity;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setMatchingStrategy(MatchingStrategies.STRICT);

        // TransactionEntity -> Transaction (mapea account.id -> accountId)
        mapper.createTypeMap(TransactionEntity.class, Transaction.class)
                .addMapping(src -> src.getAccount().getId(), Transaction::setAccountId);

        return mapper;
    }
}

