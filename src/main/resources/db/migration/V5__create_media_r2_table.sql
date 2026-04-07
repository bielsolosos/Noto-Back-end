CREATE TABLE public.media_r2
(
    id           uuid                                NOT NULL,
    filename     varchar(255)                        NOT NULL,
    url          varchar(500)                        NOT NULL,
    content_type varchar(50),
    size_bytes   int8,
    user_id      uuid                                NOT NULL,
    created_at   timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT media_pkey PRIMARY KEY (id),
    CONSTRAINT fk_media_user FOREIGN KEY (user_id) REFERENCES public.users (id) ON DELETE CASCADE
);

CREATE INDEX idx_media_user_id ON public.media_r2 USING btree (user_id);