# python - v2 or more
#
#
# version       : 1.0.0 (initial version ${1.0.0})
# description   : python v2 or more and cloud foundry PaaS.
#                                 Note: It will only print first 100 service bindings only.
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
	serviceInstanceCommand="cf curl /v2/service_instances"
	serviceInstanceData = os.popen(serviceInstanceCommand).read();
	serviceInstanceObject = json.loads(serviceInstanceData)
	for i in range(0, 100):
		try:
			serviceInstanceName=serviceInstanceObject["resources"][i]["entity"]["name"]
			serviceBindingsUrl=serviceInstanceObject["resources"][i]["entity"]["service_bindings_url"]
			bindingsCommand="cf curl %s" %(serviceBindingsUrl)
			bindingsData=os.popen(bindingsCommand).read();
			bindingsObject=json.loads(bindingsData)
			print "   service [%s]: %s" %(str(i+1), serviceInstanceName)
			for j in range(0, 100):
				try:
					appGUID=bindingsObject["resources"][j]["entity"]["app_guid"]
					guidData = os.popen("cf curl /v2/apps/%s" %(appGUID)).read();guidObject = json.loads(guidData)
					appName=guidObject["entity"]["name"];
					print "    app[%s]: %s" %(str(j+1), appName)
				except:
					#print "    No applications are bound."
					break;
				#endTryExcept
			#endFor
		except:
				break;
		#endTryExcept
	#endFor

	userviceInstanceCommand="cf curl /v2/user_provided_service_instances"
	userviceInstanceData = os.popen(userviceInstanceCommand).read();
	userviceInstanceObject = json.loads(userviceInstanceData)
	for k in range(0, 100):
		try:
			userviceInstanceName=userviceInstanceObject["resources"][k]["entity"]["name"]
			userviceBindingsUrl=userviceInstanceObject["resources"][k]["entity"]["service_bindings_url"]
			userviceBindingSpace=userviceInstanceObject["resources"][k]["entity"]["space_url"]
			spaceData=os.popen("cf curl %s" %(userviceBindingSpace)).read();spaceObject=json.loads(spaceData)
			ubindingsCommand="cf curl %s" %(userviceBindingsUrl)
			ubindingsData=os.popen(ubindingsCommand).read();
			ubindingsObject=json.loads(ubindingsData)
			print "   user provided service [%s]: %s [space: %s]" %(str(k+1), userviceInstanceName, spaceObject["entity"]["name"])
			for l in range(0, 100):
				try:
					appGUID=ubindingsObject["resources"][l]["entity"]["app_guid"]
					appCommand="cf curl /v2/apps/%s" %(appGUID)
					guidData = os.popen(appCommand).read();
					guidObject = json.loads(guidData)
					appName=guidObject["entity"]["name"];
					print "    app[%s]: %s" %(str(l+1), appName)
				except:
					#print "    No applications are bound."
					break;
				#endTryExcept
			#endFor
		except:
			break;
		#endTryExcept
	#endFor
	print
else:
	print "\n\tERROR| import failed.\n";sys.exit(0)
#endIfElse

#serviceBindings.py
