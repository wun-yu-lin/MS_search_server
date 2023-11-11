import sys,os,time


def prepare_spectrum_data(monadata_each_object, compound_data_id, compound_classification_id) -> object:
    ms_level=None
    precursor_mz=None
    collision_energy=None
    mz_error=None
    data_source=None
    tool_type=None
    ion_mode=None
    instrument=None
    exact_mass=None
    precursor_type=None
    try:
        spectrum_data = None
        data_source = []
        for item in monadata_each_object["tags"]:
            data_source.append(item["text"])
        data_source = str(data_source)



        for item in monadata_each_object["metaData"]:
            item_name = item["name"]
            item_value = item["value"]

            ##ms level
            if item_name == "ms level":
                if item_value == "MS2": ms_level = 2
                if item_value == "MS1": ms_level = 1

            #inonization mode
            if item_name == 'ionization mode':
                if item_value =="positive": ion_mode = "positive"
                if item_value =="negative": ion_mode = "negative"

            ##collision energy
            if item_name == 'collision energy': collision_energy = str(item_value)

            ##mz_error
            if item_name == 'mass error': mz_error = float(item_value)

            ##instrument
            if item["name"] == 'instrument': instrument = item["value"]

            ##precursor_mz
            if item_name == 'precursor m/z': precursor_mz = float(item_value)

            ##precursor_type
            if item_name == 'precursor type': precursor_type = item_value
            
            #instrument
            if item_name == 'instrument': instrument = item_value

            #instrument type
            if item_name == 'instrument type': tool_type = item_value

            ##exact mass
            if item_name == 'exact mass': exact_mass = float(item_value)





        spectrum_data = {
            "msLevel":ms_level,
            "compoundDataId":compound_data_id, 
            "compoundClassificationId":compound_classification_id,
            "precursorMz":precursor_mz,
            "collisionEnergy":collision_energy,
            "mzError":mz_error,
            "dataSource":data_source,
            "toolType":tool_type,
            "ionMode":ion_mode,
            "instrument":instrument,
            "exactMass":exact_mass,
            "precursorType":precursor_type,
            "ms2Spectrum":monadata_each_object["spectrum"]
        }
                    # ms_level=ms_level,
                    # compound_data_id=compound_data_id,
                    # compound_classification_id=compound_classification_id,
                    # precursor_mz=precursor_mz,
                    # collision_energy=collision_energy,
                    # mz_error=mz_error,
                    # data_source=data_source,
                    # tool_type=tool_type,
                    # ion_mode=ion_mode,
                    # instrument=instrument,
                    # exact_mass=exact_mass,
                    # precursor_type=precursor_type,
                    # ms2_spectrum = monadata_each_object["spectrum"]
            
    except Exception as e:
        print(e)
        print("error")
    
    return spectrum_data

def prepare_compound_data(compound_data_object,compound_classification_id:int=None) -> object:
    name=None
    inchi_key=None
    inchi=None
    formula=None
    smile=None
    cas=None
    exact_mass=None
    mole_file=None
    kind=None
    compound_arr = compound_data_object["compound"]
    if len(compound_arr) > 1:
        print("error")
        print("compound arr length > 1")
    compound_object = compound_arr[0]
    kind = compound_object["kind"]
    mole_file = compound_object["molFile"] 
    
    ##get compound name
    name_arr =[]
    for item in compound_object["names"]:
        name_arr.append(item["name"])
    name = str(name_arr)

    ##get inchey key, formula, smile, cas, exact_mass, pubchem_id
    for item in compound_object["metaData"]:
        item_name = item["name"]
        item_value = item["value"]
        if item_name == "InChIKey": inchi_key = str(item_value)
        if item_name == "InChI": inchi = str(item_value)
        if item_name == "molecular formula": formula = str(item_value)
        if item_name == "SMILES": smile = str(item_value)
        if item_name == "cas": cas = str(item_value)
        if item_name == "total exact mass": exact_mass = float(item_value)

    compound_data = {
        "compoundClassificationId":compound_classification_id,
        "name":name,
        "inChiKey":inchi_key,
        "inChi":inchi,
        "formula":formula,
        "smile":smile,
        "cas":cas,
        "exactMass":exact_mass,
        "molFile":mole_file,
        "kind":kind
    }
    
    return compound_data

