# python - v2 or more
#
#
# version       : 1.0.0 (initial version ${1.0.0})
# description   : python v2 or more and cloud foundry PaaS. Note: It will only print first 100 service bindings only.
# modifications : 1.0.0 (5/29/2018) initial version.
#
# author        : r2d2c3p0.
# dob           : 5/29/2018
# tool name     : serviceBindings.py
#
#

# global imports.
from __future__ import division
from time import strftime
import os, re, sys, os.path, json

# main code.
if __name__ == "__main__":
	""" Cannot invoke import."""
	print
	organizations = os.popen("cf orgs | awk '{print $1}' | egrep -v 'name|OK|Getting|^$'").read()
	if organizations.find("FAILED") == -1:
		pass
	else:
		print "ERROR| No active session found, please login ...";print
		sys.exit(1)
	#endIfElse
	for organization in organizations.splitlines():
		os.system("cf t -o %s >/dev/null 2>&1" %(organization))
		spaces  = os.popen("cf spaces | awk '{print $1}' | egrep -v 'name|OK|Getting|^$'").read()
		for space in spaces.splitlines():
			os.system("cf t -s %s >/dev/null 2>&1" %(space))
			microservices = os.popen("cf apps | awk '{print $1}' | egrep -v 'name|OK|Getting|^$'").read()
			for microservice in microservices.splitlines():
				if not re.match(r'^\s*$', microservice):
					if not re.match("\\bNo\\b", microservice):
						try:
							guidCommand="cf curl /v2/apps/$(cf app %s --guid)" %(microservice)
							guidData = os.popen(guidCommand).read();guidObject = json.loads(guidData)
							appName=guidObject["entity"]["name"];service_bindings=guidObject["entity"]["service_bindings_url"]
							service_bindings_curl="cf curl %s" %(service_bindings)
							sbData=os.popen(service_bindings_curl).read();sbObject = json.loads(sbData)
							total_services=sbObject["total_results"]
							print " %s [%s][%s]" %(appName, space, organization)
							print "   Total services: %s" %(total_services)
							for i in range(0, 100):
								try:
									serviceInstanceUrl=sbObject["resources"][i]["entity"]["service_instance_url"]
									instanceCommand="cf curl %s" %(serviceInstanceUrl)
									instanceData=os.popen(instanceCommand).read();instanceObject=json.loads(instanceData)
									serviceName=instanceObject["entity"]["name"]
									print "   service[%s]: %s" %(str(i+1), serviceName)
								except:
									break
								#endTryExcept
							#endFor
						except:
							print " %s [%s][%s]" %("None", space, organization)
						#endTryExcept
					else:
						print " %s [%s][%s]" %("None", space, organization)
					#endIfElse
				#endIf
			#endFor
		#endFor
	#endFor
	print
else:
	print "\tERROR| import failed."
	sys.exit(0)
#endIfElse

#serviceBindings.py
