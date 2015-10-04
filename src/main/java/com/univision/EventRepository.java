package com.univision;

import com.univision.storage.Record;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by jbjohn on 10/4/15.
 */
public interface EventRepository extends ElasticsearchRepository<Record, String>{
}
