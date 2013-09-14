#!/bin/bash
cat <(crontab -l) <(echo "01 12 * * * wget http://www.meteotester.com/observations?place=Madrid >> meteotester_cron.log 2>&1
02 12 * * * wget http://www.meteotester.com/forecasts?place=Madrid >> meteotester_cron.log 2>&1
03 12 * * * wget 'http://www.meteotester.com/observations?place=Atlantic City' >> meteotester_cron.log 2>&1
04 12 * * * wget 'http://www.meteotester.com/forecasts?place=Atlantic City' >> meteotester_cron.log 2>&1
05 12 * * * wget http://www.meteotester.com/observations?place=Macau >> meteotester_cron.log 2>&1
06 12 * * * wget http://www.meteotester.com/forecasts?place=Macau >> meteotester_cron.log 2>&1") | crontab -

