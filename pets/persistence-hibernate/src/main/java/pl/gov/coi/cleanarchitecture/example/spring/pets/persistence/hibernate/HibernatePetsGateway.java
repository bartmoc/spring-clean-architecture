package pl.gov.coi.cleanarchitecture.example.spring.pets.persistence.hibernate;

import lombok.RequiredArgsConstructor;
import pl.gov.coi.cleanarchitecture.example.spring.pets.domain.model.entity.Pet;
import pl.gov.coi.cleanarchitecture.example.spring.pets.domain.model.gateway.PetsGateway;
import pl.gov.coi.cleanarchitecture.example.spring.pets.persistence.hibernate.entity.PetData;
import pl.gov.coi.cleanarchitecture.example.spring.pets.persistence.hibernate.mapper.PetToPetDataMapper;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszyński</a>
 * @since 2018-01-18
 */
@RequiredArgsConstructor
final class HibernatePetsGateway implements PetsGateway {

  private final EntityManager entityManager;
  private final PetToPetDataMapper mapper;

  @Override
  public Iterable<Pet> getAllActive() {
    TypedQuery<PetData> query = entityManager.createQuery(
      "SELECT p " +
        "FROM PetData p " +
        "LEFT JOIN FETCH OwnershipData o " +
        "LEFT JOIN FETCH Person pp", PetData.class
    );
    query.setMaxResults(100);
    List<PetData> results = query.getResultList();
    return results.stream()
      .map(mapper::map)
      .collect(Collectors.toList());
  }

  @Override
  public Pet persistNew(Pet pet) {
    PetData data = mapper.map(pet);
    entityManager.persist(data);
    PetData persisted = entityManager.find(PetData.class, data.getId());
    return mapper.map(persisted);
  }
}