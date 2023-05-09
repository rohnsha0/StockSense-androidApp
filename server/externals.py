import yfinance as yf
import pandas as pd
import plotly.graph_objects as go

df = pd.read_csv('../dataset/ITC.NS-train.csv')

fig = go.Figure(data=[go.Candlestick(x=df.index,
                                     open=df['Open'],
                                     high=df['High'],
                                     low=df['Low'],
                                     close=df['Close'])])

fig.update_layout(title=f'ITC.NS Candlestick Chart (n-times)',
                  xaxis_title='Date',
                  yaxis_title='Price')

fig.show()

