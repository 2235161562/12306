create or replace function validTrain_check()
    returns trigger
as
$$
declare
    create_data date;
begin
    select new.create_data
    into create_data;

    return new;
end;
$$ language plpgsql;

create trigger train_trigger
    before insert
    on train
    for each row
execute procedure validTrain_check();



create or replace function validDock_check()
    returns trigger
as
$$
declare
    train  varchar(6);
    id     integer;
    arrive varchar(6);
    leave  varchar(6);
    num    int;
begin
    train := new.train_name;
    id := new.dock_id;
    arrive := new.arrive_time;
    leave := new.leave_time;

    if arrive <> 'start' and ((cast(substr(arrive, 1, 2) as int)) < 0 or (cast(substr(arrive, 1, 2) as int)) > 23
        or (cast(substr(arrive, 4, 2) as int)) < 0 or (cast(substr(arrive, 4, 2) as int)) > 60) then
        raise exception 'Valid Time Input';
    end if;

    if leave <> 'end' and ((cast(substr(leave, 1, 2) as int)) < 0 or (cast(substr(leave, 1, 2) as int)) > 23
        or (cast(substr(leave, 4, 2) as int)) < 0 or (cast(substr(leave, 4, 2) as int)) > 60) then
        raise exception 'Valid Time Input';
    end if;

    select count(*)
    from (
             select dock_id
             from dock
             where train_name = train
               and dock_id >= id
         ) sub
    into num;
    if num = 0 then
        if id <> 1 then
            select max(dock_id) + 1
            from dock
            where train_name = train
            into id;

            update dock
            set leave_time = leave
            where train_name = train
              and dock_id = id - 1;
        end if;
        leave = 'end';
    end if;

    if id = 1 then
        arrive = 'start';
    end if;

    select id
    into new.dock_id;
    select arrive
    into new.arrive_time;
    select leave
    into new.leave_time;

    return new;
end;
$$ language plpgsql;

create trigger dock_trigger
    before insert
    on dock
    for each row
execute procedure validDock_check();



create or replace function create_seat() returns trigger
    language plpgsql
as
$$
declare
    seat   varchar(4);
    f      int;
    t      int;
    train  varchar(6);
    num    int;
    maxNum int;
begin
    seat := new.seat_id;
    f := new.from_dock;
    t := new.to_dock;
    train := new.train_name;

    if substr(seat, 1, 1) = 'F' then
        select t.first_seat_count from train t where t.train_name = train into maxNum;
        num = cast(substr(seat, 2, length(seat) - 1) as int);
    elseif substr(seat, 1, 1) = 'S' then
        select t.second_seat_count from train t where t.train_name = train into maxNum;
        num = cast(substr(seat, 2, length(seat) - 1) as int);
    else
        select t.third_seat_count from train t where t.train_name = train into maxNum;
        num = cast(seat as int);
    end if;

    if num > maxNum then
        raise exception 'No This Seat Number';
    end if;

    for i in f .. t
        loop
            for j in (i + 1) .. t
                loop
                    if i <> j and
                       (select count(*)
                        from ticket
                        where train_name = train
                          and from_dock = i
                          and to_dock = j
                          and seat_id = seat
                          and ticket_status <> 'td') <> 0 then
                        raise exception 'This Seat Has Ordered';
                    end if;
                end loop;
        end loop;

    return new;
end;
$$;

create trigger create_seat
    before insert
    on ticket
    for each row
execute procedure create_seat();