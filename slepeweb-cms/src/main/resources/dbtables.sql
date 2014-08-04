drop table media;
drop table fieldvalue;
drop table fieldfortype;
drop table field;
drop table link;
drop table item;
drop table itemtype;
drop table site;

create table site
(
	id int not null auto_increment,
	name varchar(255),
	hostname varchar(255),
	primary key (id),
	unique key idx_site_name (name),
	unique key idx_site_hostname (hostname)
) ENGINE=InnoDB;


create table itemtype
(
	id int not null auto_increment,
	name varchar(255),
	media boolean,
	primary key (id),
	unique key idx_itemtype_name (name)
) ENGINE=InnoDB;


create table item
(
	id int not null auto_increment,
	name varchar(255),
	simplename varchar(255),
	path varchar(255),
	siteid int,
	typeid int,
	datecreated timestamp,
	dateupdated timestamp,
	deleted boolean,
	primary key (id),
	unique key idx_item_site_path (siteid, path),
	constraint foreign key (siteid) references site(id) on delete cascade,
	constraint foreign key (typeid) references itemtype(id) on delete cascade
) ENGINE=InnoDB;

create table link
(
	parentid int,
	childid int,
	linktype enum ('binding', 'relation', 'inline', 'shortcut'),
	name varchar(64),
	ordering smallint,
	primary key (parentid, childid),
	unique key idx_link_child_linktype (childid, linktype),
	constraint foreign key (parentid) references item(id) on delete cascade,
	constraint foreign key (childid) references item(id) on delete cascade
) ENGINE=InnoDB;

create table field
(
   id int not null auto_increment,
   name varchar(64) not null,
   variable varchar(32) not null,
   fieldtype enum ('text', 'markup', 'integer', 'date', 'url'),
   size int not null,
   helptext varchar(512),
   dflt varchar(64),
   primary key (id),
   unique key idx_field_variable (variable)
) ENGINE=InnoDB;

create table fieldfortype
(
   fieldid int,
   itemtypeid int,
   fieldorder int not null,
   mandatory boolean,
   primary key (fieldid, itemtypeid),
	 constraint foreign key (fieldid) references field(id) on delete cascade,
	 constraint foreign key (itemtypeid) references itemtype(id) on delete cascade
) ENGINE=InnoDB;

create table fieldvalue
(
   fieldid int,
   itemid int,
   stringvalue text,
   integervalue int,
   datevalue timestamp,
   primary key (itemid, fieldid),
	 constraint foreign key (fieldid) references field(id) on delete cascade,
	 constraint foreign key (itemid) references item(id) on delete cascade
) ENGINE=InnoDB;

create table media
(
   itemid int,
   data mediumblob,
   primary key (itemid),
	 constraint foreign key (itemid) references item(id) on delete cascade
) ENGINE=InnoDB;
