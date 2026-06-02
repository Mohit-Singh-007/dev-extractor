package com.scrappi.main.repository;

import com.scrappi.main.model.Technology;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechRepository extends JpaRepository<Technology,Long> {
}
