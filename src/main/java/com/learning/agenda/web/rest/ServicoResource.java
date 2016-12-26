package com.learning.agenda.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.learning.agenda.domain.Servico;

import com.learning.agenda.repository.ServicoRepository;
import com.learning.agenda.repository.search.ServicoSearchRepository;
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
 * REST controller for managing Servico.
 */
@RestController
@RequestMapping("/api")
public class ServicoResource {

    private final Logger log = LoggerFactory.getLogger(ServicoResource.class);
        
    @Inject
    private ServicoRepository servicoRepository;

    @Inject
    private ServicoSearchRepository servicoSearchRepository;

    /**
     * POST  /servicos : Create a new servico.
     *
     * @param servico the servico to create
     * @return the ResponseEntity with status 201 (Created) and with body the new servico, or with status 400 (Bad Request) if the servico has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/servicos")
    @Timed
    public ResponseEntity<Servico> createServico(@Valid @RequestBody Servico servico) throws URISyntaxException {
        log.debug("REST request to save Servico : {}", servico);
        if (servico.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("servico", "idexists", "A new servico cannot already have an ID")).body(null);
        }
        Servico result = servicoRepository.save(servico);
        servicoSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/servicos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("servico", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /servicos : Updates an existing servico.
     *
     * @param servico the servico to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated servico,
     * or with status 400 (Bad Request) if the servico is not valid,
     * or with status 500 (Internal Server Error) if the servico couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/servicos")
    @Timed
    public ResponseEntity<Servico> updateServico(@Valid @RequestBody Servico servico) throws URISyntaxException {
        log.debug("REST request to update Servico : {}", servico);
        if (servico.getId() == null) {
            return createServico(servico);
        }
        Servico result = servicoRepository.save(servico);
        servicoSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("servico", servico.getId().toString()))
            .body(result);
    }

    /**
     * GET  /servicos : get all the servicos.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of servicos in body
     */
    @GetMapping("/servicos")
    @Timed
    public List<Servico> getAllServicos() {
        log.debug("REST request to get all Servicos");
        List<Servico> servicos = servicoRepository.findAll();
        return servicos;
    }

    /**
     * GET  /servicos/:id : get the "id" servico.
     *
     * @param id the id of the servico to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the servico, or with status 404 (Not Found)
     */
    @GetMapping("/servicos/{id}")
    @Timed
    public ResponseEntity<Servico> getServico(@PathVariable Long id) {
        log.debug("REST request to get Servico : {}", id);
        Servico servico = servicoRepository.findOne(id);
        return Optional.ofNullable(servico)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /servicos/:id : delete the "id" servico.
     *
     * @param id the id of the servico to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/servicos/{id}")
    @Timed
    public ResponseEntity<Void> deleteServico(@PathVariable Long id) {
        log.debug("REST request to delete Servico : {}", id);
        servicoRepository.delete(id);
        servicoSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("servico", id.toString())).build();
    }

    /**
     * SEARCH  /_search/servicos?query=:query : search for the servico corresponding
     * to the query.
     *
     * @param query the query of the servico search 
     * @return the result of the search
     */
    @GetMapping("/_search/servicos")
    @Timed
    public List<Servico> searchServicos(@RequestParam String query) {
        log.debug("REST request to search Servicos for query {}", query);
        return StreamSupport
            .stream(servicoSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
