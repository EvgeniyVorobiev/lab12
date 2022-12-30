delete from items;

INSERT INTO items values (default, 0, 0),(default, 0, 0), (default, 0, 0), (default, 0, 0),
(default, 0, 0), (default, 0, 0), (default, 0, 0), (default, 0, 0),
(default, 0, 0), (default, 0, 0), (default, 0, 0), (default, 0, 0),
(default, 0, 0), (default, 0, 0), (default, 0, 0), (default, 0, 0),
(default, 0, 0), (default, 0, 0), (default, 0, 0), (default, 0, 0),
(default, 0, 0), (default, 0, 0), (default, 0, 0), (default, 0, 0),
(default, 0, 0), (default, 0, 0), (default, 0, 0), (default, 0, 0),
(default, 0, 0), (default, 0, 0), (default, 0, 0), (default, 0, 0),
(default, 0, 0), (default, 0, 0), (default, 0, 0), (default, 0, 0),
(default, 0, 0),(default, 0, 0),(default, 0, 0), (default, 0, 0);
select sum(items.value) from items;

delete from items;