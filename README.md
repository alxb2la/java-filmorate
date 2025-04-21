# java-filmorate
Template repository for Filmorate project.


## Database ER diagram (ver.2 sprint 12)
![ER_diag_filmorate.png](ER_diag_filmorate.png)










## Описание таблиц


```
users
--
id bigint PK
name varchar(100)
email varchar(100)
login varchar(100)
birthday date


user_friend
--
user_id bigint PK FK >- users.id
friend_id bigint PK FK >- users.id


film_like
--
user_id bigint PK FK >- users.id
film_id bigint PK FK >- films.id


films
--
id bigint PK
name varchar(100)
description varchar(200)
release_date date
duration int
mpa_rating_id int FK >- mpa_rating.mpa_rating_id


film_genre
--
film_id bigint PK FK >- films.id
genre_id int PK FK >- genres.genre_id


genres
--
genre_id int PK
name varchar(100)


mpa_rating
--
mpa_rating_id int PK
name varchar(30)
```