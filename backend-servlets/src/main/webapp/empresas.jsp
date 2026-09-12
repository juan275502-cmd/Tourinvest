<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html lang="es">

        <head>
            <meta charset="UTF-8">
            <title>TourInvest | Gestión de Empresas</title>
        </head>

        <body>
            <h2>Gestión de Empresas</h2>

            <c:if test="${not empty mensajeExito}">
                <p style="color:green;">${mensajeExito}</p>
            </c:if>
            <c:if test="${not empty mensajeError}">
                <p style="color:red;">${mensajeError}</p>
            </c:if>
            <p>
                Sesión: ${sessionScope.nombreUsuario != null ? sessionScope.nombreUsuario : "invitado"}
                &nbsp;|&nbsp;<a href="${pageContext.request.contextPath}/logout">Cerrar sesión</a>
            </p>

            <h3>Crear nueva empresa</h3>
            <form action="empresas" method="post">
                <label>Nombre</label><br>
                <input type="text" name="nombre" maxlength="120" required><br><br>

                <label>Sector</label><br>
                <input type="text" name="sector" maxlength="80" required><br><br>

                <label>País</label><br>
                <input type="text" name="pais" maxlength="80" required><br><br>

                <label>Símbolo</label><br>
                <input type="text" name="simbolo" maxlength="10" required><br><br>

                <button type="submit">Guardar empresa</button>
            </form>

            <h3>Empresas registradas</h3>
            <table border="1" cellpadding="6">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Sector</th>
                        <th>País</th>
                        <th>Símbolo</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="empresa" items="${listaEmpresas}">
                        <tr>
                            <td>${empresa[0]}</td>
                            <td>${empresa[1]}</td>
                            <td>${empresa[2]}</td>
                            <td>${empresa[3]}</td>
                            <td>${empresa[4]}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <p><a href="${pageContext.request.contextPath}/">Volver al inicio</a></p>
        </body>

        </html>