package com.univision;

import com.univision.storage.Information;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 */
public interface InformationRepository extends ElasticsearchRepository<Information, String> {

    List<Information> findById(String id);

    List<Information> findByIdAndType(String id, String type);
}
