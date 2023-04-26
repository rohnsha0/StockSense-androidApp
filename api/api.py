import datetime
import yfinance as yf

symbol = '^NSEI'
data = yf.download(tickers=symbol, interval="30m", period="60d")
data.to_csv(f'../dataset/{symbol}.csv')

print(f"Data retrieved & Updated for symbol: {symbol} at {datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
