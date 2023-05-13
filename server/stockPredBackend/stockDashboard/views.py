from django.shortcuts import render
import yfinance as yf
from . import externals, utils
import datetime
import json
from django.http import JsonResponse
import plotly.graph_objects as go


def home(request):
    return render(request, 'stockDashboard/app.html')


def dashboard(request):
    inputData = request.POST['input']
    request.session['inputData'] = inputData
    ticker = yf.Ticker(f'{inputData}')
    scriptName, scriptSector = externals.stockName(inputData)

    # api calls
    ticker1d= round(ticker.history(period='1d'), 2)
    ticker1d1m= round(ticker.history(period='1d', interval='1m'), 2)
    tickerInfo= ticker.info
    tickerHist1y= round(ticker.history(period='1y'), 2)

    # contexts JSON
    context = {
        'scriptName': scriptName,
        'scriptSector': scriptSector,
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
    }
    return render(request, 'stockDashboard/dashboard.html', context)

'''
def chartIntra(request):
    symbol=request.session.get('inputData')
    #symbolJSON=symbol.json()
    data = yf.download(symbol, period='1d', interval='1m')
    fig = go.Figure(data=[go.Scatter(x=data.index, y=data['Close'], fill='tozeroy')])
    fig.update_layout(title="Stock Price for {}".format(symbol), xaxis_title="Date", yaxis_title="Price")
    chartData = fig.to_json()
    return JsonResponse(chartData)
'''
