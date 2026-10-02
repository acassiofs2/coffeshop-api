package com.coffeshop.repository;

import com.coffeshop.entity.Loja;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LojaRepository extends JpaRepository<Loja, UUID> {
    Page<Loja> findAllByOrderByNomeAsc(Pageable pageable);
}
