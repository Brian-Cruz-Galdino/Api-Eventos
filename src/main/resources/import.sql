-- Inserir artistas
INSERT INTO artista (id, nome, genero_musical, biografia) VALUES
(1, 'Coldplay', 'Rock', 'Banda britanica de rock formada em 1996'),
(2, 'Beyonce', 'Pop/R&B', 'Cantora, compositora e produtora musical americana'),
(3, 'Drake', 'Hip-Hop/Rap', 'Rapper, cantor e compositor canadense'),
(4, 'Lady Gaga', 'Pop', 'Cantora, compositora e atriz americana');

-- Inserir eventos
INSERT INTO evento (id, nome, descricao, data_evento, local, capacidade_maxima, preco_ingresso, status) VALUES
(1, 'Festival de Verao', 'O maior festival de verao do pais com os melhores artistas', '2024-01-15', 'Praia de Copacabana', 50000, 250.0, 'DISPONIVEL'),
(2, 'Rock in Rio', 'O famoso festival de rock que acontece no Brasil', '2024-09-15', 'Parque Olimpico', 100000, 350.0, 'DISPONIVEL'),
(3, 'Lollapalooza', 'Festival de musica alternativa que reune diversos generos', '2024-03-25', 'Autodromo de Interlagos', 80000, 300.0, 'DISPONIVEL'),
(4, 'Tomorrowland', 'Festival de musica eletronica mais famoso do mundo', '2024-07-20', 'Boom, Belgica', 60000, 500.0, 'DISPONIVEL');

-- Inserir relação entre eventos e artistas
INSERT INTO evento_artista (evento_id, artista_id) VALUES
(1, 1), (1, 2),
(2, 1), (2, 3),
(3, 2), (3, 4),
(4, 3), (4, 4);

-- Inserir ingressos
INSERT INTO ingresso (id, nome_comprador, email_comprador, data_compra, quantidade, preco_total, status, evento_id) VALUES
(1, 'Joao Silva', 'joao@email.com', '2023-11-10 10:30:00', 2, 500.0, 'PAGO', 1),
(2, 'Maria Santos', 'maria@email.com', '2023-11-11 14:45:00', 1, 250.0, 'RESERVADO', 1),
(3, 'Pedro Costa', 'pedro@email.com', '2023-11-12 09:15:00', 4, 1400.0, 'PAGO', 2),
(4, 'Ana Oliveira', 'ana@email.com', '2023-11-13 16:20:00', 2, 600.0, 'RESERVADO', 3);