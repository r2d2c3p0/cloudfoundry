# python - v2 or less
#
#
# version       : 1.0.0 (initial version ${1.0.0})
# description   : .
# modifications : 1.0.0 (5/18/2018) inital version.
#
#
# author        : Shane Reddy.
# dob           : 5/18/2018
# tool name     : foundationMetrics.py
#
#[sr87813@NACGTDPCFJ02U ~]$ python g.py > g.out
#

# imports.
from __future__ import division
from multiprocessing import Process, current_process
from time import strftime
import os, re, sys, os.path, json

# Global Variables
foundationDiskAllocated=0
foundationDiskUsage=0
foundationMemoryAllocated=0
foundationMemoryUsage=0

# main code.
if __name__ == "__main__":
	""" Cannot invoke import."""
	print
	organizations = os.popen("cf orgs | awk '{print $1}' | egrep -v 'name|OK|Getting|^$'").read()
	for organization in organizations.splitlines():
		orgDiskAllocated=0
		orgDiskUsage=0
		orgMemoryAllocated=0
		orgMemoryUsage=0
		spaces  = os.popen("cf spaces | awk '{print $1}' | egrep -v 'name|OK|Getting|^$'").read()
		for space in spaces.splitlines():
			spaceDiskAllocated=0
			spaceDiskUsage=0
			spaceMemoryAllocated=0
			spaceMemoryUsage=0
			print "Targetting to %s/%s (org/space)..." %(organization, space)
			os.system("cf t -o %s -s %s" %(organization, space))
			microservices = os.popen("cf apps | awk '{print $1}' | egrep -v 'name|OK|Getting|^$'").read()
			for microservice in microservices.splitlines():
				if not re.match(r'^\s*$', microservice):
					if not 'No ' in microservice:
						guidCommand="cf curl /v2/apps/$(cf app %s --guid)" %(microservice)
						guidData = os.popen(guidCommand).read()
						guidObject = json.loads(guidData)
						appName=guidObject["entity"]["name"]
						state=guidObject["entity"]["state"]
						memoryAllocated=guidObject["entity"]["memory"]
						diskAllocated=guidObject["entity"]["disk_quota"]
						instances=guidObject["entity"]["instances"]
						print "\tApplication: %s" %(appName)
						print "\t\tstate: %s" %(state)
						print "\t\tinstances: %s" %(instances)
						print "\t\tmemory[alloc]: %s MB per instance" %(memoryAllocated)
						print "\t\tdisk[alloc]: %s MB per instance" %(diskAllocated)
						spaceMemoryAllocated=spaceMemoryAllocated+(memoryAllocated*instances)
						spaceDiskAllocated=spaceDiskAllocated+(diskAllocated*instances)
						if state == "STARTED":
							statsCommand="cf curl /v2/apps/$(cf app %s --guid)/stats" %(microservice)
							appData = os.popen(statsCommand).read()
							applicationObject = json.loads(appData)
							current_memory_usage=0
							current_disk_usage=0
							for i in range(0, 100):
									try:
										instance_mem_usage=applicationObject[str(i)]["stats"]["usage"]["mem"]
										instance_disk_usage=applicationObject[str(i)]["stats"]["usage"]["disk"]
										current_memory_usage=current_memory_usage+instance_mem_usage
										current_disk_usage=current_disk_usage+instance_disk_usage
										print "\t\tinstance "+str(i)+" usage:\n\t\t\tmemory: "+str((instance_mem_usage/1024)/1024)+" MB\n\t\t\tdisk: "+str((instance_disk_usage/1024)/1024)+ " MB"
									except:
										break
									#endTryExcept
							#endFor
							print "\t\ttotal application usage:\n\t\t\tmemory: "+str(((current_memory_usage/1024)/1024))+" MB\n\t\t\tdisk: "+str(((current_disk_usage/1024)/1024))+" MB"
						#endIf
					else:
						#print "\tApplication: %s is not valid." %(microservice)
						print "\t No applications are defined for this space."
				#endIf
			#endFor
			print "\tTotal Space (%s) disk allocated: %s GB" %(space, (spaceDiskAllocated/1024))
			print "\tTotal Space (%s) memory allocated: %s GB" %(space, (spaceMemoryAllocated/1024))
			orgDiskAllocated=orgDiskAllocated+spaceDiskAllocated
			orgMemoryAllocated=orgMemoryAllocated+spaceMemoryAllocated
		#endFor
		print "-------"
		print "\tTotal Org (%s) disk allocated: %s GB" %(organization, (orgDiskAllocated/1024))
		print "\tTotal Org (%s) memory allocated: %s GB" %(organization, (orgMemoryAllocated/1024))
		exit()
	#endFor
	#fileData = os.popen("cf curl /v2/apps/76136a7b-d1c7-4d90-95c7-5bbcbb6f402a/stats").read()
	print
else:
	print "\tERROR| import failed."
	exit()
#endIfElse

#foundationMetrics.py
