ALTER TABLE users
    ADD COLUMN profile_media_id UUID;

ALTER TABLE users
    ADD CONSTRAINT fk_users_profile_media
        FOREIGN KEY (profile_media_id) REFERENCES media_r2 (id)
        ON DELETE SET NULL;

CREATE UNIQUE INDEX ux_users_profile_media_id ON users(profile_media_id);

