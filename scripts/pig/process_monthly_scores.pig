--%declare MONTH '2013/09/';
--%declare LASTMONTH '2013/08/';

REGISTER $INPUT/lib/piggybank.jar;

DEFINE CustomFormatToISO org.apache.pig.piggybank.evaluation.datetime.convert.CustomFormatToISO();
DEFINE ISOToUnix org.apache.pig.piggybank.evaluation.datetime.convert.ISOToUnix();
DEFINE UnixToISO org.apache.pig.piggybank.evaluation.datetime.convert.UnixToISO();

-- Get forecasts
forecasts1 = LOAD '$INPUT/forecasts/*/$MONTH' using PigStorage('\t') as (date, daysbefore:int, source, latitude, longitude, place, country, variable, value:float, units);

forecasts0 = LOAD '$INPUT/forecasts/*/$LASTMONTH' using PigStorage('\t') as (date, daysbefore:int, source, latitude, longitude, place, country, variable, value:float, units);

-- A strange error caused by mixing unions of unions makes necessary to duplicate code

forecasts0 = FOREACH forecasts0 GENERATE date..country, variable, ((units == '%')?value/100:value) as value:float, ((units == '%')?'prob.':units) as units;

forecasts0 = FOREACH forecasts0 GENERATE date..country, variable, ((units == 'mm (1h)')?value*24:value) as value:float, ((units == 'mm (1h)')?'mm (24h)':units) as units;

forecasts0 = FOREACH forecasts0 GENERATE date..country, variable, ((units == 'mm (3h)')?value*8:value) as value:float, ((units == 'mm (3h)')?'mm (24h)':units) as units;

forecasts1 = FOREACH forecasts1 GENERATE date..country, variable, ((units == '%')?value/100:value) as value:float, ((units == '%')?'prob.':units) as units;

forecasts1 = FOREACH forecasts1 GENERATE date..country, variable, ((units == 'mm (1h)')?value*24:value) as value:float, ((units == 'mm (1h)')?'mm (24h)':units) as units;

forecasts1 = FOREACH forecasts1 GENERATE date..country, variable, ((units == 'mm (3h)')?value*8:value) as value:float, ((units == 'mm (3h)')?'mm (24h)':units) as units;


-- Get observations
observations0 = LOAD '$INPUT/observations/*/$LASTMONTH' using PigStorage('\t') as (date, source, latitude, longitude, place, country, variable, value:float, units);
observations1 = LOAD '$INPUT/observations/*/$MONTH' using PigStorage('\t') as (date, source, latitude, longitude, place, country, variable, value:float, units);

-- A strange error caused by mixing unions of unions makes necessary to duplicate code

observations0 = FOREACH observations0 GENERATE date..country, ((variable == 'rain')?'pop':variable) as variable, ((units == '%')?value/100:value) as value, ((units == '%')?'prob.':units) as units;

observations1 = FOREACH observations1 GENERATE date..country, ((variable == 'rain')?'pop':variable) as variable, ((units == '%')?value/100:value) as value, ((units == '%')?'prob.':units) as units;

-- Generate baseline forecasts
baseline01 = FOREACH observations0 GENERATE REPLACE(REGEX_EXTRACT(UnixToISO(ISOToUnix(CustomFormatToISO(date, 'yyyy/MM/dd')) +1*24*3600*1000), '([0-9]{4}-[0-9]{2}-[0-9]{2}).+', 1),'-','/') as date:chararray, 1 as daysbefore:int, 'baseline' as source, latitude..units;
baseline02 = FOREACH observations0 GENERATE REPLACE(REGEX_EXTRACT(UnixToISO(ISOToUnix(CustomFormatToISO(date, 'yyyy/MM/dd')) +2*24*3600*1000), '([0-9]{4}-[0-9]{2}-[0-9]{2}).+', 1),'-','/') as date:chararray, 2 as daysbefore:int, 'baseline' as source, latitude..units;
baseline03 = FOREACH observations0 GENERATE REPLACE(REGEX_EXTRACT(UnixToISO(ISOToUnix(CustomFormatToISO(date, 'yyyy/MM/dd')) +3*24*3600*1000), '([0-9]{4}-[0-9]{2}-[0-9]{2}).+', 1),'-','/') as date:chararray, 3 as daysbefore:int, 'baseline' as source, latitude..units;
baseline04 = FOREACH observations0 GENERATE REPLACE(REGEX_EXTRACT(UnixToISO(ISOToUnix(CustomFormatToISO(date, 'yyyy/MM/dd')) +4*24*3600*1000), '([0-9]{4}-[0-9]{2}-[0-9]{2}).+', 1),'-','/') as date:chararray, 4 as daysbefore:int, 'baseline' as source, latitude..units;
baseline05 = FOREACH observations0 GENERATE REPLACE(REGEX_EXTRACT(UnixToISO(ISOToUnix(CustomFormatToISO(date, 'yyyy/MM/dd')) +5*24*3600*1000), '([0-9]{4}-[0-9]{2}-[0-9]{2}).+', 1),'-','/') as date:chararray, 5 as daysbefore:int, 'baseline' as source, latitude..units;

