

import json
import base64
import sys
import io

file_directory = sys.argv[1]
json_data = open(file_directory, 'r', encoding='utf-8-sig')
lines = ""
for l in json_data.readlines():
    lines += " " + l

lines = lines.replace("ans", "\"ans\"").replace("id", "\"id\"").replace(", author: null", "")
lines = "[\n" + lines + "\n]"

data = json.load(io.StringIO(lines))
myList = []

for i in data:
    javab = (str(base64.b64encode(bytes(i["ans"], "utf-8")))).replace('\'', "")[1:]

    myMap = {"javab": javab, "id": i["id"], "resolved": False, "resources": str.format("level_{}.jpg", i["id"]),
             "thumbnail": str.format("level_{}.jpg", i["id"]), "type": "image"}

    myList.append(myMap)

myMap = {"levels": myList}

path = sys.argv[1].split("/")[:-1]

outPath = ""
for l in path:
    outPath += l +"/"

outPath += "level_list.json"

out = open(outPath, "w")
json.dump(myMap, out)

