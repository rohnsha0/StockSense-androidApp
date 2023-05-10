import yfinance as yf
import pandas as pd
import plotly.graph_objects as go


def chartView(symbol):
    df = pd.read_csv(f'../dataset/{symbol}-train.csv')
    fig = go.Figure(data=[go.Candlestick(x=df.index,
                                         open=df['Open'],
                                         high=df['High'],
                                         low=df['Low'],
                                         close=df['Close'])])
    fig.update_layout(title=f'ITC.NS Candlestick Chart (n-times)',
                      xaxis_title='Date',
                      yaxis_title='Price')
    return fig.show()


def stockName(symbol):
    tickerINFO = yf.Ticker(symbol).info
    scriptName = tickerINFO['longName']
    scriptSector = tickerINFO['sector']
    return scriptName, scriptSector


symbo = 'ITC.NS'

stockName(symbo)
