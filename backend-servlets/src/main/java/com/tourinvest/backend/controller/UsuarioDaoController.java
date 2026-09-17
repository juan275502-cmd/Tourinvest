package com.tourinvest.backend.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tourinvest.backend.dao.Usuario;
import com.tourinvest.backend.dao.UsuarioDAO;

/**
 * Paso 6 de la guía — expone el CRUD del UsuarioDAO (JDBC puro +
 * PreparedStatement) vía REST para que el HTML lo consuma.
 *
 * Ruta separada de /admin/usuarios (que usa JPA) para no interferir con la
 * API existente. Protegida por SecurityConfig: /dao/** exige rol
 * ADMINISTRADOR (Authorization: Bearer <token>).
 */
@RestController
@RequestMapping("/dao/usuarios")
public class UsuarioDaoController {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // ---------- READ ----------

    @GetMapping
    public List<Map<String, Object>> listar() {
        return usuarioDAO.listarUsuarios().stream()
                .map(UsuarioDaoController::aJson)
                .toList();
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<?> buscar(@PathVariable int idUsuario) {
        Usuario usuario = usuarioDAO.buscarPorId(idUsuario);
        if (usuario == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("mensaje", "Usuario no encontrado (id " + idUsuario + ")."));
        }
        return ResponseEntity.ok(aJson(usuario));
    }

    // ---------- CREATE ----------

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> cuerpo) {
        Map<String, String> errores = validar(cuerpo, true);
        if (!errores.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "Revisa los campos marcados.", "errores", errores));
        }

        if (usuarioDAO.existeCorreo(texto(cuerpo.get("correo")))) {
            return ResponseEntity.status(409)
                    .body(Map.of("mensaje", "Ya existe un usuario registrado con ese correo."));
        }

        if (!usuarioDAO.crearUsuario(desdeCuerpo(cuerpo, null))) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "mensaje", "No fue posible crear el usuario (¿cédula o correo duplicados?)."));
        }

        return ResponseEntity.status(201)
                .body(Map.of("mensaje", "Usuario creado correctamente."));
    }

    // ---------- UPDATE ----------

    @PutMapping("/{idUsuario}")
    public ResponseEntity<?> actualizar(@PathVariable int idUsuario,
                                        @RequestBody Map<String, Object> cuerpo) {
        Usuario existente = usuarioDAO.buscarPorId(idUsuario);
        if (existente == null) {
            return ResponseEntity.status(404).body(Map.of("mensaje", "Usuario no encontrado."));
        }

        Map<String, String> errores = validar(cuerpo, false);
        if (!errores.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "Revisa los campos marcados.", "errores", errores));
        }

        Usuario usuario = desdeCuerpo(cuerpo, existente);
        usuario.setIdUsuario(idUsuario);

        if (!usuarioDAO.actualizarUsuario(usuario)) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("mensaje", "No fue posible actualizar el usuario."));
        }

        return ResponseEntity.ok(Map.of("mensaje", "Usuario actualizado correctamente."));
    }

    // ---------- DELETE ----------

    @DeleteMapping("/{idUsuario}")
    public ResponseEntity<?> eliminar(@PathVariable int idUsuario) {
        if (!usuarioDAO.eliminarUsuario(idUsuario)) {
            return ResponseEntity.status(404).body(Map.of(
                    "mensaje", "No fue posible eliminar el usuario (¿existe ese id?)."));
        }
        return ResponseEntity.ok(Map.of("mensaje", "Usuario eliminado correctamente."));
    }

    // ---------- PATCH estado (suspender / activar) ----------

    @PatchMapping("/{idUsuario}/suspender")
    public ResponseEntity<?> suspender(@PathVariable int idUsuario) {
        return cambiarEstado(idUsuario, false);
    }

    @PatchMapping("/{idUsuario}/activar")
    public ResponseEntity<?> activar(@PathVariable int idUsuario) {
        return cambiarEstado(idUsuario, true);
    }

    private ResponseEntity<?> cambiarEstado(int idUsuario, boolean activo) {
        if (!usuarioDAO.cambiarEstado(idUsuario, activo)) {
            return ResponseEntity.status(404).body(Map.of(
                    "mensaje", "No fue posible cambiar el estado (¿existe ese id?)."));
        }
        return ResponseEntity.ok(Map.of("mensaje", activo ? "Usuario activado." : "Usuario suspendido."));
    }

    // ---------- Helpers ----------

    /** Convierte un Usuario a JSON sin exponer el hash de la contraseña. */
    private static Map<String, Object> aJson(Usuario usuario) {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("idUsuario", usuario.getIdUsuario());
        json.put("nombre", usuario.getNombre());
        json.put("apellido", usuario.getApellido());
        json.put("nombreCompleto", usuario.getNombreCompleto());
        json.put("cedula", usuario.getCedula());
        json.put("fechaNacimiento", usuario.getFechaNacimiento());
        json.put("correo", usuario.getCorreo());
        json.put("estado", usuario.isEstado() ? "Activo" : "Inactivo");
        json.put("idRol", usuario.getIdRol());
        json.put("rolNombre", usuario.getRolNombre());
        return json;
    }

    private static Usuario desdeCuerpo(Map<String, Object> cuerpo, Usuario base) {
        Usuario usuario = (base != null) ? base : new Usuario();
        usuario.setNombre(texto(cuerpo.get("nombre")));
        usuario.setApellido(texto(cuerpo.get("apellido")));
        usuario.setCedula(texto(cuerpo.get("cedula")));
        usuario.setFechaNacimiento(texto(cuerpo.get("fechaNacimiento")));
        usuario.setCorreo(texto(cuerpo.get("correo")));
        // Vacío en edición = conservar la contraseña actual (lo maneja el DAO).
        usuario.setContrasena(texto(cuerpo.get("contrasena")));
        usuario.setIdRol((int) numero(cuerpo.get("idRol"), 3));
        if (base == null) {
            usuario.setEstado(true); // los usuarios nuevos entran Activos
        }
        return usuario;
    }

    /** Validación mínima requerida. En edición la contraseña es opcional. */
    private static Map<String, String> validar(Map<String, Object> cuerpo, boolean esCreacion) {
        Map<String, String> errores = new LinkedHashMap<>();

        String[] obligatorios = { "nombre", "apellido", "cedula", "fechaNacimiento", "correo" };
        for (String campo : obligatorios) {
            if (texto(cuerpo.get(campo)).isBlank()) {
                errores.put(campo, "Este campo es obligatorio.");
            }
        }

        String contrasena = texto(cuerpo.get("contrasena"));
        if (esCreacion && contrasena.length() < 6) {
            errores.put("contrasena", "La contraseña debe tener al menos 6 caracteres.");
        }

        int idRol = (int) numero(cuerpo.get("idRol"), 0);
        if (idRol < 1 || idRol > 3) {
            errores.put("idRol", "El rol debe ser 1 (Administrador), 2 (Analista) o 3 (Inversionista).");
        }

        return errores;
    }

    private static String texto(Object valor) {
        return valor == null ? "" : String.valueOf(valor).trim();
    }

    private static long numero(Object valor, long porDefecto) {
        if (valor instanceof Number n) {
            return n.longValue();
        }
        try {
            return Long.parseLong(texto(valor));
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }
}
