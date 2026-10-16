
create database findex;

create user findex_user WITH PASSWORD 'findex1234';
GRANT CONNECT ON DATABASE findex TO findex_user;

GRANT CONNECT ON DATABASE findex TO findex_user;

GRANT USAGE ON SCHEMA public TO findex_user;

GRANT SELECT, INSERT, UPDATE, DELETE
      ON ALL TABLES IN SCHEMA public
          TO findex_user;

GRANT USAGE, SELECT
             ON ALL SEQUENCES IN SCHEMA public
                 TO findex_user;


-------------------------------------------

create table index_categories (
    id serial primary key ,
    created_at timestamp NOT NULL ,
    updated_at timestamp,
    category_name varchar(20) NOT NULL unique
);

create table index_infos(
    id serial PRIMARY KEY ,
    created_at timestamp NOT NULL ,
    updated_at timestamp,
    index_category_id int references  index_categories(id) NOT NULL ,
    index_name varchar(20) NOT NULL ,
    employed_items_count int NOT NULL ,
    base_point_time timestamp NOT NULL ,
    base_index decimal(20,6) NOT NULL ,
    source_type varchar(10) check (source_type in ('사용자', 'Open API')) NOT NULL ,
    favorite boolean NOT NULL ,
    unique (index_category_id,index_name)
)