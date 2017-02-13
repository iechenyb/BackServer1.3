
alter table stock drop column industry;  
alter table stock drop column classify;  
alter table stock add(industry varchar2(5),classify varchar2(5),timeOfMarket long,province varchar2(10));
alter table minutequtoescurrday add(time_min long);
alter table minutequtoescurrday add(volume number,turnvolume number);
alter table stock add(area varchar(50));
alter table realtimequtoes add(volume number,turnvolume number);
alter table realtimequtoes add(zdbz number,industry varchar2(10));
alter table minutequtoescurrday add(zdbz number);

CREATE TABLE stock(id_ varchar(50) PRIMARY KEY,   code_ VARCHAR(20),exchange_ varchar(20),name_ varchar(100),oper_time timestamp);
create table realtimequtoes(id_ varchar(50),record_date_ long,name_ varchar(50),code_ VARCHAR(20),open_ double,close_ double,price_ double,preclose_ double,high_ double,low_ double,day_ varchar(14),time_ varchar(20),oper_time timestamp);
create table minutequtoes(id_ varchar(50),record_date_ long,name_ varchar(50),code_ VARCHAR(20),open_ double,close_ double,price_ double,preclose_ double,high_ double,low_ double,day_ varchar(14),time_ varchar(20),oper_time timestamp);
create table closequtoes(id_ varchar(50),record_date_ long,name_ varchar(50),code_ VARCHAR(20),open_ double,close_ double,price_ double,preclose_ double,high_ double,low_ double,day_ varchar(14),time_ varchar(20),oper_time timestamp);
create table minutequtoescurrday(id_ varchar(50),record_date_ long,name_ varchar(50),code_ VARCHAR(20),open_ double,close_ double,price_ double,preclose_ double,high_ double,low_ double,day_ varchar(14),time_ varchar(20),oper_time timestamp);
CREATE INDEX INDEX_time_min ON minutequtoescurrday (time_min)
CREATE INDEX INDEX_date ON CLOSEQUTOES (record_date_);

insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345601','000001','sh','上证指数','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345602','000002','sh','Ａ股指数','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345603','000003','sh','Ｂ股指数','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345604','000004','sh','工业指数','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345605','000005','sh','商业指数','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345606','000006','sh','地产指数','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345607','000007','sh','公用指数','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345608','000008','sh','综合指数','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345609','000009','sh','上证380','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345610','000010','sh','上证180','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345611','000011','sh','基金指数','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345812','000012','sh','国债指数','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345613','000013','sh','企债指数','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345614','000015','sh','红利指数','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345615','000016','sh','上证50','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345616','000018','sh','180金融','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345617','000019','sh','治理指数','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345618','000032','sh','��֤��Դ','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345619','000033','sh','��֤����','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345620','000034','sh','��֤��ҵ','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345621','000037','sh','��֤ҽҩ','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345622','000038','sh','��֤����','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345623','000039','sh','��֤��Ϣ','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345701','399001','sz','��֤��ָ','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345702','399002','sz','��֤��ָR','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345703','399003','sz','�ɷ�B��','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345704','399004','sz','��֤100','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345705','399005','sz','��С��ָ','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345706','399006','sz','��ҵ��ָ','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345707','399007','sz','��֤300�۸�','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345708','399008','sz','��С��300P','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345709','399009','sz','��֤200','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345710','399010','sz','��֤700','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345711','399011','sz','��֤1000','zhzs',sysdate);
insert into stock(id_,code_,exchange_,name_,industry,oper_time) values ('12345712','399012','sz','��ҵ��300','zhzs',sysdate);
create table grailqutoes(id_ varchar(50),record_date_ long,name_ varchar(50),code_ VARCHAR(20),open_ double,close_ double,price_ double,high_ double,low_ double,oper_time timestamp);
create table grailindicatorclose(id_ varchar(50),record_date_ long,name_ varchar(50),code_ VARCHAR(20),high_ double,open_ double,low_ double,close_ double,oper_time timestamp);
create table  stockconfig1(alias varchar(100),curjyr long);
create table  stockconfig(alias varchar(100),val varchar(100));
create table holidayconfig(jjr long);
insert into holidayconfig values(20160207);
insert into stockconfig values ('LASTESTDAY',20160118);
insert into stockconfig values ('INITSTOCKCODE',0);//�Ƿ���Ҫ��ʼ����Ʊ���� 0 ��Ҫ 1 ����Ҫ
SELECT substr(oper_time,0,16)  as time_ ,price_,nvl(PRECLOSE_,0) as PRECLOSE_,HIGH_,LOW_ FROM MINUTEQUTOESCURRDAY where record_date_  in(select  curjyr from STOCKCONFIG where alias='LASTESTDAY' );
create index m_code_ on minutequtoescurrday(code_);
create index clse_code_index on closequtoes(code_);
create table idea(id_ varchar(50),phone varchar(11),email varchar(50),userType long ,ideaType long,fileId varchar(50),message varchar(2000),submittime long);
create table usr(id_ varchar(50),username varchar(50),password varchar(50),roleId long ,loginstatus long,registerTime long,status long,email varchar(50),phone long);
create table userconcern(id_ varchar2(50),code_ varchar2(20),userid varchar2(50),create_time timestamp);
update USR set password  = 'C4CA4238A0B923820DCC509A6F75849B' where username='13938469072'--�������1
--2016��2��16��17:19:51
update STOCK  set industry ='zhzs' where inDUSTRY  is null;
--2016��2��18��08:54:32
--�ǵ���־ 0 Ĭ��ֵ��1 ��ͣ 2 ��ͣ 3 ͣ�� 4 ���� 5 �µ�

update minutequtoescurrday set zdbz = 0;
update MINUTEQUTOESCURRDAY  set zdbz=1 where 100*(price_-preclose_)/preclose_>=9 ;
update MINUTEQUTOESCURRDAY  set zdbz=2 where 100*(price_-preclose_)/preclose_<=-9 ;
--2016��3��1��16:56:57
--5��ƽ��ֵ
create table bulinxian(id_ varchar(50),code_ varchar(11),record_date_ long,day5ave double ,day5bzc double,oper_time timestamp);
select xh  from (SELECT rownum xh,t.* FROM CLOSEQUTOES  t) a where a.xh<=10;
--2016年3月25日15:30:34
create table buy_log (reqstr varchar2(100),tradeNum int,handlerresult varchar2(100),time_ timestamp ,before_ int  ,after_ int);
create table ticket(id varchar2(60),num int);
insert into ticket (id,num)values ('1',0);
update STOCKCONFIG set curjyr='20160606' where alias ='LASTESTDAY';
delete from  MINUTEQUTOESCURRDAY  where record_date_='20160603'

--2016年6月28日13:51:40
update  STOCK set  name_ ='中国平安' where cLASSIFY ='fund' and code_='000001' and exchange_ ='sz';

SELECT count(code_),code_ FROM REALTIMEQUTOES  group by code_ having count(code_)>1
delete from REALTIMEQUTOES  where id_ in( SELECT max(id_) FROM REALTIMEQUTOES group by code_ having count(code_)>1)
SELECT COUNT(*) ,code_ FROM STOCK group by code_ having count(code_)>1
select * from stock where code_ in(SELECT code_ FROM STOCK group by code_ having count(code_)>1)