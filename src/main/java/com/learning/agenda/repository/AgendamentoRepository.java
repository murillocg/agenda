package com.learning.agenda.repository;

import com.learning.agenda.domain.Agendamento;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Agendamento entity.
 */
@SuppressWarnings("unused")
public interface AgendamentoRepository extends JpaRepository<Agendamento,Long> {

    @Query("select distinct agendamento from Agendamento agendamento left join fetch agendamento.servicos")
    List<Agendamento> findAllWithEagerRelationships();

    @Query("select agendamento from Agendamento agendamento left join fetch agendamento.servicos where agendamento.id =:id")
    Agendamento findOneWithEagerRelationships(@Param("id") Long id);

}
