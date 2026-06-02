package com.scrappi.main.repository;

import com.scrappi.main.model.Technology;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TechRepository extends JpaRepository<Technology,Long> {
}
