drop table if exists splittransaction;
drop table if exists transaction;
drop table if exists account;
drop table if exists payee;
drop table if exists category;


create table category
(
	id int not null auto_increment,
	origid int,
	major varchar(255),
	minor varchar(255),
	primary key (id),
	index idx_category_origid (origid),
	unique key idx_category (major, minor)
) ENGINE=InnoDB;

create table payee
(
	id int not null auto_increment,
	origid int,
	name varchar(255),
	primary key (id),
	index idx_payee_origid (origid),
	unique key idx_payee_name (name)
) ENGINE=InnoDB;

create table account
(
	id int not null auto_increment,
	origid int,
	name varchar(255),
	type enum ('current', 'savings', 'credit', 'pension'),
	openingbalance int,
	closed boolean,
	note varchar(255),
	sortcode varchar(6),
	accountno varchar(8),
	rollno varchar(16),
	primary key (id),
	index idx_account_origid (origid),
	unique key idx_account_name (name),
	index idx_status (closed)
) ENGINE=InnoDB;

create table transaction
(
	id int not null auto_increment,
	/*
	 * source indicates where the data came from:
	 * 		2 - Acorn data
	 * 		1 - MSMoney
	 * 		0 - interactive input
	 */
	source int,
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
	index idx_transaction_origid (source, origid),
	index idx_transaction_account (accountid, entered desc),
	index idx_transaction_payee (payeeid, entered desc),
	index idx_transaction_category (categoryid, entered desc),
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

create table role
(
	id int not null auto_increment,
  name varchar(255) NOT NULL,
	primary key (id),
	unique key idx_role_name (name)
) ENGINE=InnoDB;

create table user
(
	id int not null auto_increment,
  name varchar(32),
  alias varchar(32),
  password varchar(255),
  enabled smallint,
  demo_user smallint,
	primary key (id),
	unique key idx_user_alias (alias)
) ENGINE=InnoDB;

create table userrole
(
  userid int NOT NULL,
  roleid int NOT NULL,
  primary key (userid, roleid),
	constraint foreign key (userid) references user(id) on delete cascade,
	constraint foreign key (roleid) references role(id) on delete cascade
) ENGINE=InnoDB;
