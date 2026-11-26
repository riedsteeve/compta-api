package org.sebsy.compta.repo;

import org.sebsy.compta.domain.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FournisseurRepository extends JpaRepository <Fournisseur,Long>{
    List<Fournisseur> findAllById(Long id);
}
