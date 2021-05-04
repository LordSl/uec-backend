drop database if exists `uec`;
create database uec default charset `utf8`;
use uec;

create table `user`(
    `id` int(11) not null auto_increment,
    `userName` varchar(256) not null,
    `password` varchar(256) not null,
    primary key (`id`)
)engine=`innodb` default charset=`utf8`;

create table `encrypt`(
    `fromId` int(11) not null,
    `toId` int(11) not null,
    `desKey` varchar(256) not null ,
    primary key (`fromId`,`toId`)
)engine=`innodb` default charset=`utf8`