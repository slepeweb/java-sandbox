drop table fieldvalue;
drop table fieldfortype;
drop table field;
drop table link;
drop table itemtype;
drop table item;
drop table site;

create table site
(
	id int not null auto_increment,
	name varchar(255),
	hostname varchar(255),
	primary key (id),
	unique key idx_site_name (name),
	unique key idx_site_hostname (hostname)
);


create table itemtype
(
	id int not null auto_increment,
	name varchar(255),
	primary key (id),
	unique key idx_itemtype_name (name)
);


create table item
(
	id int not null auto_increment,
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

create table link
(
	parentid int references item(id),
	childid int references item(id),
	linktype enum ('binding', 'relation', 'inline', 'shortcut'),
	name varchar(64),
	ordering smallint,
	primary key (parentid, childid)
);

create table field
(
   id int not null auto_increment,
   name varchar(64) not null,
   variable varchar(32) not null,
   fieldtype enum ('text', 'markup', 'integer', 'date', 'url'),
   size int not null,
   helptext varchar(512),
   primary key (id),
   unique key idx_field_variable (variable)
);

create table fieldfortype
(
   fieldid int references field(id),
   itemtypeid int references itemtype(id),
   fieldorder int not null,
   mandatory boolean,
   primary key (fieldid, itemtypeid)
);

create table fieldvalue
(
   fieldid int references field(id),
   itemid int references item(id),
   stringvalue text,
   integervalue int,
   datevalue timestamp,
   primary key (itemid, fieldid)
);
