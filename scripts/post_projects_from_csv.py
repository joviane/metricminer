#!/usr/bin/python
# coding: utf-8

import csv, time
import httplib, urllib

reader = csv.reader(open("send.csv", "r"), delimiter=";", quotechar='"')

projects = []

for row in reader:
    projects.append({"nome": row[0], "url": row[1], "gitname": row[2]})
        
print projects

for project in projects:
    dirname = project["gitname"].split(".")[0]
    params = {"project.name": project["nome"], "project.scmUrl": project["url"], "project.scmRootDirectoryName":dirname}
    #print params
    query = urllib.urlencode(params)
    
    #url = "localhost:8080";
    url = "metricminer.org.br";
    
    headers = {"Content-type": "application/x-www-form-urlencoded","Accept": "text/plain"}
    
    conn = httplib.HTTPConnection(url)
    conn.request("POST", "/projects", query, headers)
    response = conn.getresponse()
    print response.status, response.reason
    
    time.sleep(2)