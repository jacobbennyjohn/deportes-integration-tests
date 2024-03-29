package com.univision;

import com.univision.storage.Record;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 */
public interface EventRepository extends ElasticsearchRepository<Record, String>, PagingAndSortingRepository<Record, String> {
    Iterable<Record> findByEventId(String eventId, Sort sort);
}
