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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JpaTest {

    private StoreRepository storeRepository = mock(StoreRepository.class);

    private Store entity = new Store("Big Store Inc", "https://bigstore.com");

    @BeforeEach
    public void setup() {

        when(storeRepository.findByName(eq(entity.getName()))).thenReturn(Optional.of(entity));

        List<StoreRecord> summaries = List.of(new StoreRecord(entity.getName(), entity.getWebsite()));
        when(storeRepository.loadSummaries(any(Pageable.class))).thenReturn(new PageImpl<>(summaries));
    }

    @Test
    public void testReturnRecord() {

        Page<StoreRecord> storePage = storeRepository.loadSummaries(PageRequest.of(0, 10));

        assertEquals(1, storePage.getTotalElements());
    }

}
