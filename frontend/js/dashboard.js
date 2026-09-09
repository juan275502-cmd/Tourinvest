const API_BASE_URL = "http://localhost:8080";

let empresasCache = [];

function obtenerToken() {
  const token = sessionStorage.getItem("tourinvest_token");
  if (!token) {
    window.location.href = "login.html";
    return null;
  }
  return token;
}

async function llamarApi(ruta, opciones = {}) {
  const token = obtenerToken();
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

function formatearMoneda(valor) {
  return new Intl.NumberFormat("es-CO", { style: "currency", currency: "USD", minimumFractionDigits: 2 }).format(valor);
}

function formatearPorcentaje(valor) {
  const signo = valor >= 0 ? "+" : "";
  return `${signo}${Number(valor).toFixed(2)}%`;
}

// ---------- Navegación entre vistas ----------

function inicializarNavegacion() {
  const enlaces = document.querySelectorAll(".sidebar__link[data-vista]");
  const vistas = document.querySelectorAll(".vista[data-vista-panel]");

  enlaces.forEach((enlace) => {
    enlace.addEventListener("click", () => {
      const destino = enlace.dataset.vista;

      enlaces.forEach((e) => e.classList.toggle("sidebar__link--activo", e === enlace));
      vistas.forEach((v) => v.classList.toggle("vista--activa", v.dataset.vistaPanel === destino));

      if (destino === "empresas" && empresasCache.length === 0) cargarEmpresas();
      if (destino === "alertas") cargarAlertas();
    });
  });
}

// ---------- Vista: Portafolio ----------

async function cargarResumenPortafolio() {
  const respuesta = await llamarApi("/inversionista/resumen");
  if (!respuesta) return;

  if (!respuesta.ok) {
    document.getElementById("tabla-portafolio-cuerpo").innerHTML =
      `<tr><td colspan="5" class="estado-vacio">No fue posible cargar tu portafolio. Verifica que el backend esté corriendo.</td></tr>`;
    return;
  }

  const datos = await respuesta.json();

  document.getElementById("nombre-usuario").textContent = datos.nombreUsuario;
  document.getElementById("valor-portafolio").textContent = formatearMoneda(datos.valorTotal);
  document.getElementById("total-invertido").textContent = formatearMoneda(datos.totalInvertido);

  const rendimientoEl = document.getElementById("rendimiento-portafolio");
  rendimientoEl.textContent = formatearPorcentaje(datos.rendimientoTotalPorcentual);
  rendimientoEl.className = `delta ${datos.rendimientoTotalPorcentual >= 0 ? "positive" : "negative"}`;

  document.getElementById("total-alertas-activas").textContent = datos.alertasActivas.length;

  const cuerpoTabla = document.getElementById("tabla-portafolio-cuerpo");
  if (datos.posiciones.length === 0) {
    cuerpoTabla.innerHTML = `<tr><td colspan="5" class="estado-vacio">Aún no tienes acciones en tu portafolio. Ve a la pestaña Empresas para agregar tu primera posición.</td></tr>`;
    return;
  }

  cuerpoTabla.innerHTML = datos.posiciones
    .map((posicion) => {
      const claseRendimiento = posicion.rendimientoPorcentual >= 0 ? "positiva" : "negativa";
      return `
        <tr>
          <td><span class="celda-simbolo">${posicion.simbolo}</span> · ${posicion.nombreEmpresa}</td>
          <td class="celda-numero">${posicion.cantidad}</td>
          <td class="celda-numero">${formatearMoneda(posicion.precioCompra)}</td>
          <td class="celda-numero">${formatearMoneda(posicion.precioActual)}</td>
          <td class="celda-numero etiqueta-variacion etiqueta-variacion--${claseRendimiento}">${formatearPorcentaje(posicion.rendimientoPorcentual)}</td>
        </tr>`;
    })
    .join("");
}

// ---------- Vista: Empresas ----------

async function cargarEmpresas() {
  const cuerpoTabla = document.getElementById("tabla-empresas-cuerpo");
  const respuesta = await llamarApi("/empresas");
  if (!respuesta) return;

  if (!respuesta.ok) {
    cuerpoTabla.innerHTML = `<tr><td colspan="5" class="estado-vacio">No fue posible cargar las empresas.</td></tr>`;
    return;
  }

  empresasCache = await respuesta.json();

  if (empresasCache.length === 0) {
    cuerpoTabla.innerHTML = `<tr><td colspan="5" class="estado-vacio">No hay empresas registradas todavía.</td></tr>`;
    return;
  }

  cuerpoTabla.innerHTML = empresasCache
    .map((empresa) => {
      const variacion = empresa.variacion ?? 0;
      const claseVariacion = variacion >= 0 ? "positiva" : "negativa";
      return `
        <tr>
          <td><span class="celda-simbolo">${empresa.simbolo}</span> · ${empresa.nombre}</td>
          <td>${empresa.sector}</td>
          <td class="celda-numero">${empresa.precioActual != null ? formatearMoneda(empresa.precioActual) : "—"}</td>
          <td class="celda-numero etiqueta-variacion etiqueta-variacion--${claseVariacion}">${formatearPorcentaje(variacion)}</td>
          <td><button class="boton-fila" onclick="abrirDetalleEmpresa(${empresa.idEmpresa})">Ver detalle</button></td>
        </tr>`;
    })
    .join("");
}

async function abrirDetalleEmpresa(idEmpresa) {
  const respuesta = await llamarApi(`/empresas/${idEmpresa}`);
  if (!respuesta || !respuesta.ok) return;

  const detalle = await respuesta.json();

  document.getElementById("modal-empresa-nombre").textContent = `${detalle.nombre} (${detalle.simbolo})`;
  document.getElementById("modal-empresa-meta").textContent = `${detalle.sector} · ${detalle.pais}`;
  document.getElementById("modal-empresa-precio").textContent = formatearMoneda(detalle.precio);

  const variacionEl = document.getElementById("modal-empresa-variacion");
  variacionEl.textContent = formatearPorcentaje(detalle.variacion ?? 0);
  variacionEl.className = `etiqueta-variacion etiqueta-variacion--${(detalle.variacion ?? 0) >= 0 ? "positiva" : "negativa"}`;

  document.getElementById("form-agregar-portafolio").dataset.idAccion = detalle.idAccion;
  document.getElementById("form-crear-alerta").dataset.idAccion = detalle.idAccion;

  ocultarSubformularios();
  const modal = document.getElementById("modal-empresa");
  modal.classList.add("modal--open");
  modal.setAttribute("aria-hidden", "false");
}

function cerrarModalEmpresa() {
  const modal = document.getElementById("modal-empresa");
  modal.classList.remove("modal--open");
  modal.setAttribute("aria-hidden", "true");
}

function ocultarSubformularios() {
  document.querySelectorAll(".subformulario").forEach((f) => f.classList.remove("subformulario--visible"));
}

function mostrarSubformulario(id) {
  ocultarSubformularios();
  document.getElementById(id).classList.add("subformulario--visible");
}

async function agregarAlPortafolio(evento) {
  evento.preventDefault();
  const formulario = evento.target;
  const idAccion = Number(formulario.dataset.idAccion);
  const cantidad = Number(formulario.cantidad.value);

  const respuesta = await llamarApi("/inversionista/portafolio/inversiones", {
    method: "POST",
    body: JSON.stringify({ idAccion, cantidad }),
  });

  if (respuesta && respuesta.ok) {
    cerrarModalEmpresa();
    formulario.reset();
    cargarResumenPortafolio();
  }
}

async function crearAlerta(evento) {
  evento.preventDefault();
  const formulario = evento.target;
  const idAccion = Number(formulario.dataset.idAccion);
  const precioObjetivo = Number(formulario.precioObjetivo.value);

  const respuesta = await llamarApi("/inversionista/alertas", {
    method: "POST",
    body: JSON.stringify({ idAccion, precioObjetivo }),
  });

  if (respuesta && respuesta.ok) {
    cerrarModalEmpresa();
    formulario.reset();
  }
}

// ---------- Vista: Alertas ----------

async function cargarAlertas() {
  const cuerpoTabla = document.getElementById("tabla-alertas-cuerpo");
  const respuesta = await llamarApi("/inversionista/alertas");
  if (!respuesta) return;

  if (!respuesta.ok) {
    cuerpoTabla.innerHTML = `<tr><td colspan="5" class="estado-vacio">No fue posible cargar tus alertas.</td></tr>`;
    return;
  }

  const alertas = await respuesta.json();

  if (alertas.length === 0) {
    cuerpoTabla.innerHTML = `<tr><td colspan="5" class="estado-vacio">No tienes alertas creadas. Créalas desde el detalle de una empresa.</td></tr>`;
    return;
  }

  cuerpoTabla.innerHTML = alertas
    .map(
      (alerta) => `
        <tr>
          <td class="celda-simbolo">${alerta.simbolo}</td>
          <td class="celda-numero">${formatearMoneda(alerta.precioObjetivo)}</td>
          <td class="celda-numero">${formatearMoneda(alerta.precioActual)}</td>
          <td><span class="estado-pill estado-pill--${alerta.estado.toLowerCase()}">${alerta.estado}</span></td>
          <td>${alerta.estado === "Activa" ? `<button class="boton-fila boton-fila--cancelar" onclick="cancelarAlerta(${alerta.idAlerta})">Cancelar</button>` : ""}</td>
        </tr>`
    )
    .join("");
}

async function cancelarAlerta(idAlerta) {
  const respuesta = await llamarApi(`/inversionista/alertas/${idAlerta}/cancelar`, { method: "PATCH" });
  if (respuesta && respuesta.ok) cargarAlertas();
}

// ---------- Perfil / cierre de sesión ----------

function cargarPerfil() {
  document.getElementById("perfil-nombre").textContent = sessionStorage.getItem("tourinvest_nombre") || "—";
  document.getElementById("perfil-rol").textContent = sessionStorage.getItem("tourinvest_rol") || "—";
}

function cerrarSesion() {
  sessionStorage.clear();
  window.location.href = "login.html";
}

// ---------- Arranque ----------

document.addEventListener("DOMContentLoaded", () => {
  if (!obtenerToken()) return;

  document.getElementById("nombre-usuario-topbar").textContent = sessionStorage.getItem("tourinvest_nombre") || "";
  document.getElementById("rol-usuario-topbar").textContent = sessionStorage.getItem("tourinvest_rol") || "";

  inicializarNavegacion();
  cargarResumenPortafolio();
  cargarPerfil();

  // Unifica el cierre del modal (backdrop + ESC) en un solo sistema: .modal--open
  if (window.TourInvestUI && window.TourInvestUI.initModals) {
    window.TourInvestUI.initModals();
  }
}); 