package com.coffeshop.repository;

import com.coffeshop.entity.Loja;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LojaRepository extends JpaRepository<Loja, UUID> {
    List<Loja> findAllByOrderByNomeAsc();
}
