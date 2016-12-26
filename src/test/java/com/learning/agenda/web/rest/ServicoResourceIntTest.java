package com.learning.agenda.web.rest;

import com.learning.agenda.AgendaApp;

import com.learning.agenda.domain.Servico;
import com.learning.agenda.repository.ServicoRepository;
import com.learning.agenda.repository.search.ServicoSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ServicoResource REST controller.
 *
 * @see ServicoResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AgendaApp.class)
public class ServicoResourceIntTest {

    private static final String DEFAULT_DESCRICAO = "AAAAAAAAAA";
    private static final String UPDATED_DESCRICAO = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRICAO_DETALHADA = "AAAAAAAAAA";
    private static final String UPDATED_DESCRICAO_DETALHADA = "BBBBBBBBBB";

    @Inject
    private ServicoRepository servicoRepository;

    @Inject
    private ServicoSearchRepository servicoSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restServicoMockMvc;

    private Servico servico;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ServicoResource servicoResource = new ServicoResource();
        ReflectionTestUtils.setField(servicoResource, "servicoSearchRepository", servicoSearchRepository);
        ReflectionTestUtils.setField(servicoResource, "servicoRepository", servicoRepository);
        this.restServicoMockMvc = MockMvcBuilders.standaloneSetup(servicoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Servico createEntity(EntityManager em) {
        Servico servico = new Servico()
                .descricao(DEFAULT_DESCRICAO)
                .descricaoDetalhada(DEFAULT_DESCRICAO_DETALHADA);
        return servico;
    }

    @Before
    public void initTest() {
        servicoSearchRepository.deleteAll();
        servico = createEntity(em);
    }

    @Test
    @Transactional
    public void createServico() throws Exception {
        int databaseSizeBeforeCreate = servicoRepository.findAll().size();

        // Create the Servico

        restServicoMockMvc.perform(post("/api/servicos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(servico)))
            .andExpect(status().isCreated());

        // Validate the Servico in the database
        List<Servico> servicoList = servicoRepository.findAll();
        assertThat(servicoList).hasSize(databaseSizeBeforeCreate + 1);
        Servico testServico = servicoList.get(servicoList.size() - 1);
        assertThat(testServico.getDescricao()).isEqualTo(DEFAULT_DESCRICAO);
        assertThat(testServico.getDescricaoDetalhada()).isEqualTo(DEFAULT_DESCRICAO_DETALHADA);

        // Validate the Servico in ElasticSearch
        Servico servicoEs = servicoSearchRepository.findOne(testServico.getId());
        assertThat(servicoEs).isEqualToComparingFieldByField(testServico);
    }

    @Test
    @Transactional
    public void createServicoWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = servicoRepository.findAll().size();

        // Create the Servico with an existing ID
        Servico existingServico = new Servico();
        existingServico.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restServicoMockMvc.perform(post("/api/servicos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingServico)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Servico> servicoList = servicoRepository.findAll();
        assertThat(servicoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkDescricaoIsRequired() throws Exception {
        int databaseSizeBeforeTest = servicoRepository.findAll().size();
        // set the field null
        servico.setDescricao(null);

        // Create the Servico, which fails.

        restServicoMockMvc.perform(post("/api/servicos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(servico)))
            .andExpect(status().isBadRequest());

        List<Servico> servicoList = servicoRepository.findAll();
        assertThat(servicoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllServicos() throws Exception {
        // Initialize the database
        servicoRepository.saveAndFlush(servico);

        // Get all the servicoList
        restServicoMockMvc.perform(get("/api/servicos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(servico.getId().intValue())))
            .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO.toString())))
            .andExpect(jsonPath("$.[*].descricaoDetalhada").value(hasItem(DEFAULT_DESCRICAO_DETALHADA.toString())));
    }

    @Test
    @Transactional
    public void getServico() throws Exception {
        // Initialize the database
        servicoRepository.saveAndFlush(servico);

        // Get the servico
        restServicoMockMvc.perform(get("/api/servicos/{id}", servico.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(servico.getId().intValue()))
            .andExpect(jsonPath("$.descricao").value(DEFAULT_DESCRICAO.toString()))
            .andExpect(jsonPath("$.descricaoDetalhada").value(DEFAULT_DESCRICAO_DETALHADA.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingServico() throws Exception {
        // Get the servico
        restServicoMockMvc.perform(get("/api/servicos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateServico() throws Exception {
        // Initialize the database
        servicoRepository.saveAndFlush(servico);
        servicoSearchRepository.save(servico);
        int databaseSizeBeforeUpdate = servicoRepository.findAll().size();

        // Update the servico
        Servico updatedServico = servicoRepository.findOne(servico.getId());
        updatedServico
                .descricao(UPDATED_DESCRICAO)
                .descricaoDetalhada(UPDATED_DESCRICAO_DETALHADA);

        restServicoMockMvc.perform(put("/api/servicos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedServico)))
            .andExpect(status().isOk());

        // Validate the Servico in the database
        List<Servico> servicoList = servicoRepository.findAll();
        assertThat(servicoList).hasSize(databaseSizeBeforeUpdate);
        Servico testServico = servicoList.get(servicoList.size() - 1);
        assertThat(testServico.getDescricao()).isEqualTo(UPDATED_DESCRICAO);
        assertThat(testServico.getDescricaoDetalhada()).isEqualTo(UPDATED_DESCRICAO_DETALHADA);

        // Validate the Servico in ElasticSearch
        Servico servicoEs = servicoSearchRepository.findOne(testServico.getId());
        assertThat(servicoEs).isEqualToComparingFieldByField(testServico);
    }

    @Test
    @Transactional
    public void updateNonExistingServico() throws Exception {
        int databaseSizeBeforeUpdate = servicoRepository.findAll().size();

        // Create the Servico

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restServicoMockMvc.perform(put("/api/servicos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(servico)))
            .andExpect(status().isCreated());

        // Validate the Servico in the database
        List<Servico> servicoList = servicoRepository.findAll();
        assertThat(servicoList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteServico() throws Exception {
        // Initialize the database
        servicoRepository.saveAndFlush(servico);
        servicoSearchRepository.save(servico);
        int databaseSizeBeforeDelete = servicoRepository.findAll().size();

        // Get the servico
        restServicoMockMvc.perform(delete("/api/servicos/{id}", servico.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean servicoExistsInEs = servicoSearchRepository.exists(servico.getId());
        assertThat(servicoExistsInEs).isFalse();

        // Validate the database is empty
        List<Servico> servicoList = servicoRepository.findAll();
        assertThat(servicoList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchServico() throws Exception {
        // Initialize the database
        servicoRepository.saveAndFlush(servico);
        servicoSearchRepository.save(servico);

        // Search the servico
        restServicoMockMvc.perform(get("/api/_search/servicos?query=id:" + servico.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(servico.getId().intValue())))
            .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO.toString())))
            .andExpect(jsonPath("$.[*].descricaoDetalhada").value(hasItem(DEFAULT_DESCRICAO_DETALHADA.toString())));
    }
}
