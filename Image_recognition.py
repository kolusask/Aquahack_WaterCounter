import requests
import string


 #First version of code, will be updated!


def ocr_space_file(filename, api_key='07cf5a4f8488957'):

    payload = {
               'apikey': api_key,
               'OCREngine':'2',
                'scale': True
               }
    with open(filename, 'rb') as f:
        r = requests.post('https://api.ocr.space/parse/image',
                          files={filename: f},
                          data=payload,
                          )

    data1=r.json()
    al=(data1["ParsedResults"][0]["ParsedText"])

    leg=[]
    zag=[]
    leg.append(al)
    a=al.split()
    for i in range (len(a)-1):
        #print('ici',a[i][0])
        if a[i][0] not in string.ascii_letters :
             zag.append(a[i])

    answer_list = []
    for i in a:
        if (i.isnumeric() == True):
            answer_list.append(i)

    fin_list=[]
    for i in answer_list:

        if (len(i)>1):
            for j in i:
                fin_list.append(j)
        else:
            fin_list.append(i)

    fin_list = [int(i) for i in fin_list]



    print('duzeldilmis - ',zag)
    print('ANswer = ', fin_list)
    print(data1["ParsedResults"][0]["ParsedText"])

# Use examples:


test_file = ocr_space_file(filename='1.jpg')

