drop table if exists partpayment;
drop table if exists payment;
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
	balance int,
	primary key (id),
	unique key idx_account_name (name)
) ENGINE=InnoDB;

create table payment
(
	id int not null auto_increment,
	origid int,
	entered timestamp,
	accountid int,
	payeeid int,
	categoryid int,
	reference varchar(255),
	
	charge int,
	memo varchar(255),
	reconciled boolean,
	transferid int,
	
	primary key (id),
	index idx_origid (origid),
	constraint foreign key (accountid) references account(id) on delete cascade,
	constraint foreign key (payeeid) references payee(id) on delete cascade,
	constraint foreign key (categoryid) references category(id) on delete cascade
) ENGINE=InnoDB;

create table partpayment
(
	paymentid int,
	categoryid int,
	charge int,
	memo varchar(255),
	
	primary key (paymentid, categoryid),
	constraint foreign key (paymentid) references payment(id) on delete cascade,
	constraint foreign key (categoryid) references category(id) on delete cascade
) ENGINE=InnoDB;
