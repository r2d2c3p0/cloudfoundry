#!/usr/bin/ksh

for organization in `cf orgs | egrep -v "Getting|name|OK" | sed '/^$/d' | awk '{print $1}'`; do
        cf t -o ${organization} >/dev/null 2>&1
        echo;echo "==========================================================================================================="
        echo "Org: ${organization}"
        for space in `cf spaces | egrep -v "Getting|name|OK" | sed '/^$/d' | awk '{print $1}'`; do
                cf t -s ${space} >/dev/null 2>&1
                echo "  Space: ${space}"
                for service in `cf services | egrep -v "Getting|OK|name" | sed '/^$/d' | awk '{print $1}'`; do
                        line1=`cf service ${service} | sed '/^$/d' | head -1`
                        line2=`cf service ${service} | sed '/^$/d' | head -2 | tail -1`
                        line3=`cf service ${service} | sed '/^$/d' | head -3 | tail -1`
                        echo "          ${line1}"
                        echo "                  ${line2}"
                        echo "                  ${line3}";echo
                done                
        done
        echo "===========================================================================================================";echo
done
