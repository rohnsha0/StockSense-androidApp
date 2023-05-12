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
    priceChange = round(ticker.history(period='1d', interval='1m')['Close'][-1]) - round(ticker.history(period='1d', interval='1m')['Close'][0])

    context = {
        'scriptName': scriptName,
        'scriptSector': scriptSector,
        'LTP': round(ticker.history(period='1d', interval='1m')['Close'].iloc[-1], 2),
        'high': round(ticker.history(period='1d')['High'].iloc[-1], 2),
        'low': round(ticker.history(period='1d')['Low'].iloc[-1], 2),
        'updationTime': datetime.datetime.now().strftime('%H:%M:%S'),
        '52wkHigh': round(ticker.history(period='1y')['High'].max(), 2),
        '52wkLow': round(ticker.history(period='1y')['Low'].min(), 2),
        'isin': ticker.isin,
        'PE': round(ticker.info['trailingPE'], 2),
        'netMargin': round(ticker.info['profitMargins'], 2),
        'change': priceChange
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
