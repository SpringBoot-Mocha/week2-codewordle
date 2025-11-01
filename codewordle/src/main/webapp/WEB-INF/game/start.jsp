<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Iniciar CodeWordle</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="container py-5 text-center">
    <h1 class="text-primary mb-4">¡Bienvenido a CodeWordle!</h1>
    <form action="/game/start" method="get" class="w-50 mx-auto">
        <label class="form-label">Selecciona un tema:</label>
        <select name="topic" class="form-select mb-3">
            <option value="Java">Java</option>
            <option value="Spring">Spring</option>
            <option value="SQL">SQL</option>
        </select>
        <button class="btn btn-success w-100">Iniciar partida</button>
    </form>
</body>
</html>
