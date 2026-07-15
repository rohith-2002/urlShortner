package com.example.urlShortner.repositories;

import com.example.urlShortner.entity.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {

    Optional<ShortUrl> findByShortCode(String shortCode);

    Optional<ShortUrl> findByOriginalUrlHash(String originalUrlHash);

    boolean existsByShortCode(String shortCode);

    // Pulls the next value from the Postgres sequence directly.
    // nextval() is atomic at the DB level — safe under concurrent calls.
    @Query(value = "SELECT nextval('short_code_seq')", nativeQuery = true)
    Long getNextSequenceValue();
}
