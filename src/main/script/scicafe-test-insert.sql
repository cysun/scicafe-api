insert into hibernate_sequence values ( 200 );

insert into programs (id,name,fullName,description) values(101,'FYrE@ECST',' First-Year Experience Program at ECST','It is the First-Year Experience Program at ECST');
insert into programs (id,name,fullName,description) values(102,'ESP@IO','Exchange Students Program at International Office','It is the Exchange Students Program at International Office');
insert into programs (id,name,fullName,description) values(103,'Test','Test','Test');
insert into programs (id,name,fullName,description) values(104,'Test','Test','Test');

insert into roles (id,name) values (0, 'REGULAR');
insert into roles (id,name) values (1, 'ADMIN');
insert into roles (id,name) values (2, 'EVENT_ORGANIZOR');
insert into roles (id,name) values (3, 'REWARD_PROVIDER');

insert into tags (id,name) values(101,'ACM');
insert into tags (id,name) values(102,'IEEE');
insert into tags (id,name) values(103,'ECST');
insert into tags (id,name) values(104,'DUMMY');

insert into users (id,firstName,lastName,email,username,password,position,title,unit) values(101,'Peter','Parker','peterparker@gmail.com','admin','1234','Staff','Admin of Sci-CAFE','CSULA');
insert into users (id,firstName,lastName,email,username,password,position,title,unit) values(102,'Yi','Chen','chenyii426@gmail.com','chenyi','1234','Student','','College of Engineering, Computer Science, and Technology');
insert into users (id,firstName,lastName,email,username,password,position,title,unit) values(103,'Joe','Doe','joedoe123@gmail.com','joedoe','1234','Faculty','President of ACM Student Chapter','College of Engineering, Computer Science, and Technology');
insert into users (id,firstName,lastName,email,username,password,position,title,unit) values(104,'Pamula','Raj','pamularaj@gmail.com','pamularaj','1234','Faculty','CS Department Chair','College of Engineering, Computer Science, and Technology');
insert into users (id,firstName,lastName,email,username,password,position,title,unit) values(105,'test','test','test','test','1234','Faculty','test','test');

insert into events (id,name,description,location,organizer_id,eventDate,startTime,endTime,status) values(101,'ACM Student Chapter events','It is of the ACM Student Chapter events','ET-324',102,'2018-12-1','10:30:00','12:30:00',1);
insert into events (id,name,description,location,organizer_id,eventDate,startTime,endTime,status) values(102,'Dummy event','It is a dummy event','ET-111',101,'2019-1-1','00:00:00','23:59:59',1);
insert into events (id,name,description,location,organizer_id,eventDate,startTime,endTime,status) values(103,'Test event','It is a test event','ET-111',101,'2019-2-1','00:00:00','23:59:59',0);
insert into events (id,name,description,location,organizer_id,eventDate,startTime,endTime,status) values(104,'Test event','It is a test event','ET-111',101,'2019-3-1','00:00:00','23:59:59',0);

insert into rewards (id,name,description,providerName,submitter_id,startTime,endTime,criteria,status) values(101,'Extra Credit','It is extra credit for students who attend all 6 ACM Student Chapter events','Dr.Pamula',102,'2018-9-27 10:30:00','2018-9-30 10:30:00',6,1);
insert into rewards (id,name,description,providerName,submitter_id,startTime,endTime,criteria,status) values(102,'Cal State LA gears','It is reward for students who attend the dummy event','Dr.Pamula',102,'2018-9-27 10:30:00','2018-9-30 10:30:00',1,0);

insert into authorities (user_id, role_id) values (101, 1);
insert into authorities (user_id, role_id) values (102, 0);
insert into authorities (user_id, role_id) values (103, 2);
insert into authorities (user_id, role_id) values (104, 3);

insert into rewards_tags (reward_id, tag_id) values (101, 101);

insert into rewards_events (reward_id, event_id) values (101, 101);

insert into users_events (user_id, event_id) values (102, 101);
insert into users_events (user_id, event_id) values (102, 102);

insert into users_programs (user_id, program_id) values (102, 101);
insert into users_programs (user_id, program_id) values (102, 102);

insert into events_tags (tag_id, event_id) values (101, 101);
insert into events_tags (tag_id, event_id) values (104, 101);
insert into events_tags (tag_id, event_id) values (104, 102);




