
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



