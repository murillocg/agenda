package com.learning.agenda.repository;

import com.learning.agenda.domain.Servico;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Servico entity.
 */
@SuppressWarnings("unused")
public interface ServicoRepository extends JpaRepository<Servico,Long> {

}
