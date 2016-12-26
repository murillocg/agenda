package com.learning.agenda.repository.search;

import com.learning.agenda.domain.Servico;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Servico entity.
 */
public interface ServicoSearchRepository extends ElasticsearchRepository<Servico, Long> {
}
