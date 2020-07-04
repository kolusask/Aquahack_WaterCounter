import pyodbc
import logging
import azure.functions as func
from datetime import datetime
from dateutil.relativedelta import relativedelta

con = pyodbc.connect("Driver={ODBC Driver 17 for SQL Server};Server=tcp:water-counter-database-server.database.windows.net,1433;Database=water-counter-database;Uid=askar;Pwd=WaterCounter123;Encrypt=yes;TrustServerCertificate=no;Connection Timeout=30;")
cur = con.cursor()

def _execute(req):
    return str(cur.execute(req).fetchone())

def _get_sum(begin, end):
    request = "SELECT SUM(DIFFERENT) FROM water_use WHERE DATE BETWEEN '{}' AND '{}'".format(str(begin), str(end))
    response = _execute(request)
    return response[1:-3]

def main(req: func.HttpRequest) -> func.HttpResponse:
    now = datetime.date(datetime.now())
    months_readings = list()
    begin = now - relativedelta(years=1)
    while begin < now:
        end = begin + relativedelta(months=1)
        months_readings.append(_get_sum(begin, end))
        begin += relativedelta(months=1)

    return func.HttpResponse(' '.join(months_readings))
