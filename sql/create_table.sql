DROP TABLE IF EXISTS mvcboard;
create table mvcboard (
	idx				int					primary key auto_increment,
	name			varchar(50)			not null,
	title			varchar(200)		not null,
	content			varchar(2000)		not null,
	postdate		date				default curdate() not null,
	ofile			varchar(200)		,
	sfile			varchar(30)			,
	downcount		int					default 0 not null,
	pass			varchar(50)			not null,
	visitcount		varchar(200)		default 0 not null
);


select * from mvcboard m ;