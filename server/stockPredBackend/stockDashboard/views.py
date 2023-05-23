import requests
from django.shortcuts import render
import yfinance as yf
import datetime


def home(request):
    return render(request, 'stockDashboard/app.html')


def dashboard(request):
    inputData = request.POST['input']
    ticker = yf.Ticker(inputData)

    # api calls
    ticker1d= round(ticker.history(period='1d'), 2)
    ticker1d1m= round(ticker.history(period='1d', interval='1m'), 2)
    tickerInfo= ticker.info
    tickerHist1y= round(ticker.history(period='1y'), 2)
    print("sending model api request")
    predictionAPI = requests.get(f'http://127.0.0.1:8000/api/query/{inputData}').json()

    # contexts JSON
    context = {
        'scriptName': tickerInfo['longName'],
        'scriptSector': tickerInfo['sector'],
        'LTP': ticker1d1m['Close'].iloc[-1],
        'high': ticker1d['High'].iloc[-1],
        'low': ticker1d['Low'].iloc[-1],
        'updationTime': datetime.datetime.now().strftime('%H:%M:%S'),
        '52wkHigh': tickerHist1y['High'].max(),
        '52wkLow': tickerHist1y['Low'].min(),
        'isin': ticker.isin,
        'PE': round(tickerInfo['trailingPE'], 2),
        'netMargin': round(tickerInfo['profitMargins'], 2),
        'divYield': round(tickerInfo['dividendYield'], 2),
        'marketcap': round((tickerInfo['marketCap']/10000000), 0),
        'beta': round(tickerInfo['beta'], 2),
        'change': round(ticker1d1m['Close'][-1] - ticker1d1m['Close'][0], 2),
        'prediction30M_close': round(predictionAPI['close'], 2)
    }
    return render(request, 'stockDashboard/dashboard.html', context)
