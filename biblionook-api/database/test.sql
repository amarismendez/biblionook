drop database if exists biblionook_test;
create database biblionook_test;
use biblionook_test;

-- tables for authentication
create table app_user (
    appUserId int primary key auto_increment,
    username varchar(50) not null unique,
    passwordHash varchar(2048) not null,
    enabled bit not null default(1)
);

create table app_role (
    appRoleId int primary key auto_increment,
    `name` varchar(50) not null unique
);

create table app_user_role (
    appUserId int not null,
    appRoleId int not null,
    constraint pk_app_user_role
        primary key (appUserId, appRoleId),
    constraint fk_app_user_role_userId
        foreign key (appUserId)
        references app_user(appUserId),
    constraint fk_app_user_role_roleId
        foreign key (appRoleId)
        references app_role(appRoleId)
);

-- tables for app-specific models
create table library_item (
	libraryItemId int primary key auto_increment,
    isbn varchar(15) not null,
    rating int,
    `description` varchar(2000),
    `status` varchar(50) not null, -- status will be an enum in our java backend
    appUserId int not null,
    constraint fk_library_item_appUserId
		foreign key (appUserId)
        references app_user(appUserId)
);

create table `quote` (
	quoteId int primary key auto_increment,
    `text` varchar(2000) not null,
    pageNumber int,
    libraryItemId int not null,
    constraint fk_quote_libraryItemId
		foreign key (libraryItemId)
        references library_item(libraryItemId)
);

create table `comment` (
	commentId int primary key auto_increment,
	`text` varchar(2000) not null,
	likes int not null default 0,
    likeIds varchar(2000) not null default "", -- a serialized string to keep track of which users have liked this comment
	createdAt date not null,
	parentCommentId int default null, -- replies have a parentCommentId
	isbn varchar(15) not null, -- the book isbn for which this comment is made
	appUserId int not null, -- the user who made this comment
	constraint fk_comment_appUserId
		foreign key (appUserId)
		references app_user(appUserId),
	constraint fk_comment_parentCommentId
		foreign key (parentCommentId)
		references `comment`(commentId) 
);

-- set known good state for tests
delimiter //
create procedure set_known_good_state()
begin
-- wipe all tables clean & reset ids to start from 1
	delete from app_user_role;
	alter table app_user_role auto_increment=1;
    delete from app_role;
    alter table app_role auto_increment=1;
    update `comment` set parentCommentId = null; -- get rid of all parent relationships before wiping entire table
    delete from `comment`;
    alter table `comment` auto_increment=1;
	delete from `quote`;
    alter table `quote` auto_increment=1;
    delete from library_item;
    alter table library_item auto_increment=1;
	delete from app_user;
    alter table app_user auto_increment=1;

-- re-insert test data
insert into app_role (`name`) values
    ('USER'),
    ('ADMIN');

insert into app_user (username, passwordHash, enabled) -- all passwords are set to "P@ssw0rd!"
    values
    ('amaris@mendez.com', '$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa', 1), -- admin
    ('sally@jones.com', '$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa', 1), -- user
	('john@smith.com', '$2a$10$ntB7CsRKQzuLoKY3rfoAQen5nNyiC/U60wBsWnnYrtQQi8Z3IZzQa', 1); -- user
    
insert into app_user_role
    values
    (1, 2),
    (2, 1),
    (3, 1);
    
insert into library_item (isbn, rating, `description`, `status`, appUserId)
	values
    (9781451696196, 10, "I LOVED Perks of Being a Wallflower! I can't wait to read it a second time.", "COMPLETED", 1),
    (9780571334643, 7, "Normal People was a very cute read, I loved the relationship between the main characters. But I wish the ending was a bit clearer...", "COMPLETED", 2),
    (9780670813025, 8, "I'm currently reading It by Stephen King and it's really great. Can't wait to see how everything pans out.", "IN_PROGRESS", 3),
    (9780593598429, null, null, "TO_BE_READ", 1);
    
insert into `quote` (`text`, pageNumber, libraryItemId)
	values
    ("We accept the love we think we deserve.", 24, 1),
    ("And in that moment, I swear we were infinite.", 118, 1),
    ("They were cheering together, they had seen something magical which dissolved the ordinary social relations between them.", 12, 2);
    
insert into `comment` (`text`, likes, likeIds, createdAt, parentCommentId, isbn, appUserId)
	values
	("Was anyone confused by the ending of this book?? Or is it just me?", 1, "3", "2023-05-01", null, 9781451696196, 2),
    ("Me too! Like what was that about Aunt Helen???", 0, "", "2023-05-01", 1, 9781451696196, 3);
       
end //
delimiter ;