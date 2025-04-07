delete from authority auth
where auth.user_id in (
    select u.id
    from public.user u
    where u.username not in ('dima', 'bee', 'duck')
);

delete from public."user" u
where username not in ('dima', 'bee', 'duck');