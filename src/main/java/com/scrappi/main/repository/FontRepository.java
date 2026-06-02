package com.scrappi.main.repository;

import com.scrappi.main.model.Font;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FontRepository extends JpaRepository<Font,Long> {
}
