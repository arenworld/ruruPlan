commit;

select * from ruru_member;
select * from ruru_gpt_cmd;
select * from ruru_task; -- 택시/ 도보/ 대중교통/ 식사 / 관광/ 숙소
select * from ruru_place_info;
select * from ruru_plan;

insert into ruru_plan(member_id, plan_name, start_date, end_date, plan_create_date, plan_update_date, theme1, theme2, theme3)
values('yoojy1', '테스트', '2024-09-10', '2024-09-12', '2024-09-09', '2024-09-09', '힐링', '쇼핑', '휴양');

insert into ruru_task(plan_num, place_id, member_id, date_n, start_time, duration, task, cost)
values(2, '1019041', 'yoojy1', 1, '12:00', '2:30', '관광', 3000);