import datetime
import yfinance as yf

startDate = max
endDate = datetime.datetime.now()  - datetime.timedelta(days=90)

symbol = 'M&M.NS'
data = yf.download(tickers=symbol, interval="1d", period='max', end=endDate)
with open(f'../dataset/{symbol}-test.csv', mode='a', newline='') as z:
    data.to_csv(z, header=z.tell() == 0)

print(f"Data retrieved & Updated for symbol: {symbol} at {datetime.datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
print(data.head(2))
print(data.tail(2))
