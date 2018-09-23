drop table if exists splittransaction;
drop table if exists transaction;
drop table if exists account;
drop table if exists payee;
drop table if exists category;


create table category
(
	id int not null auto_increment,
	major varchar(255),
	minor varchar(255),
	primary key (id),
	unique key idx_category (major, minor)
) ENGINE=InnoDB;

create table payee
(
	id int not null auto_increment,
	name varchar(255),
	primary key (id),
	unique key idx_payee_name (name)
) ENGINE=InnoDB;

create table account
(
	id int not null auto_increment,
	name varchar(255),
	openingbalance int,
	closed boolean,
	note varchar(255),
	primary key (id),
	unique key idx_account_name (name),
	index idx_status (closed)
) ENGINE=InnoDB;

create table transaction
(
	id int not null auto_increment,
	origid int,
	entered timestamp default 0,
	accountid int,
	payeeid int,
	categoryid int,
	split boolean,
	reference varchar(255),	
	amount int,
	memo varchar(255),
	reconciled boolean,
	transferid int,
	
	primary key (id),
	index idx_origid (origid),
	constraint foreign key (accountid) references account(id) on delete cascade,
	constraint foreign key (payeeid) references payee(id) on delete cascade,
	constraint foreign key (categoryid) references category(id) on delete cascade
) ENGINE=InnoDB;

create table splittransaction
(
	id int not null auto_increment,
	transactionid int,
	categoryid int,
	amount int,
	memo varchar(255),
	
	primary key (id),
	constraint foreign key (transactionid) references transaction(id) on delete cascade,
	constraint foreign key (categoryid) references category(id) on delete cascade
) ENGINE=InnoDB;
