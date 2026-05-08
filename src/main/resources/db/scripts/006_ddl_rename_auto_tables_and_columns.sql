ALTER TABLE auto_user RENAME TO users;

ALTER TABLE auto_post RENAME TO posts;
ALTER TABLE posts RENAME COLUMN auto_user_id TO user_id;

ALTER TABLE price_history RENAME COLUMN auto_post_id TO post_id;