def prepare_compound_classification(compound_data_object) -> object:
    classification_kingdom=None
    classification_superclass=None
    classification_class=None
    classification_subclas=None
    classification_direct_parent=None

    ##get classification arr 
    compound_arr = compound_data_object["compound"]
    if len(compound_arr) > 1:
        print("error")
        print("compound arr length > 1")
    
    classification_arr = compound_arr[0]["classification"]

    for item in classification_arr:
        item_name = item["name"]
        item_value = item["value"]
        if item_name == "kingdom": classification_kingdom = str(item_value)
        if item_name == "superclass": classification_superclass = str(item_value)
        if item_name == "class": classification_class = str(item_value)
        if item_name == "subclass": classification_subclas = str(item_value)
        if item_name == "direct parent": classification_direct_parent = str(item_value)

    compound_classification = {
        "classificationKingdom":classification_kingdom,
        "classificationSuperclass":classification_superclass,
        "classificationClass":classification_class,
        "classificationSubclass":classification_subclas,
        "classificationDirectParent":classification_direct_parent

    }
    return compound_classification
    


def insert_mona_raw_data_into_db(file_url:str):
    '''This function is used to insert mona raw data into db by fetech java spring boot server
        currently, we only insert compound_classification, compound_data, spectrum_data table
        table name: compound_classification -> compound_data -> spectrum_data
    '''
    import json
    import requests
    spectrum_data_url = "http://127.0.0.1:8080/api/spectrum"
    compound_data_url = "http://127.0.0.1:8080/api/compound/compoundData"
    compound_classification_url = "http://127.0.0.1:8080/api/compound/compoundClassification"
    #read json in disk mode
    ##file_url = "./data/test.json"
    with open(file_url, "r") as f:
        monadata_each_object = json.load(f)
        headers = {'Content-type': 'application/json'}
        for item in monadata_each_object:
            spectrum_data = None
            compound_data = None
            current_compound_data_id =0
            compound_classification = None
            current_compound_classification_id =0
            

            ##嘗試插入compound_classification 進入 database, 如有重複則跳過
            try:
                compound_classification = prepare_compound_classification(item)
                ##直接查詢是否有重複的compound_classification
                params = {
                    "classificationDirectParent":compound_classification["classificationDirectParent"]
                }
                response = requests.get(compound_classification_url, params=params)
                response_json = json.loads(response.text)
                
                if len(response_json) > 0:
                    ##database exist, get id
                    current_compound_classification_id = response_json[0]["id"]
                else:
                    post_results= requests.post(url=compound_classification_url, data=json.dumps(compound_classification), headers=headers)
                    response = requests.get(compound_classification_url, params=params)
                    response_json = json.loads(response.text)
                    current_compound_classification_id = response_json[0]["id"]
                   
            except Exception as e:
                print("error")
                print(e)


            try:
                compound_data = prepare_compound_data(item,current_compound_classification_id)
                params = {
                    "inChiKey":compound_data["inChiKey"]
                }
                response = requests.get(compound_data_url, params=params)
                response_json = json.loads(response.text)
                if len(response_json) > 0:
                    current_compound_data_id = response_json[0]["id"]
                else:
                    post_results= requests.post(url=compound_data_url, data=json.dumps(compound_data), headers=headers)
                    response = requests.get(compound_data_url, params=params)
                    response_json = json.loads(response.text)
                    current_compound_data_id = response_json[0]["id"]
            except Exception as e:
                print("error")
                print(e)


            try:
                spectrum_data = prepare_spectrum_data(
                    monadata_each_object=item,
                    compound_data_id=current_compound_data_id,
                    compound_classification_id=current_compound_classification_id
                )
                post_results= requests.post(url=spectrum_data_url, data=json.dumps(spectrum_data), headers=headers)

            except Exception as e:
                print("error")
                print(e)



    print("total insertion complete, please check database")



    
url = "MoNA-export-LC-MS_Spectra.json"
url = "MoNA-export-GC-MS_Spectra.json"
url = "test.json"




insert_mona_raw_data_into_db(file_url=url)