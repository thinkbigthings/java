package org.thinkbigthings.demo.records.jpa;


import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Pageable;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    Optional<Store> findByName(String name);

    @Query("SELECT new org.thinkbigthings.demo.records.jpa.StoreRecord(s.name, s.website) " +
            "FROM Store s ORDER BY s.name ASC ")
    Page<StoreRecord> loadSummaries(Pageable page);
}
