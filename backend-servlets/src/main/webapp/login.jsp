<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>TourInvest | Iniciar sesión</title>
</head>
<body>
    <h2>Iniciar sesión</h2>

    <c:if xmlns:c="jakarta.tags.core" test="${not empty mensajeError}">
        <p style="color:red;">${mensajeError}</p>
    </c:if>

    <form action="login" method="post">
        <label for="correo">Correo</label><br>
        <input type="email" id="correo" name="correo" required><br><br>

        <label for="contrasena">Contraseña</label><br>
        <input type="password" id="contrasena" name="contrasena" required><br><br>

        <button type="submit">Ingresar</button>
    </form>

    <p><a href="${pageContext.request.contextPath}/">Volver al inicio</a></p>
</body>
</html>