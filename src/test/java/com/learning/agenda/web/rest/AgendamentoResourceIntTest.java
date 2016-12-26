package com.learning.agenda.web.rest;

import com.learning.agenda.AgendaApp;

import com.learning.agenda.domain.Agendamento;
import com.learning.agenda.repository.AgendamentoRepository;
import com.learning.agenda.repository.search.AgendamentoSearchRepository;

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
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;

import static com.learning.agenda.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.learning.agenda.domain.enumeration.SituacaoAgendamento;
/**
 * Test class for the AgendamentoResource REST controller.
 *
 * @see AgendamentoResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AgendaApp.class)
public class AgendamentoResourceIntTest {

    private static final ZonedDateTime DEFAULT_DATA_HORA_INICIO = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATA_HORA_INICIO = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_DATA_HORA_FIM = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATA_HORA_FIM = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_OBSERVACOES = "AAAAAAAAAA";
    private static final String UPDATED_OBSERVACOES = "BBBBBBBBBB";

    private static final SituacaoAgendamento DEFAULT_SITUACAO = SituacaoAgendamento.AGENDADO;
    private static final SituacaoAgendamento UPDATED_SITUACAO = SituacaoAgendamento.REALIZADO;

    @Inject
    private AgendamentoRepository agendamentoRepository;

    @Inject
    private AgendamentoSearchRepository agendamentoSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restAgendamentoMockMvc;

    private Agendamento agendamento;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AgendamentoResource agendamentoResource = new AgendamentoResource();
        ReflectionTestUtils.setField(agendamentoResource, "agendamentoSearchRepository", agendamentoSearchRepository);
        ReflectionTestUtils.setField(agendamentoResource, "agendamentoRepository", agendamentoRepository);
        this.restAgendamentoMockMvc = MockMvcBuilders.standaloneSetup(agendamentoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Agendamento createEntity(EntityManager em) {
        Agendamento agendamento = new Agendamento()
                .dataHoraInicio(DEFAULT_DATA_HORA_INICIO)
                .dataHoraFim(DEFAULT_DATA_HORA_FIM)
                .observacoes(DEFAULT_OBSERVACOES)
                .situacao(DEFAULT_SITUACAO);
        return agendamento;
    }

    @Before
    public void initTest() {
        agendamentoSearchRepository.deleteAll();
        agendamento = createEntity(em);
    }

    @Test
    @Transactional
    public void createAgendamento() throws Exception {
        int databaseSizeBeforeCreate = agendamentoRepository.findAll().size();

        // Create the Agendamento

        restAgendamentoMockMvc.perform(post("/api/agendamentos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(agendamento)))
            .andExpect(status().isCreated());

        // Validate the Agendamento in the database
        List<Agendamento> agendamentoList = agendamentoRepository.findAll();
        assertThat(agendamentoList).hasSize(databaseSizeBeforeCreate + 1);
        Agendamento testAgendamento = agendamentoList.get(agendamentoList.size() - 1);
        assertThat(testAgendamento.getDataHoraInicio()).isEqualTo(DEFAULT_DATA_HORA_INICIO);
        assertThat(testAgendamento.getDataHoraFim()).isEqualTo(DEFAULT_DATA_HORA_FIM);
        assertThat(testAgendamento.getObservacoes()).isEqualTo(DEFAULT_OBSERVACOES);
        assertThat(testAgendamento.getSituacao()).isEqualTo(DEFAULT_SITUACAO);

        // Validate the Agendamento in ElasticSearch
        Agendamento agendamentoEs = agendamentoSearchRepository.findOne(testAgendamento.getId());
        assertThat(agendamentoEs).isEqualToComparingFieldByField(testAgendamento);
    }

    @Test
    @Transactional
    public void createAgendamentoWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = agendamentoRepository.findAll().size();

        // Create the Agendamento with an existing ID
        Agendamento existingAgendamento = new Agendamento();
        existingAgendamento.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAgendamentoMockMvc.perform(post("/api/agendamentos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingAgendamento)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Agendamento> agendamentoList = agendamentoRepository.findAll();
        assertThat(agendamentoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkDataHoraInicioIsRequired() throws Exception {
        int databaseSizeBeforeTest = agendamentoRepository.findAll().size();
        // set the field null
        agendamento.setDataHoraInicio(null);

        // Create the Agendamento, which fails.

        restAgendamentoMockMvc.perform(post("/api/agendamentos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(agendamento)))
            .andExpect(status().isBadRequest());

        List<Agendamento> agendamentoList = agendamentoRepository.findAll();
        assertThat(agendamentoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAgendamentos() throws Exception {
        // Initialize the database
        agendamentoRepository.saveAndFlush(agendamento);

        // Get all the agendamentoList
        restAgendamentoMockMvc.perform(get("/api/agendamentos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(agendamento.getId().intValue())))
            .andExpect(jsonPath("$.[*].dataHoraInicio").value(hasItem(sameInstant(DEFAULT_DATA_HORA_INICIO))))
            .andExpect(jsonPath("$.[*].dataHoraFim").value(hasItem(sameInstant(DEFAULT_DATA_HORA_FIM))))
            .andExpect(jsonPath("$.[*].observacoes").value(hasItem(DEFAULT_OBSERVACOES.toString())))
            .andExpect(jsonPath("$.[*].situacao").value(hasItem(DEFAULT_SITUACAO.toString())));
    }

    @Test
    @Transactional
    public void getAgendamento() throws Exception {
        // Initialize the database
        agendamentoRepository.saveAndFlush(agendamento);

        // Get the agendamento
        restAgendamentoMockMvc.perform(get("/api/agendamentos/{id}", agendamento.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(agendamento.getId().intValue()))
            .andExpect(jsonPath("$.dataHoraInicio").value(sameInstant(DEFAULT_DATA_HORA_INICIO)))
            .andExpect(jsonPath("$.dataHoraFim").value(sameInstant(DEFAULT_DATA_HORA_FIM)))
            .andExpect(jsonPath("$.observacoes").value(DEFAULT_OBSERVACOES.toString()))
            .andExpect(jsonPath("$.situacao").value(DEFAULT_SITUACAO.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAgendamento() throws Exception {
        // Get the agendamento
        restAgendamentoMockMvc.perform(get("/api/agendamentos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAgendamento() throws Exception {
        // Initialize the database
        agendamentoRepository.saveAndFlush(agendamento);
        agendamentoSearchRepository.save(agendamento);
        int databaseSizeBeforeUpdate = agendamentoRepository.findAll().size();

        // Update the agendamento
        Agendamento updatedAgendamento = agendamentoRepository.findOne(agendamento.getId());
        updatedAgendamento
                .dataHoraInicio(UPDATED_DATA_HORA_INICIO)
                .dataHoraFim(UPDATED_DATA_HORA_FIM)
                .observacoes(UPDATED_OBSERVACOES)
                .situacao(UPDATED_SITUACAO);

        restAgendamentoMockMvc.perform(put("/api/agendamentos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedAgendamento)))
            .andExpect(status().isOk());

        // Validate the Agendamento in the database
        List<Agendamento> agendamentoList = agendamentoRepository.findAll();
        assertThat(agendamentoList).hasSize(databaseSizeBeforeUpdate);
        Agendamento testAgendamento = agendamentoList.get(agendamentoList.size() - 1);
        assertThat(testAgendamento.getDataHoraInicio()).isEqualTo(UPDATED_DATA_HORA_INICIO);
        assertThat(testAgendamento.getDataHoraFim()).isEqualTo(UPDATED_DATA_HORA_FIM);
        assertThat(testAgendamento.getObservacoes()).isEqualTo(UPDATED_OBSERVACOES);
        assertThat(testAgendamento.getSituacao()).isEqualTo(UPDATED_SITUACAO);

        // Validate the Agendamento in ElasticSearch
        Agendamento agendamentoEs = agendamentoSearchRepository.findOne(testAgendamento.getId());
        assertThat(agendamentoEs).isEqualToComparingFieldByField(testAgendamento);
    }

    @Test
    @Transactional
    public void updateNonExistingAgendamento() throws Exception {
        int databaseSizeBeforeUpdate = agendamentoRepository.findAll().size();

        // Create the Agendamento

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restAgendamentoMockMvc.perform(put("/api/agendamentos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(agendamento)))
            .andExpect(status().isCreated());

        // Validate the Agendamento in the database
        List<Agendamento> agendamentoList = agendamentoRepository.findAll();
        assertThat(agendamentoList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteAgendamento() throws Exception {
        // Initialize the database
        agendamentoRepository.saveAndFlush(agendamento);
        agendamentoSearchRepository.save(agendamento);
        int databaseSizeBeforeDelete = agendamentoRepository.findAll().size();

        // Get the agendamento
        restAgendamentoMockMvc.perform(delete("/api/agendamentos/{id}", agendamento.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean agendamentoExistsInEs = agendamentoSearchRepository.exists(agendamento.getId());
        assertThat(agendamentoExistsInEs).isFalse();

        // Validate the database is empty
        List<Agendamento> agendamentoList = agendamentoRepository.findAll();
        assertThat(agendamentoList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchAgendamento() throws Exception {
        // Initialize the database
        agendamentoRepository.saveAndFlush(agendamento);
        agendamentoSearchRepository.save(agendamento);

        // Search the agendamento
        restAgendamentoMockMvc.perform(get("/api/_search/agendamentos?query=id:" + agendamento.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(agendamento.getId().intValue())))
            .andExpect(jsonPath("$.[*].dataHoraInicio").value(hasItem(sameInstant(DEFAULT_DATA_HORA_INICIO))))
            .andExpect(jsonPath("$.[*].dataHoraFim").value(hasItem(sameInstant(DEFAULT_DATA_HORA_FIM))))
            .andExpect(jsonPath("$.[*].observacoes").value(hasItem(DEFAULT_OBSERVACOES.toString())))
            .andExpect(jsonPath("$.[*].situacao").value(hasItem(DEFAULT_SITUACAO.toString())));
    }
}
