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

INSERT INTO comments (text, book_id) VALUES ('A masterpiece of world literature', 1);
INSERT INTO comments (text, book_id) VALUES ('Long but worth reading', 1);
INSERT INTO comments (text, book_id) VALUES ('Tragic and profound', 2);
INSERT INTO comments (text, book_id) VALUES ('Psychological depth', 3);
INSERT INTO comments (text, book_id) VALUES ('Great play', 4);
