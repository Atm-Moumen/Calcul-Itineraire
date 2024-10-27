drop table if exists aretes;
drop table if exists stations;
drop table if exists lignes;
drop table if exists horaires;

create table stations (
      id integer primary key autoincrement,
      nom varchar(100) not null,
      x varchar(100) not null,
      y varchar(100) not null,
      nom_bis varchar(100) not null
);

create table lignes (
    nom varchar(10) not null,
    variante varchar(10) not null,
    primary key (nom, variante)
);

create table aretes (
        stationA int not null,
        stationB int not null ,
        distanceAB FLOAT not null,
        tempsAB varchar(100) not null,
        nom varchar(10) not null,
        variante varchar(10) not null,
        primary key (stationA, stationB, nom, variante),
        foreign key (stationA) references stations(id),
        foreign key (stationB) references stations(id),
        foreign key (nom, variante) references lignes(nom, variante)
);

create table horaires (
      ligne varchar(10) not null,
      station varchar(100) not null,
      heure varchar(10) not null,
      minutes varchar(10) not null,
      variante varchar(10) not null,
      foreign key (ligne) references lignes(nom),
      foreign key (variante) references lignes(variante),
      foreign key (station) references stations(nom),
      primary key (ligne, station, heure, minutes, variante)

);