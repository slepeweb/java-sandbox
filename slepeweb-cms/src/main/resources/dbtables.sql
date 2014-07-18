drop table itemtype;
drop table item;
drop table site;

create table site
(
	id int(11) not null auto_increment,
	name varchar(255),
	hostname varchar(255),
	primary key (id),
	unique key idx_site_name (name),
	unique key idx_site_hostname (hostname)
);


create table itemtype
(
	id int(11) not null auto_increment,
	name varchar(255),
	primary key (id),
	unique key idx_itemtype_name (name)
);


create table item
(
	id int(11) not null auto_increment,
	name varchar(255),
	simplename varchar(255),
	path varchar(255),
	siteid int references site(id),
	typeid int references itemtype(id),
	datecreated timestamp,
	dateupdated timestamp,
	deleted boolean,
	primary key (id),
	unique key idx_item_site_path (siteid, path)
);
