<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>CodeWordle</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .word-box {
            display: inline-block;
            padding: 10px 15px;
            margin: 5px;
            font-weight: bold;
            border-radius: 8px;
            color: white;
            min-width: 35px;
            text-align: center;
        }
        .correct {
            background-color: #198754; /* verde */
        }
        .present {
            background-color: #ffc107; /* amarillo */
        }
        .absent {
            background-color: #6c757d; /* gris */
        }
    </style>
</head>
<body class="container py-5">
    <div class="text-center mb-4">
        <h1 class="fw-bold text-primary">CodeWordle</h1>
        <p class="lead">${message}</p>
    </div>

    <div class="card shadow-sm p-4 mb-4">
        <h5>Tema: <strong>${game.topic}</strong></h5>
        <h6>Intentos: ${game.attempts}</h6>

        <form action="/game/attempt" method="post" class="mt-3 d-flex justify-content-center">
            <input type="hidden" name="id_game" value="${game.id_game}">
            <input type="text" name="attempt" class="form-control w-25 me-2" placeholder="Escribe tu intento" required>
            <button type="submit" class="btn btn-primary">Enviar</button>
        </form>
    </div>

    <c:if test="${not empty attempts}">
        <div class="card shadow-sm p-4">
            <h5 class="mb-3">Historial de intentos:</h5>
            <ul class="list-group">
                <c:forEach var="a" items="${attempts}">
                    <li class="list-group-item">
                        <c:forEach var="ch" items="${fn:split(a.result, ',')}">
                            <span class="word-box ${fn:trim(ch)}">${fn:substring(ch,0,1)}</span>
                        </c:forEach>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </c:if>

    <c:if test="${not empty lastResult}">
        <div class="text-center mt-4">
            <h4>Último resultado:</h4>
            <p>${lastResult}</p>
        </div>
    </c:if>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
