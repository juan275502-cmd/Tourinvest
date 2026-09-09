package com.tourinvest.backend.service;

import com.tourinvest.backend.dto.LoginRequest;
import com.tourinvest.backend.dto.LoginResponse;
import com.tourinvest.backend.model.Rol;
import com.tourinvest.backend.model.Usuario;
import com.tourinvest.backend.repository.RolRepository;
import com.tourinvest.backend.repository.UsuarioRepository;
import com.tourinvest.backend.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UsuarioRepository usuarioRepository,
                        RolRepository rolRepository,
                        PasswordEncoder passwordEncoder,
                        AuthenticationManager authenticationManager,
                        JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getCorreo(), request.getContrasena()));
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Correo o contraseña incorrectos");
        }

        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new BadCredentialsException("Correo o contraseña incorrectos"));

        String token = jwtUtil.generarToken(usuario.getCorreo(), usuario.getRol().getNombre().name());

        return new LoginResponse(token, usuario.getNombre1(), usuario.getCorreo(), usuario.getRol().getNombre().name());
    }

    public Usuario registrar(Usuario nuevoUsuario, Rol.NombreRol nombreRol) {
        if (usuarioRepository.existsByCorreo(nuevoUsuario.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con ese correo");
        }
        if (usuarioRepository.existsByCedula(nuevoUsuario.getCedula())) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con esa cédula");
        }

        Rol rol = rolRepository.findByNombre(nombreRol)
                .orElseThrow(() -> new IllegalStateException("El rol solicitado no existe en la base de datos"));

        nuevoUsuario.setRol(rol);
        nuevoUsuario.setContrasena(passwordEncoder.encode(nuevoUsuario.getPassword()));

        return usuarioRepository.save(nuevoUsuario);
    }

    /**
     * Recuperar contraseña. Por seguridad, la respuesta nunca revela si el correo
     * está registrado o no — solo se distingue internamente para fines de log/auditoría futura.
     * El envío real de correo queda fuera de alcance (requeriría configurar un servidor SMTP).
     */
    public void solicitarRecuperacion(String correo) {
        usuarioRepository.findByCorreo(correo); // hook para una futura integración de envío de correo
    }
} 