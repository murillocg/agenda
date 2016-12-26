package com.learning.agenda.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

import com.learning.agenda.domain.enumeration.SituacaoAgendamento;

/**
 * A Agendamento.
 */
@Entity
@Table(name = "agendamento")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "agendamento")
public class Agendamento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "data_hora_inicio", nullable = false)
    private ZonedDateTime dataHoraInicio;

    @Column(name = "data_hora_fim")
    private ZonedDateTime dataHoraFim;

    @Column(name = "observacoes")
    private String observacoes;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao")
    private SituacaoAgendamento situacao;

    @ManyToOne
    private Cliente cliente;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "agendamento_servico",
               joinColumns = @JoinColumn(name="agendamentos_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="servicos_id", referencedColumnName="ID"))
    private Set<Servico> servicos = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public Agendamento dataHoraInicio(ZonedDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
        return this;
    }

    public void setDataHoraInicio(ZonedDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    public ZonedDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public Agendamento dataHoraFim(ZonedDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
        return this;
    }

    public void setDataHoraFim(ZonedDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public Agendamento observacoes(String observacoes) {
        this.observacoes = observacoes;
        return this;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public SituacaoAgendamento getSituacao() {
        return situacao;
    }

    public Agendamento situacao(SituacaoAgendamento situacao) {
        this.situacao = situacao;
        return this;
    }

    public void setSituacao(SituacaoAgendamento situacao) {
        this.situacao = situacao;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Agendamento cliente(Cliente cliente) {
        this.cliente = cliente;
        return this;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Set<Servico> getServicos() {
        return servicos;
    }

    public Agendamento servicos(Set<Servico> servicos) {
        this.servicos = servicos;
        return this;
    }

    public Agendamento addServico(Servico servico) {
        servicos.add(servico);
        return this;
    }

    public Agendamento removeServico(Servico servico) {
        servicos.remove(servico);
        return this;
    }

    public void setServicos(Set<Servico> servicos) {
        this.servicos = servicos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Agendamento agendamento = (Agendamento) o;
        if (agendamento.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, agendamento.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Agendamento{" +
            "id=" + id +
            ", dataHoraInicio='" + dataHoraInicio + "'" +
            ", dataHoraFim='" + dataHoraFim + "'" +
            ", observacoes='" + observacoes + "'" +
            ", situacao='" + situacao + "'" +
            '}';
    }
}
