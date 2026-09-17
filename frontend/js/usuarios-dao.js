// ============================================================
// TourInvest · Driver de la pantalla "Usuarios · DAO (JDBC)"
// Consume el CRUD REST del UsuarioDaoController (/dao/usuarios),
// respaldado por UsuarioDAO.java (JDBC puro + PreparedStatement).
// ============================================================

const API_BASE_URL = (window.TourInvestConfig || {}).apiUrl || "http://localhost:8080";
const ETIQUETA_ROL = { 1: "Administrador", 2: "Analista", 3: "Inversionista" };

let usuariosDaoCache = [];
let usuarioDaoEditandoId = null;

// ---------- Sesión y llamadas al backend ----------

function obtenerTokenDao() {
  const token = sessionStorage.getItem("tourinvest_token");
  if (!token) {
    window.location.href = "login.html";
    return null;
  }
  return token;
}

async function llamarApiDao(ruta, opciones = {}) {
  const token = obtenerTokenDao();
  if (!token) return null;

  const respuesta = await fetch(`${API_BASE_URL}${ruta}`, {
    ...opciones,
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
      ...(opciones.headers || {}),
    },
  });

  if (respuesta.status === 401) {
    sessionStorage.clear();
    window.location.href = "login.html";
    return null;
  }

  return respuesta;
}

// ---------- READ ----------

async function cargarUsuariosDao() {
  const cuerpoTabla = document.getElementById("tabla-usuarios-dao-cuerpo");
  const respuesta = await llamarApiDao("/dao/usuarios");

  if (!respuesta) return;

  if (!respuesta.ok) {
    cuerpoTabla.innerHTML =
      `<tr><td colspan="6" class="estado-vacio">No fue posible cargar los usuarios. ¿Está corriendo el backend en :8080?</td></tr>`;
    return;
  }

  usuariosDaoCache = await respuesta.json();
  renderUsuariosDao();
}

function renderUsuariosDao() {
  const cuerpoTabla = document.getElementById("tabla-usuarios-dao-cuerpo");
  const entrada = document.getElementById("buscar-usuarios-dao");
  const filtro = (entrada && entrada.value ? entrada.value : "").trim().toLowerCase();

  if (usuariosDaoCache.length === 0) {
    cuerpoTabla.innerHTML = `<tr><td colspan="6" class="estado-vacio">No hay usuarios registrados. Crea el primero con «+ Crear usuario».</td></tr>`;
    return;
  }

  const visibles = filtro
    ? usuariosDaoCache.filter(
        (u) =>
          (u.nombreCompleto || "").toLowerCase().includes(filtro) ||
          (u.correo || "").toLowerCase().includes(filtro)
      )
    : usuariosDaoCache;

  if (visibles.length === 0) {
    cuerpoTabla.innerHTML = `<tr><td colspan="6" class="estado-vacio">Sin resultados para tu búsqueda.</td></tr>`;
    return;
  }

  cuerpoTabla.innerHTML = visibles
    .map((usuario) => {
      const esActivo = usuario.estado === "Activo";
      return `
        <tr>
          <td>${usuario.nombreCompleto}</td>
          <td>${usuario.correo}</td>
          <td>${usuario.rolNombre || ETIQUETA_ROL[usuario.idRol] || "—"}</td>
          <td>${usuario.cedula}</td>
          <td><span class="estado-pill estado-pill--${esActivo ? "activa" : "cancelada"}">${usuario.estado}</span></td>
          <td style="white-space: nowrap;">
            <button class="boton-fila" onclick="abrirModalEditarUsuarioDao(${usuario.idUsuario})">Editar</button>
            ${esActivo
              ? `<button class="boton-fila boton-fila--cancelar" onclick="suspenderUsuarioDao(${usuario.idUsuario})">Suspender</button>`
              : `<button class="boton-fila" onclick="activarUsuarioDao(${usuario.idUsuario})">Activar</button>`}
            <button class="boton-fila boton-fila--cancelar" onclick="eliminarUsuarioDao(${usuario.idUsuario}, '${(usuario.nombreCompleto || "").replace(/'/g, "\\'")}')">Eliminar</button>
          </td>
        </tr>`;
    })
    .join("");
}

// ---------- CREATE / UPDATE ----------

function abrirModalCrear() {
  usuarioDaoEditandoId = null;
  const formulario = document.getElementById("form-usuario-dao");
  formulario.reset();
  formulario.idRol.value = "3";
  document.getElementById("modal-usuario-dao-titulo").textContent = "Crear usuario";
  ocultarMensajeModal();
  abrirModalUsuarioDao();
}

