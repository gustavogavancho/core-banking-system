package com.swiftline.client.config;

import com.swiftline.client.domain.model.Client;
import com.swiftline.client.infrastructure.persistence.entity.ClientEntity;
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

        // ClientEntity -> Client (aplana campos de person)
        mapper.createTypeMap(ClientEntity.class, Client.class)
                .addMapping(ClientEntity::getId, Client::setId)
                .addMappings(m -> {
                    m.map(src -> src.getPerson().getName(), Client::setName);
                    m.map(src -> src.getPerson().getGender(), Client::setGender);
                    m.map(src -> src.getPerson().getAge(), Client::setAge);
                    m.map(src -> src.getPerson().getIdentification(), Client::setIdentification);
                    m.map(src -> src.getPerson().getAddress(), Client::setAddress);
                    m.map(src -> src.getPerson().getPhoneNumber(), Client::setPhoneNumber);
                });

        return mapper;
    }
}
