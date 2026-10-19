
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
    base_point_time DATE NOT NULL ,
    base_index decimal(20,6) NOT NULL ,
    source_type varchar(10) check (source_type in ('USER', 'OPEN_API')) NOT NULL ,
    favorite boolean NOT NULL ,
    unique (index_category_id,index_name)
);

--------------------------------------------

CREATE TABLE index_data
(
    id                  SERIAL PRIMARY KEY,

    created_at          TIMESTAMP      NOT NULL,
    updated_at          TIMESTAMP,

    index_info_id       INT            NOT NULL,

    base_date           DATE           NOT NULL source_type VARCHAR(10) NOT NULL
    CHECK (source_type IN ('USER'. 'OPEN_API')),

    market_price        DECIMAL(20, 6) NOT NULL closing_price DECIMAL(20, 6) NOT NULL
    high_price DECIMAL(20, 6) NOT NULL
    low_price DECIMAL(20, 6) NOT NULL

    versus DECIMAL(20, 6) NOT NULL,
    fluctuation_rate    DECIMAL(15, 6) NOT NULL,

    trading_quantity    INT            NOT NULL,
    trading_price       DECIMAL(25, 3) NOT NULL,
    market_total_amount DECIMAL(27, 3)

        CONSTRAINT uk_index_data
            UNIQUE (index_info_id, base_date),

    CONSTRAINT fk_index_data_index_info
        FOREIGN KEY (index_info_id)
            REFERENCES index_info (id)
);