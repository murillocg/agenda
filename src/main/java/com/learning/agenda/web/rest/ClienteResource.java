package com.learning.agenda.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.learning.agenda.domain.Cliente;

import com.learning.agenda.repository.ClienteRepository;
import com.learning.agenda.repository.search.ClienteSearchRepository;
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
 * REST controller for managing Cliente.
 */
@RestController
@RequestMapping("/api")
public class ClienteResource {

    private final Logger log = LoggerFactory.getLogger(ClienteResource.class);
        
    @Inject
    private ClienteRepository clienteRepository;

    @Inject
    private ClienteSearchRepository clienteSearchRepository;

    /**
     * POST  /clientes : Create a new cliente.
     *
     * @param cliente the cliente to create
     * @return the ResponseEntity with status 201 (Created) and with body the new cliente, or with status 400 (Bad Request) if the cliente has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/clientes")
    @Timed
    public ResponseEntity<Cliente> createCliente(@Valid @RequestBody Cliente cliente) throws URISyntaxException {
        log.debug("REST request to save Cliente : {}", cliente);
        if (cliente.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("cliente", "idexists", "A new cliente cannot already have an ID")).body(null);
        }
        Cliente result = clienteRepository.save(cliente);
        clienteSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/clientes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("cliente", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /clientes : Updates an existing cliente.
     *
     * @param cliente the cliente to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated cliente,
     * or with status 400 (Bad Request) if the cliente is not valid,
     * or with status 500 (Internal Server Error) if the cliente couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/clientes")
    @Timed
    public ResponseEntity<Cliente> updateCliente(@Valid @RequestBody Cliente cliente) throws URISyntaxException {
        log.debug("REST request to update Cliente : {}", cliente);
        if (cliente.getId() == null) {
            return createCliente(cliente);
        }
        Cliente result = clienteRepository.save(cliente);
        clienteSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("cliente", cliente.getId().toString()))
            .body(result);
    }

    /**
     * GET  /clientes : get all the clientes.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of clientes in body
     */
    @GetMapping("/clientes")
    @Timed
    public List<Cliente> getAllClientes() {
        log.debug("REST request to get all Clientes");
        List<Cliente> clientes = clienteRepository.findAll();
        return clientes;
    }

    /**
     * GET  /clientes/:id : get the "id" cliente.
     *
     * @param id the id of the cliente to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the cliente, or with status 404 (Not Found)
     */
    @GetMapping("/clientes/{id}")
    @Timed
    public ResponseEntity<Cliente> getCliente(@PathVariable Long id) {
        log.debug("REST request to get Cliente : {}", id);
        Cliente cliente = clienteRepository.findOne(id);
        return Optional.ofNullable(cliente)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /clientes/:id : delete the "id" cliente.
     *
     * @param id the id of the cliente to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/clientes/{id}")
    @Timed
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        log.debug("REST request to delete Cliente : {}", id);
        clienteRepository.delete(id);
        clienteSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("cliente", id.toString())).build();
    }

    /**
     * SEARCH  /_search/clientes?query=:query : search for the cliente corresponding
     * to the query.
     *
     * @param query the query of the cliente search 
     * @return the result of the search
     */
    @GetMapping("/_search/clientes")
    @Timed
    public List<Cliente> searchClientes(@RequestParam String query) {
        log.debug("REST request to search Clientes for query {}", query);
        return StreamSupport
            .stream(clienteSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
