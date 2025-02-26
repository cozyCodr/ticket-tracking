-- Create audit_log table
CREATE TABLE audit_log
(
    id           RAW(16) NOT NULL PRIMARY KEY,
    created_date TIMESTAMP(6),
    log_message  CLOB NOT NULL,
    updated_date TIMESTAMP(6)
);

-- Create app_user table
CREATE TABLE app_user
(
    id           RAW(16) NOT NULL PRIMARY KEY,
    created_date TIMESTAMP(6),
    first_name   VARCHAR2(255) NOT NULL,
    last_name    VARCHAR2(255) NOT NULL,
    password     VARCHAR2(255) NOT NULL,
    role         VARCHAR2(255) CONSTRAINT app_user_role_check CHECK (role IN ('EMPLOYEE', 'IT_SUPPORT')),
    updated_date TIMESTAMP(6),
    username     VARCHAR2(255) NOT NULL
);

-- Create ticket table
CREATE TABLE ticket
(
    id           RAW(16) NOT NULL PRIMARY KEY,
    category     NUMBER(1) NOT NULL CONSTRAINT ticket_category_check CHECK (category BETWEEN 0 AND 3),
    created_date TIMESTAMP(6),
    description  CLOB NOT NULL,
    priority     NUMBER(1) NOT NULL CONSTRAINT ticket_priority_check CHECK (priority BETWEEN 0 AND 2),
    status       NUMBER(1) NOT NULL CONSTRAINT ticket_status_check CHECK (status BETWEEN 0 AND 2),
    title        VARCHAR2(255) NOT NULL,
    updated_date TIMESTAMP(6),
    raised_by_id RAW(16) NOT NULL CONSTRAINT fk2pixkuq2h6h5gbyy9ks07oh3x REFERENCES app_user
);

-- Create comment table
CREATE TABLE comment
(
    id           RAW(16) NOT NULL PRIMARY KEY,
    created_date TIMESTAMP(6),
    message      VARCHAR2(255) NOT NULL,
    updated_date TIMESTAMP(6),
    commenter_id RAW(16) NOT NULL CONSTRAINT fkkdfvgyjqmtfiqs74t43ck5omq REFERENCES app_user,
    ticket_id    RAW(16) NOT NULL CONSTRAINT fksyf8wt2qb7rhcau6v3p4axrba REFERENCES ticket
);

-- Create ticket_comments table
CREATE TABLE ticket_comments
(
    ticket_id   RAW(16) NOT NULL CONSTRAINT fk7o9jd07fbf5xo43itq2pvyd03 REFERENCES ticket,
    comments_id RAW(16) NOT NULL CONSTRAINT uk79q8x9kc14ueyhl8ceivb0ipk UNIQUE CONSTRAINT fk5hbr87lnfvn7mwolw24vq522c REFERENCES comment
);

-- Create user_comments table
CREATE TABLE user_comments
(
    user_id     RAW(16) NOT NULL,
    comments_id RAW(16) NOT NULL CONSTRAINT ukm7map903hqfosjlvugs90odp4 UNIQUE CONSTRAINT fknccrlyd851u04nqll2fo9lyvj REFERENCES comment
);

-- Create app_user_comments table
CREATE TABLE app_user_comments
(
    app_user_id RAW(16) NOT NULL CONSTRAINT fk95p6gi8wwj69vkwi2c3l6exi0 REFERENCES app_user,
    comments_id RAW(16) NOT NULL CONSTRAINT ukn4sk01el3ktkdjhdttwdiua8x UNIQUE CONSTRAINT fk8r900gewkbqsxgk7ljk69lq40 REFERENCES comment
);