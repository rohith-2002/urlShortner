package com.example.urlShortner.repositories;

import com.example.urlShortner.entity.ClickEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {
    long countByShortCode(String shortCode);
    List<ClickEvent> findByShortCodeOrderByClickedAtDesc(String shortCode);
}