async function abrirModalEditarUsuarioDao(idUsuario) {
  // GET /dao/usuarios/{id} — evidencia del READ por id del DAO (buscarPorId)
  const respuesta = await llamarApiDao(`/dao/usuarios/${idUsuario}`);
  if (!respuesta || !respuesta.ok) return;

  const usuario = await respuesta.json();
  usuarioDaoEditandoId = idUsuario;

  const formulario = document.getElementById("form-usuario-dao");
  formulario.nombre.value = usuario.nombre;
  formulario.apellido.value = usuario.apellido;
  formulario.cedula.value = usuario.cedula;
  formulario.fechaNacimiento.value = usuario.fechaNacimiento;
  formulario.correo.value = usuario.correo;
  formulario.contrasena.value = ""; // vacío = conservar la actual
  formulario.idRol.value = String(usuario.idRol);

  document.getElementById("modal-usuario-dao-titulo").textContent =
    `Editar usuario: ${usuario.nombreCompleto}`;
  ocultarMensajeModal();
  abrirModalUsuarioDao();
}

function abrirModalUsuarioDao() {
  const modal = document.getElementById("modal-usuario-dao");
  modal.classList.add("modal--open");
  modal.setAttribute("aria-hidden", "false");
}

function cerrarModalUsuarioDao() {
  const modal = document.getElementById("modal-usuario-dao");
  modal.classList.remove("modal--open");
  modal.setAttribute("aria-hidden", "true");
}

async function guardarUsuarioDao(evento) {
  evento.preventDefault();
  const formulario = evento.target;
  ocultarMensajeModal();

  const cuerpo = {
    nombre: formulario.nombre.value.trim(),
    apellido: formulario.apellido.value.trim(),
    cedula: formulario.cedula.value.trim(),
    fechaNacimiento: formulario.fechaNacimiento.value,
    correo: formulario.correo.value.trim(),
    contrasena: formulario.contrasena.value,
    idRol: Number(formulario.idRol.value),
  };

  const esEdicion = usuarioDaoEditandoId != null;
  const respuesta = await llamarApiDao(
    esEdicion ? `/dao/usuarios/${usuarioDaoEditandoId}` : "/dao/usuarios",
    { method: esEdicion ? "PUT" : "POST", body: JSON.stringify(cuerpo) }
  );

  if (!respuesta) return false;

  const datos = await respuesta.json().catch(() => ({}));

  if (!respuesta.ok) {
    const detalle = datos.errores ? Object.values(datos.errores).join(" ") : "";
    mostrarMensajeModal((datos.mensaje || "No fue posible guardar el usuario.") + (detalle ? ` ${detalle}` : ""), "error");
    return false;
  }

  cerrarModalUsuarioDao();
  window.TourInvestUI.toast(datos.mensaje || "Usuario guardado.", "success");
  cargarUsuariosDao();
  return false;
}

// ---------- DELETE + PATCH estado ----------

async function eliminarUsuarioDao(idUsuario, nombre) {
  if (!confirm(`¿Eliminar el usuario "${nombre}"? Esta acción no se puede deshacer.`)) return;

  const respuesta = await llamarApiDao(`/dao/usuarios/${idUsuario}`, { method: "DELETE" });
  if (!respuesta) return;

  const datos = await respuesta.json().catch(() => ({}));
  if (!respuesta.ok) {
    window.TourInvestUI.toast(datos.mensaje || "No fue posible eliminar el usuario.", "error");
    return;
  }

  window.TourInvestUI.toast(datos.mensaje || "Usuario eliminado.", "success");
  cargarUsuariosDao();
}

async function suspenderUsuarioDao(idUsuario) {
  const respuesta = await llamarApiDao(`/dao/usuarios/${idUsuario}/suspender`, { method: "PATCH" });
  if (respuesta && respuesta.ok) cargarUsuariosDao();
}

async function activarUsuarioDao(idUsuario) {
  const respuesta = await llamarApiDao(`/dao/usuarios/${idUsuario}/activar`, { method: "PATCH" });
  if (respuesta && respuesta.ok) cargarUsuariosDao();
}

// ---------- Mensajes y arranque ----------

function mostrarMensajeModal(texto, tipo) {
  const elemento = document.getElementById("mensaje-modal-usuario");
  elemento.textContent = texto;
  elemento.className = `mensaje-global mensaje-global--visible mensaje-global--${tipo}`;
}

function ocultarMensajeModal() {
  const elemento = document.getElementById("mensaje-modal-usuario");
  elemento.className = "mensaje-global";
}

function cerrarSesion() {
  sessionStorage.clear();
  window.location.href = "login.html";
}

document.addEventListener("DOMContentLoaded", () => {
  if (!obtenerTokenDao()) return;

  document.getElementById("nombre-usuario-topbar").textContent =
    sessionStorage.getItem("tourinvest_nombre") || "";
  document.getElementById("rol-usuario-topbar").textContent =
    sessionStorage.getItem("tourinvest_rol") || "";

  cargarUsuariosDao();

  document.getElementById("buscar-usuarios-dao").addEventListener("input", renderUsuariosDao);

  if (window.TourInvestUI && window.TourInvestUI.initModals) {
    window.TourInvestUI.initModals();
  }
});
