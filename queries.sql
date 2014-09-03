select pm.account_id, p.persona_name, count(*)
  from players p, players_in_matches pm  
  where p.account_id = pm.account_id  
  group by pm.account_id  
  order by count(*) desc;    

select h.localized_name, count(*)
from heroes h, players_in_matches pm
where h.id = pm.hero_id
group by pm.hero_id
order by count(*) desc;


select count(*) from players_in_matches;

select count(*) from players;

select count(*) from matches;


select id, count(*)
from matches
group by id
order by count(*) desc;

select account_id, count(*)
from players
group by account_id
order by count(*) desc;

select match_id, count(*)
from players_in_matches
group by match_id
order by count(*) desc;

select  h.localized_name, count(*)
from players_in_matches pm, players p, heroes h
where 
      pm.account_id = p.ACCOUNT_ID 
      and h.id = pm.hero_id      
      and p.ACCOUNT_ID = 113696708 
group by pm.account_id, h.localized_name
order by count(*) desc;

select he.[localized_name] 
from heroes he
where he.id not in (
      select  h.id
      from players_in_matches pm, heroes h
      where 
           h.id = pm.hero_id      
           and pm.ACCOUNT_ID = 113696708 
);


select * from test where test.[testInt] is null;

--+++delte extra anonymous records
-- select for check
select pm11.[id], pm11.[account_id] 
from players_in_matches pm11,
(select pm.[hero_id], pm.[player_slot], pm.[match_id]
   from    players_in_matches pm
   group by pm.[hero_id], pm.[player_slot], pm.[match_id]
   having count(*) > 1)pm22
where 
  pm11.[hero_id] = pm22.[hero_id] and  
  pm11.[player_slot] = pm22.[player_slot] and
  pm11.[match_id] = pm22.[match_id] and
  pm11.[account_id] = 2147483647;  
        
-- delete
delete from players_in_matches where id in (
select pm11.[id]
from players_in_matches pm11,
(select pm.[hero_id], pm.[player_slot], pm.[match_id]
   from    players_in_matches pm
   group by pm.[hero_id], pm.[player_slot], pm.[match_id]
   having count(*) > 1)pm22
where 
  pm11.[hero_id] = pm22.[hero_id] and  
  pm11.[player_slot] = pm22.[player_slot] and
  pm11.[match_id] = pm22.[match_id] and
  pm11.[account_id] = 2147483647);   

--^^^delte extra anonymous records


