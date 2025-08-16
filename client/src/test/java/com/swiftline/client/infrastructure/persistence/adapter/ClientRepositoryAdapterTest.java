package com.swiftline.client.infrastructure.persistence.adapter;

import com.swiftline.client.domain.model.Client;
import com.swiftline.client.infrastructure.persistence.entity.ClientEntity;
import com.swiftline.client.infrastructure.persistence.entity.PersonEntity;
import com.swiftline.client.infrastructure.persistence.repository.ClientJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ClientRepositoryAdapterTest {

    private ClientJpaRepository clientJpaRepository;
    private ClientRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        clientJpaRepository = mock(ClientJpaRepository.class);
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setSkipNullEnabled(true).setMatchingStrategy(MatchingStrategies.STRICT);
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
        adapter = new ClientRepositoryAdapter(clientJpaRepository, mapper);
    }

    @Test
    void save_shouldMapDomainToEntities_andReturnDomainWithId() {
        Client domain = sampleClient();

        ClientEntity saved = ClientEntity.builder()
                .id(42L)
                .person(PersonEntity.builder()
                        .id(42L)
                        .name(domain.getName())
                        .gender(domain.getGender())
                        .age(domain.getAge())
                        .identification(domain.getIdentification())
                        .address(domain.getAddress())
                        .phoneNumber(domain.getPhoneNumber())
                        .build())
                .password(domain.getPassword())
                .state(domain.getState())
                .build();
        when(clientJpaRepository.save(any(ClientEntity.class))).thenReturn(saved);

        Client result = adapter.save(domain);

        assertEquals(42L, result.getId());
        assertEquals(domain.getName(), result.getName());

        ArgumentCaptor<ClientEntity> captor = ArgumentCaptor.forClass(ClientEntity.class);
        verify(clientJpaRepository).save(captor.capture());
        ClientEntity toPersist = captor.getValue();
        assertNull(toPersist.getId()); // id se asigna por la DB
        assertNotNull(toPersist.getPerson());
        assertEquals(domain.getName(), toPersist.getPerson().getName());
    }

    @Test
    void findById_shouldMapEntityToDomain() {
        when(clientJpaRepository.findById(5L)).thenReturn(Optional.of(entityWithId(5L)));
        Optional<Client> opt = adapter.findById(5L);
        assertTrue(opt.isPresent());
        assertEquals(5L, opt.get().getId());
    }

    @Test
    void findAll_shouldReturnMappedList() {
        when(clientJpaRepository.findAll()).thenReturn(List.of(entityWithId(1L), entityWithId(2L)));
        List<Client> list = adapter.findAll();
        assertEquals(2, list.size());
        assertEquals(1L, list.get(0).getId());
        assertEquals(2L, list.get(1).getId());
    }

    @Test
    void update_shouldApplyChangesAndSave() {
        ClientEntity existing = entityWithId(9L);
        when(clientJpaRepository.findById(9L)).thenReturn(Optional.of(existing));
        when(clientJpaRepository.save(any(ClientEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        Client changes = sampleClient();
        changes.setName("Changed");
        changes.setPassword("newpwd");
        changes.setState(false);

        Client updated = adapter.update(9L, changes);
        assertEquals(9L, updated.getId());
        assertEquals("Changed", updated.getName());
        assertEquals(false, updated.getState());

        ArgumentCaptor<ClientEntity> captor = ArgumentCaptor.forClass(ClientEntity.class);
        verify(clientJpaRepository).save(captor.capture());
        ClientEntity toSave = captor.getValue();
        assertEquals("Changed", toSave.getPerson().getName());
        assertEquals("newpwd", toSave.getPassword());
    }

    @Test
    void deleteById_shouldCallRepo_whenExists() {
        when(clientJpaRepository.existsById(3L)).thenReturn(true);
        adapter.deleteById(3L);
        verify(clientJpaRepository).deleteById(3L);
    }

    @Test
    void deleteById_shouldNotCallDelete_whenNotExists() {
        when(clientJpaRepository.existsById(3L)).thenReturn(false);
        adapter.deleteById(3L);
        verify(clientJpaRepository, never()).deleteById(anyLong());
    }

    @Test
    void existsById_shouldDelegate() {
        when(clientJpaRepository.existsById(1L)).thenReturn(true);
        assertTrue(adapter.existsById(1L));
    }

    private Client sampleClient() {
        Client c = new Client();
        c.setName("John");
        c.setGender("M");
        c.setAge(20);
        c.setIdentification("ID");
        c.setAddress("Addr");
        c.setPhoneNumber("555");
        c.setPassword("pwd");
        c.setState(true);
        return c;
    }

    private ClientEntity entityWithId(Long id) {
        return ClientEntity.builder()
                .id(id)
                .person(PersonEntity.builder()
                        .id(id)
                        .name("John")
                        .gender("M")
                        .age(20)
                        .identification("ID")
                        .address("Addr")
                        .phoneNumber("555")
                        .build())
                .password("pwd")
                .state(true)
                .build();
    }
}
