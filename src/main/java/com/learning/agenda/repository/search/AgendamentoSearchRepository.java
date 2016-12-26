package com.learning.agenda.repository.search;

import com.learning.agenda.domain.Agendamento;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Agendamento entity.
 */
public interface AgendamentoSearchRepository extends ElasticsearchRepository<Agendamento, Long> {
}
