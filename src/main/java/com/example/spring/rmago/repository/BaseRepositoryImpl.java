package com.example.spring.rmago.repository;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

public class BaseRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID> implements BaseRepository<T, ID> {

    private final EntityManager entityManager;

    // 각 레파지토리에서 findAll() 같은 기본 메서드 호출만으로도 soft-delete 필터가 자동 적용됨
    public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager em) {
        super(entityInformation, em);
        this.entityManager = em;

        // deletedFilter 자동 활성화
        Session session = em.unwrap(Session.class);
        session.enableFilter("deletedFilter");
    }
}

