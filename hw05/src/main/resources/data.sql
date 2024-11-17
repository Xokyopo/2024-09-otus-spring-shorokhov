insert into authors(full_name)
values ('Джоан Роулинг'),
       ('Джон Рональд Руэл Толкин'),
       ('Антуана де Сент-Экзюпери');

insert into genres(name)
values ('Роман'),
       ('Роман-эпопея'),
       ('Аллегорическая повесть');

insert into books(title, author_id, genre_id)
values ('Гарри Поттер', 1, 1),
       ('Властелин колец', 2, 2),
       ('Маленький принц', 3, 3);
