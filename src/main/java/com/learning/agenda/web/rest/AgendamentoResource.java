package com.learning.agenda.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.learning.agenda.domain.Agendamento;

import com.learning.agenda.repository.AgendamentoRepository;
import com.learning.agenda.repository.search.AgendamentoSearchRepository;
import com.learning.agenda.web.rest.util.HeaderUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Agendamento.
 */
@RestController
@RequestMapping("/api")
public class AgendamentoResource {

    private final Logger log = LoggerFactory.getLogger(AgendamentoResource.class);
        
    @Inject
    private AgendamentoRepository agendamentoRepository;

    @Inject
    private AgendamentoSearchRepository agendamentoSearchRepository;

    /**
     * POST  /agendamentos : Create a new agendamento.
     *
     * @param agendamento the agendamento to create
     * @return the ResponseEntity with status 201 (Created) and with body the new agendamento, or with status 400 (Bad Request) if the agendamento has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/agendamentos")
    @Timed
    public ResponseEntity<Agendamento> createAgendamento(@Valid @RequestBody Agendamento agendamento) throws URISyntaxException {
        log.debug("REST request to save Agendamento : {}", agendamento);
        if (agendamento.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("agendamento", "idexists", "A new agendamento cannot already have an ID")).body(null);
        }
        Agendamento result = agendamentoRepository.save(agendamento);
        agendamentoSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/agendamentos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("agendamento", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /agendamentos : Updates an existing agendamento.
     *
     * @param agendamento the agendamento to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated agendamento,
     * or with status 400 (Bad Request) if the agendamento is not valid,
     * or with status 500 (Internal Server Error) if the agendamento couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/agendamentos")
    @Timed
    public ResponseEntity<Agendamento> updateAgendamento(@Valid @RequestBody Agendamento agendamento) throws URISyntaxException {
        log.debug("REST request to update Agendamento : {}", agendamento);
        if (agendamento.getId() == null) {
            return createAgendamento(agendamento);
        }
        Agendamento result = agendamentoRepository.save(agendamento);
        agendamentoSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("agendamento", agendamento.getId().toString()))
            .body(result);
    }

    /**
     * GET  /agendamentos : get all the agendamentos.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of agendamentos in body
     */
    @GetMapping("/agendamentos")
    @Timed
    public List<Agendamento> getAllAgendamentos() {
        log.debug("REST request to get all Agendamentos");
        List<Agendamento> agendamentos = agendamentoRepository.findAllWithEagerRelationships();
        return agendamentos;
    }

    /**
     * GET  /agendamentos/:id : get the "id" agendamento.
     *
     * @param id the id of the agendamento to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the agendamento, or with status 404 (Not Found)
     */
    @GetMapping("/agendamentos/{id}")
    @Timed
    public ResponseEntity<Agendamento> getAgendamento(@PathVariable Long id) {
        log.debug("REST request to get Agendamento : {}", id);
        Agendamento agendamento = agendamentoRepository.findOneWithEagerRelationships(id);
        return Optional.ofNullable(agendamento)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /agendamentos/:id : delete the "id" agendamento.
     *
     * @param id the id of the agendamento to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/agendamentos/{id}")
    @Timed
    public ResponseEntity<Void> deleteAgendamento(@PathVariable Long id) {
        log.debug("REST request to delete Agendamento : {}", id);
        agendamentoRepository.delete(id);
        agendamentoSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("agendamento", id.toString())).build();
    }

    /**
     * SEARCH  /_search/agendamentos?query=:query : search for the agendamento corresponding
     * to the query.
     *
     * @param query the query of the agendamento search 
     * @return the result of the search
     */
    @GetMapping("/_search/agendamentos")
    @Timed
    public List<Agendamento> searchAgendamentos(@RequestParam String query) {
        log.debug("REST request to search Agendamentos for query {}", query);
        return StreamSupport
            .stream(agendamentoSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
