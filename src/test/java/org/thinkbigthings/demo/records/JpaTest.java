package org.thinkbigthings.demo.records;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.thinkbigthings.demo.records.jpa.Store;
import org.thinkbigthings.demo.records.jpa.StoreRecord;
import org.thinkbigthings.demo.records.jpa.StoreRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JpaTest {

    private StoreRepository storeRepository = mock(StoreRepository.class);

    private Store entity = new Store("Big Store Inc", "https://bigstore.com");
    private StoreRecord storeProjection = new StoreRecord(entity.getName(), entity.getWebsite());
    private PageRequest requestFirstPage = PageRequest.of(0, 10);

    @BeforeEach
    public void setup() {

        when(storeRepository.findByName(eq(entity.getName()))).thenReturn(Optional.of(entity));

        when(storeRepository.loadSummaries(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(storeProjection)));
    }

    @Test
    public void testReturnRecord() {

        Page<StoreRecord> storePage = storeRepository.loadSummaries(requestFirstPage);

        assertEquals(1, storePage.getTotalElements());
    }

    @Test
    public void testReturnEntity() {

        Optional<Store> entityResults = storeRepository.findByName(entity.getName());

        assertTrue(entityResults.isPresent());
        assertEquals(entity.getName(), entityResults.get().getName());
    }

}
