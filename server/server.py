import datetime
from flask import Flask, jsonify
import yfinance as yf
import pandas as pd
import json
import requests
from apscheduler.schedulers.background import BackgroundScheduler

app = Flask(__name__)


def dataRetrieval():
    symbol = "ITC.NS"
    data = yf.download(tickers=symbol, interval="30m", period="1d")
    print('Data retrieved at', datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S'))
    return data


scheduler = BackgroundScheduler()
scheduler.add_job(func=dataRetrieval, trigger='interval', minutes=1)
scheduler.start()


@app.route('/')
def welcome():
    df = dataRetrieval()
    df.to_csv('file.csv')
    return 'Welcome'


if __name__ == '__main__':
    print('Starting Flask Server.....')
    app.run()
