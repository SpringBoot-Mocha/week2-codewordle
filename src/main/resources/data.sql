INSERT INTO themes (name, description) VALUES
('JAVA', 'Términos relacionados con el lenguaje Java'),
('SPRING', 'Conceptos del framework Spring'),
('DEVOPS', 'Herramientas y prácticas DevOps'),
('DATABASE', 'Términos de bases de datos');

INSERT INTO words (word, theme_id, hint) VALUES
('CLASS', 1, 'Plantilla para crear objetos'),
('ARRAY', 1, 'Estructura de datos indexada'),
('LAMBDA', 1, 'Expresión funcional anónima'),
('STATIC', 1, 'Miembro de clase no de instancia'),
('FINAL', 1, 'No puede ser modificado'),
('SUPER', 1, 'Referencia a la clase padre'),
('CATCH', 1, 'Captura excepciones'),
('WHILE', 1, 'Bucle condicional'),
('BREAK', 1, 'Sale de un bucle'),
('THROW', 1, 'Lanza una excepción');

INSERT INTO words (word, theme_id, hint) VALUES
('BEANS', 2, 'Objetos gestionados por Spring'),
('SCOPE', 2, 'Ciclo de vida del bean'),
('MODEL', 2, 'Datos para la vista'),
('VALID', 2, 'Validación de datos'),
('QUERY', 2, 'Consulta personalizada'),
('VALUE', 2, 'Inyecta propiedades'),
('TABLE', 2, 'Mapeo de entidad'),
('CROSS', 2, 'Problema de CORS'),
('PATCH', 2, 'Actualización parcial'),
('TRACE', 2, 'Nivel de logging');

INSERT INTO words (word, theme_id, hint) VALUES
('BUILD', 3, 'Compilar proyecto'),
('NGINX', 3, 'Servidor web y proxy'),
('CACHE', 3, 'Almacenamiento temporal'),
('PROXY', 3, 'Intermediario de red'),
('CLONE', 3, 'Copiar repositorio'),
('MERGE', 3, 'Unir ramas'),
('STACK', 3, 'Conjunto de tecnologías'),
('SCALE', 3, 'Ajustar capacidad'),
('QUEUE', 3, 'Cola de mensajes'),
('IMAGE', 3, 'Contenedor Docker');

INSERT INTO words (word, theme_id, hint) VALUES
('QUERY', 4, 'Consulta a base de datos'),
('INDEX', 4, 'Mejora velocidad de búsqueda'),
('TABLE', 4, 'Estructura de datos'),
('GRANT', 4, 'Otorgar permisos'),
('ALTER', 4, 'Modificar estructura'),
('WHERE', 4, 'Filtro de condición'),
('INNER', 4, 'Tipo de JOIN'),
('COUNT', 4, 'Contar registros'),
('LIMIT', 4, 'Restringir resultados'),
('GROUP', 4, 'Agrupar resultados');
