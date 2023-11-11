
import json
from decimal import Decimal
linePerFile = 10000
fileName = "MoNA-export-In-Silico_Spectra"
# fileName = "MoNA-export-LC-MS-MS_Spectra 2"
def decimal_default(obj):
    if isinstance(obj, Decimal):
        return float(obj)
    raise TypeError(f'Object of type {obj.__class__.__name__} is not JSON serializable')


import ijson

resultsArr = []

with open(fileName + ".json", 'r', encoding="utf-8") as f:
    print("test")
    stream = ijson.parse(f)
    object = ijson.items(f, 'item')
    items = (item for item in object)
    resultsArr = []
    currenLen = 0
    fileIndex=0
    for i in items:
        resultsArr.append(i)
        currenLen += 1
        if (resultsArr.__len__() == linePerFile):
            json.dump(resultsArr, open("./splitFile/{}_{}.json".format(fileName,fileIndex), "w", encoding="utf-8"), ensure_ascii=False, default=decimal_default )
            resultsArr = []
            fileIndex += 1

    # items:
    #     print(i)
    #     resultsArr.append(i)
        

 