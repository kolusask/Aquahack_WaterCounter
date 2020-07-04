import requests
import string
import os
from datetime import date,datetime

# Connecting to OCR server
def ocr_space_file(filename, api_key='07cf5a4f8488957'):
    #Parameters of OCR
    payload = {
               'apikey': api_key,
               'OCREngine':'2',
               }
    with open(filename, 'rb') as f:
        r = requests.post('https://api.ocr.space/parse/image',
                          files={filename: f},
                          data=payload,
                          )

    #Formatting the obtained data
    data1=r.json()
    al=(data1["ParsedResults"][0]["ParsedText"])
    letter_remove=[]
    a=al.split()
    formatted_list = []

    #Using simple filter for wrong recognition of numbers
    for i in a:
        mesi=i.replace("O","0")
        fin_l=mesi.replace("o","0")
        formatted_list.append(fin_l)

    # adding only digits to the list
    for i in range (len(formatted_list)-1):
        if (formatted_list[i][0]=='0') :
            letter_remove.append(formatted_list[i])


    second_filter = []

    #double check for the validity of all inputs of numbers
    for i in letter_remove:
        if (i.isnumeric() == True):
            second_filter.append(i)

    fin_list=[]
    # Seperating numbers just in case they are recognized as together
    # (such as 7 and 5 on counter may be recognized as 75)
    for i in second_filter:
        if (len(i)>1):
            for j in i:
                fin_list.append(j)
        else:
            fin_list.append(i)


    #Getting current time and date for sending to the server
    today=date.today()
    timing = datetime.now().time()
    # convert all strings into the integer
    fin_list = [int(i) for i in fin_list]

    #Combining seperate numbers to one integer
    stri=[str(integer) for integer in fin_list]
    res = int("".join(stri))

    #creating dictionary for sending it to the database
    send_dict =  {
       'date':today,
        'time':timing,
        'reading':res
                }
    #Creating request for posting data
    requests.post('https://aquahackwatercounter.azurewebsites.net/api/putNewReading',params=send_dict,headers={"x-functions-key":"CZoQdv4HoXStabracJbjZFH5HmuocietVuyw5vBJTXXpZJDTgbW4VA=="})



    #For Printing final result
    print('Counter Result = ', res)



#######     Function for Raspberry Pi to take a photo

##### for taking photo instantly with Raspberry Pi we uncomment line 87-88 and change filename
##### accordingly while calling function

#while True:
    #os.system('fswebcam -r 640x480 -S 3 --jpeg 50 --save /home/pi/Samples/counter_meter.jpg')

##### for checking photo only from local storage

ocr_space_file(filename='test_photo.jpg')


