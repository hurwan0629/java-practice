CREATE TABLE member (
    member_pk BIGSERIAL PRIMARY KEY,
    member_id VARCHAR(50) NOT NULL UNIQUE,
    member_name VARCHAR(100) NOT NULL,
    member_password_hash VARCHAR(255) NOT NULL,
    member_email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE post (
    post_pk BIGSERIAL PRIMARY KEY,
    post_title VARCHAR(100) NOT NULL,
    writer_pk BIGINT NOT NULL,
    post_content TEXT NOT NULL,
    post_view_count BIGINT DEFAULT 0 NOT NULL,
    post_created_at TIMESTAMP NOT NULL DEFAULT now(),
    post_updated_at TIMESTAMP NULL,
    post_deleted BOOLEAN DEFAULT false,

    CONSTRAINT fk_post_writer
        foreign key (writer_pk)
        references member(member_pk)
);

DROP TABLE post;
