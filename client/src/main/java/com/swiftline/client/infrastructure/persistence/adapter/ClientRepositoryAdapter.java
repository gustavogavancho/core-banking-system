package com.swiftline.client.infrastructure.persistence.adapter;

import com.swiftline.client.domain.model.Client;
import com.swiftline.client.domain.model.Person;
import com.swiftline.client.domain.repository.ClientRepository;
import com.swiftline.client.infrastructure.persistence.entity.ClientEntity;
import com.swiftline.client.infrastructure.persistence.entity.PersonEntity;
import com.swiftline.client.infrastructure.persistence.repository.ClientJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Transactional
public class ClientRepositoryAdapter implements ClientRepository {

    private final ClientJpaRepository clientJpaRepository;

    public ClientRepositoryAdapter(ClientJpaRepository clientJpaRepository) {
        this.clientJpaRepository = clientJpaRepository;
    }

    @Override
    public Client save(Client client) {
        // Crear PersonEntity desde dominio
        PersonEntity personEntity = toPersonEntity(client);
        // Construir ClientEntity con cascada para persistir person y compartir PK con @MapsId
        ClientEntity clientEntity = ClientEntity.builder()
                .person(personEntity)
                .password(client.getPassword())
                .state(client.getState())
                .build();
        ClientEntity saved = clientJpaRepository.save(clientEntity);
        return toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Client> findById(Long id) {
        return clientJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> findAll() {
        return clientJpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Client update(Long id, Client client) {
        ClientEntity existing = clientJpaRepository.findById(id).orElseThrow();
        // actualizar datos de person
        PersonEntity p = existing.getPerson();
        p.setName(client.getName());
        p.setGender(client.getGender());
        p.setAge(client.getAge());
        p.setIdentification(client.getIdentification());
        p.setAddress(client.getAddress());
        p.setPhoneNumber(client.getPhoneNumber());
        // actualizar datos de client
        existing.setPassword(client.getPassword());
        existing.setState(client.getState());
        ClientEntity saved = clientJpaRepository.save(existing);
        return toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        if (clientJpaRepository.existsById(id)) {
            clientJpaRepository.deleteById(id); // cascade remove eliminará person
        }
    }

    @Override
    public boolean existsById(Long id) {
        return clientJpaRepository.existsById(id);
    }

    private Client toDomain(ClientEntity entity) {
        PersonEntity p = entity.getPerson();
        return Client.builder()
                .id(entity.getId())
                .name(p.getName())
                .gender(p.getGender())
                .age(p.getAge())
                .identification(p.getIdentification())
                .address(p.getAddress())
                .phoneNumber(p.getPhoneNumber())
                .password(entity.getPassword())
                .state(entity.getState())
                .build();
    }

    private PersonEntity toPersonEntity(Person person) {
        return PersonEntity.builder()
                .name(person.getName())
                .gender(person.getGender())
                .age(person.getAge())
                .identification(person.getIdentification())
                .address(person.getAddress())
                .phoneNumber(person.getPhoneNumber())
                .build();
    }
}
