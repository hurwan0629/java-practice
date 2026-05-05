CREATE TABLE member (
    member_pk BIGSERIAL PRIMARY KEY,
    member_id VARCHAR(50) NOT NULL UNIQUE,
    member_name VARCHAR(100) NOT NULL,
    member_password_hash VARCHAR(255) NOT NULL,
    member_email VARCHAR(255) NOT NULL UNIQUE
);

SELECT * FROM member;

SELECT
    member.member_pk AS memberPk,
    member.member_name AS memberName,
    member.member_password_hash AS memberPasswordHash
FROM member
WHERE member_id='hurwan0629';