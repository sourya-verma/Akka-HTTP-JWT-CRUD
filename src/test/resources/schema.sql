
CREATE TABLE student(id int PRIMARY KEY auto_increment,name varchar(50), email varchar(200), university_id int);

CREATE TABLE university(id int PRIMARY KEY, university_name varchar(50), location varchar(200));

CREATE TABLE user_cred(id int PRIMARY KEY, fname varchar(50),lname varchar(50) , password varchar(50));