baseline1 = FOREACH observations1 GENERATE REPLACE(REGEX_EXTRACT(UnixToISO(ISOToUnix(CustomFormatToISO(date, 'yyyy/MM/dd')) +1*24*3600*1000), '([0-9]{4}-[0-9]{2}-[0-9]{2}).+', 1),'-','/') as date:chararray, 1 as daysbefore:int, 'baseline' as source, latitude..units;
baseline2 = FOREACH observations1 GENERATE REPLACE(REGEX_EXTRACT(UnixToISO(ISOToUnix(CustomFormatToISO(date, 'yyyy/MM/dd')) +2*24*3600*1000), '([0-9]{4}-[0-9]{2}-[0-9]{2}).+', 1),'-','/') as date:chararray, 2 as daysbefore:int, 'baseline' as source, latitude..units;
baseline3 = FOREACH observations1 GENERATE REPLACE(REGEX_EXTRACT(UnixToISO(ISOToUnix(CustomFormatToISO(date, 'yyyy/MM/dd')) +3*24*3600*1000), '([0-9]{4}-[0-9]{2}-[0-9]{2}).+', 1),'-','/') as date:chararray, 3 as daysbefore:int, 'baseline' as source, latitude..units;
baseline4 = FOREACH observations1 GENERATE REPLACE(REGEX_EXTRACT(UnixToISO(ISOToUnix(CustomFormatToISO(date, 'yyyy/MM/dd')) +4*24*3600*1000), '([0-9]{4}-[0-9]{2}-[0-9]{2}).+', 1),'-','/') as date:chararray, 4 as daysbefore:int, 'baseline' as source, latitude..units;
baseline5 = FOREACH observations1 GENERATE REPLACE(REGEX_EXTRACT(UnixToISO(ISOToUnix(CustomFormatToISO(date, 'yyyy/MM/dd')) +5*24*3600*1000), '([0-9]{4}-[0-9]{2}-[0-9]{2}).+', 1),'-','/') as date:chararray, 5 as daysbefore:int, 'baseline' as source, latitude..units;
baseline0 = UNION baseline01, baseline02, baseline03, baseline04, baseline05;
baseline1 = UNION baseline1, baseline2, baseline3, baseline4, baseline5;
forecasts2 = UNION forecasts0, forecasts1, baseline0, baseline1;

-- Generate MONTHly score
data0 = JOIN forecasts2 by (date, place, country, variable), observations1 by (date, place, country, variable);

data1 = FOREACH data0 GENERATE forecasts2::date as date, forecasts2::daysbefore..forecasts2::units, observations1::value, observations1::units as units_f, (observations1::value-forecasts2::value)*(observations1::value-forecasts2::value) as score;

-- Aggregation of mintemp and maxtemp in one score
data1 = FOREACH data1 GENERATE date..country,  (((variable == 'mintemp') or (variable == 'maxtemp'))?'temp':variable) as variable, forecasts2::value..score;

data2 = GROUP data1 by (source, variable, daysbefore);

data3 = FOREACH data2 GENERATE group.$0 as source, group.$1 as variable, group.$2 as daysbefore, AVG($1.score) as mse, COUNT($1.score) as count, MAX($1.date) as lastdate, MAX($1.units) as units, MAX($1.units_f) as units_f, SUM($1.score) as suma, COUNT($1.place) as places;

-- store MONTHly score
rmf $INPUT/scores/$MONTH;
STORE data3 INTO '$INPUT/scores/$MONTH';

-- update total score (warning: avg in totalscores is sqrt, mse in scores is not)
scores0 = LOAD '$INPUT/totalscores/$LASTMONTH' using PigStorage('\t') as (source, variable, daysbefore, rmse, count, lastdate);

scores1 = JOIN data3 by (source, variable, daysbefore), scores0 by (source, variable, daysbefore);
scores2 = FOREACH scores1 GENERATE data3::source..data3::daysbefore,SQRT(((data3::mse*data3::count)+(scores0::rmse*scores0::rmse*scores0::count))/(data3::count+scores0::count)) as rmse, (data3::count+scores0::count) as count, data3::lastdate as lastdate;
rmf $INPUT/totalscores/$MONTH;

STORE scores2 INTO '$INPUT/totalscores/$MONTH';
