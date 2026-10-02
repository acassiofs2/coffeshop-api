package com.coffeshop.service;

import com.coffeshop.dto.LojaPatchDTO;
import com.coffeshop.dto.LojaRequestDTO;
import com.coffeshop.dto.LojaResponseDTO;
import com.coffeshop.entity.Loja;
import com.coffeshop.entity.Usuario;
import com.coffeshop.repository.LojaRepository;
import com.coffeshop.repository.UsuarioRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class LojaService {

    private final LojaRepository lojaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public LojaResponseDTO criar(LojaRequestDTO request) {
        Loja loja = new Loja();
        loja.setNome(request.getNome());
        loja.setEndereco(request.getEndereco());
        loja.setTelefone(request.getTelefone());
        loja.setUsuario(buscarUsuarioPorId(request.getUsuarioId()));

        Loja salva = lojaRepository.save(loja);
        return toResponseDTO(salva);
    }

    @Transactional(readOnly = true)
    public List<LojaResponseDTO> listar() {
        return lojaRepository.findAllByOrderByNomeAsc().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public LojaResponseDTO buscarPorId(UUID id) {
        Loja loja = buscarEntidadePorId(id);
        return toResponseDTO(loja);
    }

    @Transactional
    public LojaResponseDTO atualizar(UUID id, LojaRequestDTO request) {
        Loja loja = buscarEntidadePorId(id);
        loja.setNome(request.getNome());
        loja.setEndereco(request.getEndereco());
        loja.setTelefone(request.getTelefone());
        loja.setUsuario(buscarUsuarioPorId(request.getUsuarioId()));

        Loja atualizada = lojaRepository.save(loja);
        return toResponseDTO(atualizada);
    }

    @Transactional
    public LojaResponseDTO atualizarParcial(UUID id, LojaPatchDTO request) {
        Loja loja = buscarEntidadePorId(id);

        if (request.getNome() != null) {
            loja.setNome(request.getNome());
        }
        if (request.getEndereco() != null) {
            loja.setEndereco(request.getEndereco());
        }
        if (request.getTelefone() != null) {
            loja.setTelefone(request.getTelefone());
        }
        if (request.getUsuarioId() != null) {
            loja.setUsuario(buscarUsuarioPorId(request.getUsuarioId()));
        }

        Loja atualizada = lojaRepository.save(loja);
        return toResponseDTO(atualizada);
    }

    @Transactional
    public void deletar(UUID id) {
        if (!lojaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Loja não encontrada com id: " + id);
        }
        lojaRepository.deleteById(id);
    }

    private Loja buscarEntidadePorId(UUID id) {
        return lojaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Loja não encontrada com id: " + id));
    }

    private Usuario buscarUsuarioPorId(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuário não encontrado com id: " + id));
    }

    private LojaResponseDTO toResponseDTO(Loja loja) {
        return new LojaResponseDTO(
                loja.getId(),
                loja.getNome(),
                loja.getEndereco(),
                loja.getTelefone(),
                loja.getUsuario().getId(),
                loja.getDataCriacao()
        );
    }
}
