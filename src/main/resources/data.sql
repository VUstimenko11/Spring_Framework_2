INSERT INTO authors (full_name) VALUES ('Leo Tolstoy');
INSERT INTO authors (full_name) VALUES ('Fyodor Dostoevsky');
INSERT INTO authors (full_name) VALUES ('Anton Chekhov');

INSERT INTO genres (name) VALUES ('Novel');
INSERT INTO genres (name) VALUES ('Drama');
INSERT INTO genres (name) VALUES ('Short story');

INSERT INTO books (title, author_id, genre_id) VALUES ('War and Peace', 1, 1);
INSERT INTO books (title, author_id, genre_id) VALUES ('Anna Karenina', 1, 1);
INSERT INTO books (title, author_id, genre_id) VALUES ('Crime and Punishment', 2, 1);
INSERT INTO books (title, author_id, genre_id) VALUES ('The Cherry Orchard', 3, 2);
INSERT INTO books (title, author_id, genre_id) VALUES ('The Lady with the Dog', 3, 3);
