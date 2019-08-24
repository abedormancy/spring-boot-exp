select
===
select * from user where 1 = 1
@if (!isEmpty(age)) {
and age = #age#
@}
@if (!isEmpty(name)) {
and name = #name#
@}

sample
===
* 注释

	select #use("cols")# from user  where  #use("condition")#

cols
===
	id,name,age,create_date

updateSample
===
	
	id=#id#,name=#name#,age=#age#,create_date=#createDate#

condition
===

	1 = 1  
	@if(!isEmpty(id)){
	 and id=#id#
	@}
	@if(!isEmpty(name)){
	 and name=#name#
	@}
	@if(!isEmpty(age)){
	 and age=#age#
	@}
	@if(!isEmpty(createDate)){
	 and create_date=#createDate#
	@}