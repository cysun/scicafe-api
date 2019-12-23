create table hibernate_sequence (
    next_val bigint
);

create table programs (
       id bigint not null,
        description text not null,
        full_name varchar(255) not null,
        name varchar(255) not null,
        image_url varchar(255),
        primary key (id)
);

create table users (
       id bigint not null,
        email varchar(255) not null,
        first_name varchar(255) not null,
        last_name varchar(255) not null,
        password varchar(255) not null,
        position varchar(255),
        title varchar(255),
        program_id bigint,
        username varchar(255) not null unique,
        unit varchar(255),
		token varchar(255),
		enabled integer,
        primary key (id),
        foreign key (program_id) references programs (id)
);

create table events (
       id bigint not null,
        description text,
        event_date date,
        end_time time,
        name varchar(255),
        location varchar(255),
        start_time time,
        status integer,
        organizer_id bigint,
        image_url varchar(255),
        primary key (id),
        foreign key (organizer_id) references users (id)
);

create table tags (
       id bigint not null,
        name varchar(255) not null unique,
		description varchar(255),
        primary key (id)
);

create table rewards (
       id bigint not null,
        description varchar(2000),
        end_date datetime(6),
        name varchar(255),
        provider_name varchar(255),
        start_date datetime(6),
        status integer,
        criteria integer,
        submitter_id bigint,
        primary key (id)
);

create table roles (
       id bigint not null,
        name varchar(255) not null unique,
        primary key (id)
);

create table authorities (
       user_id bigint not null,
        role_id bigint not null,
        primary key (user_id, role_id),
        foreign key (user_id) references users (id),
        foreign key (role_id) references roles (id)
        
);

create table users_programs (
       user_id bigint not null,
        program_id bigint not null,
        primary key (user_id, program_id),
        foreign key (user_id) references users (id),
        foreign key (program_id) references programs (id)
);

create table users_events (
       user_id bigint not null,
        event_id bigint not null,
        primary key (user_id, event_id),
        foreign key (user_id) references users (id),
        foreign key (event_id) references events (id)
);

create table events_tags (
       tag_id bigint not null,
        event_id bigint not null,
        primary key (tag_id, event_id),
        foreign key (tag_id) references tags (id),
        foreign key (event_id) references events (id)

);

create table rewards_events (
       reward_id bigint not null,
        event_id bigint not null,
        primary key (reward_id, event_id),
        foreign key (reward_id) references rewards (id),
        foreign key (event_id) references events (id)
);

create table rewards_tags (
       reward_id bigint not null,
        tag_id bigint not null,
        primary key (reward_id, tag_id),
        foreign key (reward_id) references rewards (id),
        foreign key (tag_id) references tags (id)
);

create table news (
       id bigint not null,
        author varchar(255) not null,
        content text not null,
        image_url varchar(255),
        isTop varchar(255) not null,
        posted_date datetime(6) not null,
        title varchar(255) not null,
        primary key (id)
);

