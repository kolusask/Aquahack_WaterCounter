import logging

import azure.functions as func
import pyodbc


def main(req: func.HttpRequest) -> func.HttpResponse:
    date = req.params.get("date")
    time = req.params.get("time")
    readings = req.params.get("readings")
    difference = req.params.get("difference")

    con = pyodbc.connect("Driver={ODBC Driver 13 for SQL Server};Server=tcp:water-counter-database-server.database.windows.net,1433;Database=water-counter-database;Uid=askar;Pwd=WaterCounter123;Encrypt=yes;TrustServerCertificate=no;Connection Timeout=30;")
    cur = con.cursor()

    request = "" #TODO
    cur.execute(string)
    con.commit()

    return func.HttpResponse("Readings registered successfully")
