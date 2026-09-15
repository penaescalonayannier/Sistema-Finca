package com.kynsoft.share.core.infrastructure.repository;

import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

/**
 * Custom JPA repository base class that correctly handles save() for entities
 * with pre-set IDs (e.g., UUID generated before persist).
 *
 * Problem: Spring Data's default SimpleJpaRepository.save() uses merge() when
 * the entity ID is not null. In Hibernate 6.6+ (Spring Boot 3.5.x), merge()
 * on a new entity with pre-set ID throws StaleObjectStateException.
 *
 * Solution: Check if the entity actually exists in the DB before deciding
 * between persist() (new entity) and merge() (existing entity).
 * Always uses merge() for safety — it handles both insert and update correctly
 * without cascade issues that persist() has with detached related entities.
 */
@NoRepositoryBean
public class SmartSaveRepository<T, ID> extends SimpleJpaRepository<T, ID> {

    private final JpaEntityInformation<T, ?> entityInformation;
    private final EntityManager em;

    public SmartSaveRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
        this.em = entityManager;
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public <S extends T> S save(S entity) {
        ID id = (ID) entityInformation.getId(entity);

        if (id == null) {
            // No ID — definitely new, persist directly
            em.persist(entity);
            return entity;
        }

        // Entity has an ID set — check if it actually exists in the DB
        T existing = em.find(entityInformation.getJavaType(), id);
        if (existing == null) {
            // Entity doesn't exist in DB — it's new, use persist()
            em.persist(entity);
            return entity;
        }

        // If the found entity IS the same object (same transaction, same persistence context),
        // it's already managed — just return it, dirty checking will flush changes
        if (existing == entity) {
            return entity;
        }

        // Entity exists in DB but is a different object — detach the found entity to avoid
        // "A different object with the same identifier value was already
        // associated with the session" errors, then merge the updated entity
        em.detach(existing);
        return em.merge(entity);
    }
}
