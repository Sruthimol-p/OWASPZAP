#!/bin/bash
# Script for testing the packaged scans when using the Automation Framework

RES=0

mkdir -p /zap/wrk/output

echo "TEST: Baseline test 1 (vs example.com)"
/zap/zap-baseline.py -t https://www.example.com/ > /zap/wrk/output/baseline1.out
RET=$?
DIFF=$(diff /zap/wrk/output/baseline1.out /zap/wrk/results/baseline1.out) 
if [ "$DIFF" != "" ] 
then
	# For some reason this test can give slightly different results
	DIFF=$(diff /zap/wrk/output/baseline1.out /zap/wrk/results/baseline1b.out) 
fi
if [ "$DIFF" != "" ] 
then
    echo "ERROR: differences:"
    echo "$DIFF"
	RES=1
else
	if [ "$RET" -ne 2 ] 
	then
    	echo "ERROR: exited with $RET instead of 2"
		RES=1
	else
    	echo "PASS"
    fi
fi
# Dont carry over any configs
rm ~/.ZAP_D/config.xml

echo
echo "Baseline test 2 (vs example.com with INFO/WARN/FAIL set)"
/zap/zap-baseline.py -t https://www.example.com/ --auto -c configs/baseline2.conf > /zap/wrk/output/baseline2.out
RET=$?
DIFF=$(diff /zap/wrk/output/baseline2.out /zap/wrk/results/baseline2.out) 
if [ "$DIFF" != "" ] 
then
	# For some reason this test can give slightly different results
	DIFF=$(diff /zap/wrk/output/baseline2.out /zap/wrk/results/baseline2b.out) 
fi
if [ "$DIFF" != "" ] 
then
    echo "ERROR: differences:"
    echo "$DIFF"
	RES=1
else
	if [ "$RET" -ne 1 ] 
	then
    	echo "ERROR: exited with $RET instead of 1"
		RES=1
	else
    	echo "PASS"
    fi
fi
# Dont carry over any configs
rm ~/.ZAP_D/config.xml

echo
echo "Baseline test 3 (vs example.com with INFO/WARN/FAIL and OUTOFSCOPE set via file)"
/zap/zap-baseline.py -t https://www.example.com/ -c configs/baseline3.conf > /zap/wrk/output/baseline3.out
RET=$?
# In this case use the OUTOFSCOPE to ignore the rule that can get differing results ;)
DIFF=$(diff /zap/wrk/output/baseline3.out /zap/wrk/results/baseline3.out) 
if [ "$DIFF" != "" ] 
then
    echo "ERROR: differences:"
    echo "$DIFF"
	RES=1
else
	if [ "$RET" -ne 1 ] 
	then
    	echo "ERROR: exited with $RET instead of 1"
		RES=1
	else
    	echo "PASS"
    fi
fi
# Dont carry over any configs
rm ~/.ZAP_D/config.xml

echo
echo "Baseline test 4 (vs example.com with INFO/WARN/FAIL and OUTOFSCOPE set via URL)"
/zap/zap-baseline.py -t https://www.example.com/ -u https://raw.githubusercontent.com/zaproxy/zaproxy/main/docker/integration_tests/configs/baseline3.conf > /zap/wrk/output/baseline4.out
RET=$?
# In this case use the OUTOFSCOPE to ignore the rule that can get differing results ;)
DIFF=$(diff /zap/wrk/output/baseline4.out /zap/wrk/results/baseline4.out) 
if [ "$DIFF" != "" ] 
then
    echo "ERROR: differences:"
    echo "$DIFF"
	RES=1
else
	if [ "$RET" -ne 1 ] 
	then
    	echo "ERROR: exited with $RET instead of 1"
		RES=1
	else
    	echo "PASS"
    fi
fi
# Dont carry over any configs
rm ~/.ZAP_D/config.xml

echo
echo "TEST Baseline 5 (new vs old) - we expect _some_ differences and this will not fail the whole script"
/zap/zap-baseline.py -t https://www.example.com/ --autooff > /zap/wrk/output/baseline1-orig.out
DIFF=$(diff /zap/wrk/output/baseline1.out /zap/wrk/output/baseline1-orig.out) 
echo "Differences:"
echo "$DIFF"
# Dont carry over any configs
rm ~/.ZAP_D/config.xml

echo "End result: $RES"
exit $RES
