package com.example.urlShortner.service;

import com.example.urlShortner.repositories.ShortUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CounterService {

    private final ShortUrlRepository repository;

    /**
     * REQUIRES_NEW: fetching the next sequence value must commit
     * independently of the outer save transaction. Postgres sequences
     * are non-transactional by design (nextval() is never rolled back,
     * even if the enclosing transaction fails) — this propagation just
     * keeps our code's transactional boundaries honest and explicit
     * about that fact.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public long nextValue() {
        Long value = repository.getNextSequenceValue();
        if (value == null) {
            throw new IllegalStateException("Failed to obtain next value from short_code_seq");
        }
        return value;
    }
}
