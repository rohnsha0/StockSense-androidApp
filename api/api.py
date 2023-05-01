import datetime
import yfinance as yf


startDate= datetime.datetime.now() - datetime.timedelta(days=121)
endDate= datetime.datetime.now() - datetime.timedelta(days=61)

symbol = '^NSEI'
data = yf.download(tickers=symbol, interval="30m", start=startDate, end=endDate)
with open(f'../dataset/{symbol}.csv', mode='a', newline='') as z:
    data.to_csv(z, header=z.tell() == 0)

print(f"Data retrieved & Updated for symbol: {symbol} at {datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
print(data.head(2))
print(data.tail(2))
