drop table if exists axisvalue;
drop table if exists axis;
drop table if exists variant;
drop table if exists product;

drop table if exists tag;
drop table if exists loglevel;
drop table if exists config;
drop table if exists media;
drop table if exists fieldvalue;
drop table if exists fieldfortype;
drop table if exists field;
drop table if exists link;
drop table if exists linktype;
drop table if exists linkname;
drop table if exists item;
drop table if exists template;
drop table if exists itemtype;
drop table if exists host;
drop table if exists site;


create table site
(
	id int not null auto_increment,
	name varchar(255),
	shortname varchar(8),
	primary key (id),
	unique key idx_site_name (name),
	unique key idx_site_shortname (shortname),
) ENGINE=InnoDB;

create table host
(
	id int not null auto_increment,
	siteid int,
	name varchar(255),
	primary key (id),
	unique key idx_host_name (name)
) ENGINE=InnoDB;

create table itemtype
(
	id int not null auto_increment,
	name varchar(255),
	mimetype varchar(64),
	privatecache int,
	publiccache int,
	primary key (id),
	unique key idx_itemtype_name (name)
) ENGINE=InnoDB;


create table template
(
	id int not null auto_increment,
	name varchar(255),
	forward varchar(255),
	siteid int,
	typeid int,
	primary key (id),
	unique key idx_template_site_name (siteid, name),
	constraint foreign key (siteid) references site(id) on delete cascade,
	constraint foreign key (typeid) references itemtype(id) on delete cascade
) ENGINE=InnoDB;


create table item
(
	id int not null auto_increment,
	origid int,
	name varchar(255),
	simplename varchar(255),
	path varchar(255),
	siteid int,
	typeid int,
	templateid int,
	datecreated timestamp,
	dateupdated timestamp,
	deleted boolean,
	editable boolean,
	published boolean,
	searchable boolean,
	version int,
	primary key (id),
	unique key idx_item_site_path (siteid, path, version),
	index idx_origid (origid),
	index idx_deleted (deleted),
	index idx_editable (editable),
	index idx_published (published),
	constraint foreign key (siteid) references site(id) on delete cascade,
	constraint foreign key (typeid) references itemtype(id) on delete cascade
) ENGINE=InnoDB;


create table linktype
(
	id int not null auto_increment,
	name varchar(24),
	primary key (id),
	unique key idx_linktype_name (name)
) ENGINE=InnoDB;

create table linkname
(
	id int not null auto_increment,
	siteid int,
	linktypeid int,
	name varchar(64),
	primary key (id),
	unique key idx_linkname_site_type_name (siteid, linktypeid, name),
	constraint foreign key (siteid) references site(id) on delete cascade,
	constraint foreign key (linktypeid) references linktype(id) on delete cascade
) ENGINE=InnoDB;

create table link
(
	parentid int,
	childid int,
	linktypeid int,
	linknameid int,
	ordering smallint,
	primary key (parentid, childid),
	index idx_link_child (childid),
	constraint foreign key (parentid) references item(id) on delete cascade,
	constraint foreign key (childid) references item(id) on delete cascade,
	constraint foreign key (linktypeid) references linktype(id) on delete cascade,
	constraint foreign key (linknameid) references linkname(id) on delete cascade
) ENGINE=InnoDB;

create table field
(
   id int not null auto_increment,
   name varchar(64) not null,
   variable varchar(32) not null,
   fieldtype enum ('text', 'markup', 'integer', 'date', 'url', 'radio', 'checkbox', 'select', 'datetime'),
   size int not null,
   helptext varchar(512),
   dflt varchar(64),
   valid varchar(512),
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
   size int,
   primary key (itemid),
	 constraint foreign key (itemid) references item(id) on delete cascade
) ENGINE=InnoDB;


create table config
(
	siteid int,
	name varchar(128),
	value varchar(1023),
	primary key (siteid, name),
	constraint foreign key (siteid) references site(id) on delete cascade
) ENGINE=InnoDB;

create table loglevel
(
	package varchar(512),
  level enum ('TRACE', 'DEBUG', 'INFO', 'WARN', 'ERROR'),
	primary key (package)
) ENGINE=InnoDB;

create table tag
(
   siteid int,
   itemid int,
   value varchar(256),
   primary key (itemid, value),
   index idx_tag_site_value (siteid, value),
	 constraint foreign key (itemid) references item(id) on delete cascade
) ENGINE=InnoDB;

create table product
(
   origitemid int,
   siteid int,
   partnum varchar(256) not null,
   stock int,
   price int,
   rrp int,
   shippingweight int,
   alphaaxisid int,
   betaaxisid int,
   primary key (origitemid),
   unique key idx_product_partnum (siteid, partnum)
) ENGINE=InnoDB;

create table variant
(
   origitemid int,
   qualifier varchar(32) not null,
   stock int,
   price int,
   alphavalueid int,
   betavalueid int default -1,
   primary key (origitemid, alphavalueid, betavalueid),
   unique key idx_variant_unique (origitemid, qualifier),
	 constraint foreign key (origitemid) references product(origitemid) on delete cascade
) ENGINE=InnoDB;

create table axis
(
   id int not null auto_increment,
   shortname varchar(16) not null,
   label varchar(32) not null,
   units varchar(16),
   description varchar(256),
   primary key (id),
   unique key idx_axis_shortname (shortname)
) ENGINE=InnoDB;

create table axisvalue
(
   id int not null auto_increment,
   axisid int,
   value varchar(64) not null,
   ordering int not null,
   primary key (id),
   unique key idx_axisvalue_value (value),
	 constraint foreign key (axisid) references axis(id) on delete cascade
) ENGINE=InnoDB;
