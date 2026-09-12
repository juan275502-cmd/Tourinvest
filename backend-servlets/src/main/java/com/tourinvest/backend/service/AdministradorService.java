package com.tourinvest.backend.service;

import com.tourinvest.backend.dto.UsuarioResumenDTO;
import com.tourinvest.backend.model.Usuario;
import com.tourinvest.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AdministradorService {

    private final UsuarioRepository usuarioRepository;

    public AdministradorService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<UsuarioResumenDTO> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::mapearADTO)
                .toList();
    }

    public UsuarioResumenDTO suspender(Usuario administrador, Integer idUsuario) {
        if (administrador.getIdUsuario().equals(idUsuario)) {
            throw new IllegalStateException("No puedes suspender tu propia cuenta de administrador");
        }
        return cambiarEstado(idUsuario, Usuario.EstadoUsuario.Inactivo);
    }

    public UsuarioResumenDTO activar(Integer idUsuario) {
        return cambiarEstado(idUsuario, Usuario.EstadoUsuario.Activo);
    }

    private UsuarioResumenDTO cambiarEstado(Integer idUsuario, Usuario.EstadoUsuario nuevoEstado) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        usuario.setEstado(nuevoEstado);
        Usuario actualizado = usuarioRepository.save(usuario);
        return mapearADTO(actualizado);
    }

    private UsuarioResumenDTO mapearADTO(Usuario usuario) {
        return new UsuarioResumenDTO(
                usuario.getIdUsuario(),
                usuario.getNombre1() + " " + usuario.getApellido1(),
                usuario.getCorreo(),
                usuario.getRol().getNombre().name(),
                usuario.getEstado().name(),
                usuario.getFechaRegistro());
    }
}